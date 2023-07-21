package com.baomidou.springboot.sql;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLIndexDefinition;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author linzhenlie-jk
 * @date 2023/6/12
 */
public class MySqlToHiveOutputVisitor extends MySqlASTVisitorAdapter {

    private StringBuilder createColumnContent = new StringBuilder();

    private StringBuilder selectColumnContent = new StringBuilder();

    private Map<String, StringBuilder> idxNum = new HashMap<>();

    private List<String> columnIdx = new ArrayList<>();

    private Integer uniTmp = 1;

    private String formatePrex = "%";

    private File file = null;

    public MySqlToHiveOutputVisitor() {
    }

    public MySqlToHiveOutputVisitor(File file) {
        this.file = file;
    }

    private String replaceChar(String str) {
        return str.trim().replaceAll("`", "")
            .replaceAll(" ", "")
            .replaceAll("：", ":")
            .replaceAll(";", "；")
            .replaceAll("（", "(")
            .replaceAll("）", ")");
    }


    public void endVisit(SQLColumnDefinition columnDefinition) {
        String unionType = null;
        String columnName = replaceChar(columnDefinition.getColumnName());
        String columnNameNew = columnName;
        String dataType = columnDefinition.getDataType().getName();

        String hiveDataType = "STRING";
        switch (dataType.toUpperCase()) {
            case SQLDataType.Constants.CHAR:
            case SQLDataType.Constants.NCHAR:
            case SQLDataType.Constants.VARCHAR:
            case SQLDataType.Constants.VARBINARY:
            case SQLDataType.Constants.TEXT:
            case SQLDataType.Constants.BYTEA:
                unionType = "STRING";
                break;
            case "DATETIME":
            case SQLDataType.Constants.TIMESTAMP:
                unionType = SQLDataType.Constants.TIMESTAMP;
                if (columnName.endsWith("_time")) {
                    columnNameNew = "time_" + columnName.replaceAll("_time", "");
                } else if ((columnName.startsWith("date_") || columnName.endsWith("_date"))
                    && !StringUtils.equalsAny(columnName, "date_created", "date_updated")) {
                    columnNameNew = "time_" +
                        columnName.replaceAll("date_", "")
                            .replaceAll("_date", "");
                }
                break;
            case SQLDataType.Constants.DATE:
                unionType = SQLDataType.Constants.DATE;
                if (columnName.endsWith("_time")) {
                    columnNameNew = "date_" + columnName.replaceAll("_time", "");
                } else if (columnName.endsWith("_date")) {
                    columnNameNew = "date_" + columnName.replaceAll("_date", "");
                }
                break;
            case SQLDataType.Constants.TINYINT:
            case SQLDataType.Constants.SMALLINT:
            case SQLDataType.Constants.INT:
            case SQLDataType.Constants.BIGINT:
                unionType = SQLDataType.Constants.BIGINT;
                hiveDataType = SQLDataType.Constants.BIGINT;
                break;
            case SQLDataType.Constants.DOUBLE:
            case SQLDataType.Constants.FLOAT:
            case SQLDataType.Constants.DOUBLE_PRECISION:
            case SQLDataType.Constants.REAL:
            case SQLDataType.Constants.DECIMAL:
            case SQLDataType.Constants.BOOLEAN:
                unionType = SQLDataType.Constants.DECIMAL;
                if (columnName.contains("amt")) {
                    hiveDataType = "DECIMAL(17,2)";
                } else {
                    hiveDataType = "DECIMAL(12,8)";
                }
                break;
        }

        createColumnContent.append(",").append(columnNameNew).append("  ");
        createColumnContent.append(hiveDataType);

        SQLCharExpr commentExpr = (SQLCharExpr) columnDefinition.getComment();
        String comment = replaceChar(commentExpr == null ? StringUtils.EMPTY : commentExpr.getText());
        createColumnContent.append(" COMMENT '").append(comment);
        if (unionType == SQLDataType.Constants.DATE) {
            createColumnContent.append("yyyy-MM-dd");
        } else if (unionType == SQLDataType.Constants.TIMESTAMP) {
            createColumnContent.append("yyyy-MM-dd HH:mm:ss");
        }

        createColumnContent.append(formatePrex + columnName).append("'").append(System.lineSeparator());
        columnIdx.add(columnName);

        selectColumnContent.append(",");
        SQLExpr defaultExpr = columnDefinition.getDefaultExpr();
        // 有默认值，则一定是NOT NULL
        String kuhaoAS = " AS ";
        if (Objects.nonNull(defaultExpr) && defaultExpr instanceof SQLCharExpr) {
            SQLCharExpr sqlCharExpr = (SQLCharExpr) defaultExpr;
            // 默认为空值的,才进行判断
            if (StringUtils.isBlank(sqlCharExpr.getText())) {
                selectColumnContent.append("IF(").append(columnName).append(" = '',NULL,");
                kuhaoAS = ") AS ";
            }
        }

        if (SQLDataType.Constants.TIMESTAMP.equals(unionType)) {
            selectColumnContent.append("SUBSTR(").append(columnName).append(",1,19)").append(kuhaoAS);
        } else if (SQLDataType.Constants.DATE.equals(unionType)) {
            selectColumnContent.append("SUBSTR(").append(columnName).append(",1,10)").append(kuhaoAS);
        } else if (SQLDataType.Constants.DECIMAL.equals(unionType)) {
            selectColumnContent.append("COALESCE(").append(columnName).append(",0)").append(kuhaoAS);
        } else if (StringUtils.equalsAny(kuhaoAS, ") AS ")) {
            selectColumnContent.append(columnName).append(kuhaoAS);
        }

        selectColumnContent.append(columnNameNew);
        selectColumnContent.append(System.lineSeparator());
    }

    public void endVisit(MySqlUnique mySqlUnique) {
        SQLIndexDefinition idxDefinition = mySqlUnique.getIndexDefinition();
        idxDefinition.getColumns().stream().forEach(sqlSelectOrderByItem -> {
            SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) sqlSelectOrderByItem.getExpr();
            idxNum.computeIfAbsent(replaceChar(identifierExpr.getName()),
                s -> new StringBuilder("唯一索引")).append(uniTmp).append(",");
        });

        uniTmp++;
    }

    public void endVisit(MySqlPrimaryKey primaryKey) {
        SQLIndexDefinition idxDefinition = primaryKey.getIndexDefinition();
        idxDefinition.getColumns().stream().forEach(sqlSelectOrderByItem -> {
            SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) sqlSelectOrderByItem.getExpr();
            idxNum.computeIfAbsent(replaceChar(identifierExpr.getName()),
                s -> new StringBuilder("主键")).append(uniTmp).append(",");
        });

        uniTmp++;
    }

    public void endVisit(MySqlCreateTableStatement createTable) {
        String tableName = replaceChar(createTable.getTableName());
        SQLCharExpr sqlCharExpr = (SQLCharExpr) createTable.getComment();

        StringBuilder createContent = new StringBuilder();
        createContent.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" ( ")
            .append(createColumnContent.deleteCharAt(0))
            .append(") COMMENT '")
            .append(replaceChar(sqlCharExpr.getText()))
            .append("'")
            .append(System.lineSeparator())
            .append("PARTITIONED BY (pday STRING COMMENT '按日分区')")
            .append(System.lineSeparator())
            .append("STORED AS ORC;");

        String createColumn = createContent.toString();
        for (int index = 0, length = columnIdx.size(); index < length; index++) {
            String columnName = columnIdx.get(index);
            StringBuilder columIdxBuilder = idxNum.get(columnName);
            String comment = "";
            if (Objects.nonNull(columIdxBuilder)) {
                comment = columIdxBuilder.reverse().deleteCharAt(0).reverse().insert(0, "(").append(")").toString();
            }

            createColumn = createColumn.replaceAll(formatePrex + columnName, comment);
        }

        StringBuilder selectContent = new StringBuilder();
        selectContent.append(" SELECT ")
            .append(selectColumnContent.deleteCharAt(0))
            .append(" FROM ")
            .append(tableName).append(";");
        final SQLUtils.FormatOption formatOption = new SQLUtils.FormatOption(VisitorFeature.OutputUCase, VisitorFeature.OutputPrettyFormat);
        if (Objects.nonNull(file)) {
            try {
                FileUtils.writeStringToFile(file, SQLUtils.formatHive(createColumn, formatOption), StandardCharsets.UTF_8, Boolean.TRUE);
                FileUtils.writeStringToFile(file, System.lineSeparator(), StandardCharsets.UTF_8, Boolean.TRUE);
                FileUtils.writeStringToFile(file, SQLUtils.formatHive(selectContent.toString(), formatOption), StandardCharsets.UTF_8, Boolean.TRUE);
                FileUtils.writeStringToFile(file, System.lineSeparator(), StandardCharsets.UTF_8, Boolean.TRUE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println(SQLUtils.formatHive(createColumn, formatOption));
            System.out.println();
            System.out.println(SQLUtils.formatHive(selectContent.toString(), formatOption));
        }

        createColumnContent = new StringBuilder();
        selectColumnContent = new StringBuilder();
        idxNum = new HashMap<>();
        columnIdx = new ArrayList<>();
        uniTmp = 1;
    }
}
