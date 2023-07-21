package com.baomidou.springboot.sql;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * @author linzhenlie-jk
 * @date 2023/4/10
 */
@Slf4j
public class HiveSQLTest {
    @Test
    public void drop() {
        log.info("1=========");
        String sql = "DROP TABLE fin_dw.dwd_trade_loan_pda;";
        //HiveSqlUtil.parser(sql);
        HiveSqlUtil.parser1(sql);

        log.info("2=========");
        String correctSql = "DROP TABLE IF EXISTS fin_dw.dwd_trade_loan_pda;";
        //HiveSqlUtil.parser(correctSql);
        //HiveSqlUtil.parser1(correctSql);

        log.info("3=========");
        String correctSql2 = "INSERT OVERWRITE TABLE dwd_tmp.test " +
            " SELECT * FROM (" +
            " select distinct loan_no,user_name FROM A a where a.id = 1 AND date(a.time_submit)>'11'" +
            " UNION ALL " +
            " select distinct loan_no,user_name FROM A a where a.id = 2 AND a.time>'22'" +
            " )b WHERE b.id = 3;";
        //HiveSqlUtil.parser(correctSql2);
        //HiveSqlUtil.parser1(correctSql2);
    }

    @Test
    public void creat() {
        String sql =
            "CREATE TABLE fin_dw.dwd_trade_loan_pda ( \n" +
                "     user_no                 string         COMMENT  '用户号' \n" +
                "    ,cust_no                 string         COMMENT  '' \n" +
                "    ,term                    INT            COMMENT  '期数' \n" +
                "    ,bank_code               string         COMMENT  '银行卡编号' \n" +
                "    ,loan_channel_id         string         COMMENT  '资金动用渠道' \n" +
                "    ,rpy_type                string         COMMENT  '还款方式:00-等额本金,01-等额本息,02-先息后本,03等本等息(商城固定)' \n" +
                "    ,date_compensate         string         COMMENT  '代偿日期yyyy-MM-dd' \n" +
                "    ,compensate_type         string         COMMENT  '代偿类型,NCP未代偿,ECP提前代偿,OCP逾期代偿' \n" +
                "    ,over_due_days           INT            COMMENT  '系统逾期天数(对应over_due_status)' \n" +
                "    ,calc_over_due_days      INT            COMMENT  '逻辑逾期天数(昨日未还over_due_days+1天)'\n" +
                "    ,over_due_status         string         COMMENT  '拖欠周期状态码(对应over_due_days)' \n" +
                ") \n" +
                "COMMENT '' \n" +
                "PARTITIONED BY ( \n" +
                "      pday        string COMMENT  '' \n" +
                "     ,source_type string COMMENT  '数据源类型:JT借条,WLH微零花,MALL商城' \n" +
                ") USING TEXT \n";
        //HiveSqlUtil.parser(sql);
        HiveSqlUtil.parser1(sql);
    }

    @Test
    public void alter() {
        String sql = "ALTER TABLE fin_dw.dwd_trade_loan_pda RENAME TO fin_dw.dwd_trade_loan_pda2;";
        HiveSqlUtil.parser(sql);

        String sql1 = "ALTER TABLE fin_dw.dwd_trade_loan_ice_static_pdi CHANGE COLUMN year_rate year_rate DECIMAL(17,9)" +
            " COMMENT '';";
        HiveSqlUtil.parser(sql1);

        String sql2 = "ALTER TABLE fin_dw.dwd_trade_loan_pda ADD COLUMNS(" +
            "        is_due_mob37_01         string         COMMENT  '达mob37且1天观察日'" +
            "        ,over_due_days_mob37_01 string         COMMENT  ''" +
            "        ,act_prin_amt_mob37_01  decimal(17,2)  COMMENT  '达mob37且1天观察日实还本金'" +
            "        ,is_due_mob37_04        string         COMMENT  '达mob37且4天观察日'" +
            "    );";

        HiveSqlUtil.parser(sql2);

        String sql3 = "ALTER TABLE fin_dw.dwd_trade_loan_pda REPLACE COLUMNS(" +
            "        is_due_mob37_01        string         COMMENT  '达mob37且1天观察日'" +
            "        ,over_due_days_mob37_01 string         COMMENT  '达mob37且1天观察日逾期天数'" +
            "        ,act_prin_amt_mob37_01  decimal(17,2) COMMENT  ''" +
            "        ,is_due_mob37_04        string         COMMENT  '达mob37且4天观察日'" +
            "    );";
        HiveSqlUtil.parser(sql3);
    }

    @Test
    public void create2() {
        String sql = "CREATE TABLE IF NOT EXISTS dwd_tmp.test USING TEXT AS \n"
            + "SELECT  loan_no"
            + "       ,IF(t0.settle_days IS NOT NULL, t1.settle_term, NULL) AS settle_term\n"
            + "       ,t1.date_settle_days_last\n"
            + "       ,COUNT(distinct loan_no) AS loan_cnt\n"
            + "       ,COALESCE(t2.over_due_days_head_3terms_max ,0)\n"
            + "       ,t3.date_tran_last\n"
            + "       ,GET_JSON_OBJECT(outputjson, '$.score') AS score\n"
            + "       ,get_json_object(outputjson, '$.val') AS val\n"
            + "       ,'${last2Days_p}'                                   AS pday\n"
            + "       ,t0.source_type   AS source_type\n"
            + "FROM dwd_tmp.tmp00_his AS t0\n"
            + "LEFT JOIN dwd_tmp.tmp01_his AS t1\n"
            + "ON t0.loan_no = t1.loan_no \n"
            + "LEFT JOIN dwd_tmp.tmp02_his AS t2\n"
            + "ON t0.loan_no = t2.loan_no AND 1=1 \n"  // 出现1=1
            + "LEFT JOIN dwd_tmp.tmp03_his AS t3\n"
            + "ON t2.loan_no = t3.loan_no\n"
            + "LEFT JOIN dwd_tmp.tmp04_his AS t4\n"
            + "ON t2.loan_no = t4.loan_no\n"
            + "RIGHT JOIN dwd_tmp.tmp05_his AS t5\n"
            + "ON t5.loan_no = t4.loan_no;\n";
        HiveSqlUtil.parser1(sql);
    }

    @Test
    public void create3() {
        // 禁止 1=1 ，禁止使用 select * FROM 表
        // right join改为使用left join 更符合阅读及使用习惯
        String sql = "CREATE TABLE IF NOT EXISTS dwd_tmp.test USING TEXT AS \n"
            + "SELECT  *,loan_no AS loan_no1,t1.*,count(distinct *)\n"
            + ",IF(COUNT(distinct *) OVER(PARTITION BY cust_no ORDER BY date_created) = loan_no,'Y'," +
            "'N') AS first_loan\n"
            + "FROM dwd_tmp.tmp00_his\n"
            + "LEFT JOIN dwd_tmp.tmp01_his AS t1\n"
            + "ON t0.loan_no = t1.loan_no AND '1'='1'\n";
        HiveSqlUtil.parser1(sql);

        String sql1 = "CREATE TABLE IF NOT EXISTS dwd_tmp.test USING TEXT AS \n"
            + "SELECT  *\n"
            + "FROM dwd_tmp.tmp00_his AS t0 where t0.id in (select id FROM dwd_tmp.tmp01_his)\n";
        //HiveSqlUtil.parser(sql1);

        String sql2 = "CREATE TABLE IF NOT EXISTS dwd_tmp.test USING TEXT AS \n"
            + "SELECT  t.id AS id2\n"
            + "FROM (select * FROM (select * FROM dwd_tmp.tmp01_his where 1=1) AS a1  where 2=2) AS a2 where a2.id=1\n";
        //HiveSqlUtil.parser(sql2);
    }

    @Test
    public void insert1() {
        // 没有别名，join几次，就有几个on条件，主表出现的次数不等于join个数，可能出现倾斜
        // 多次使用get_json_object，改用json_purple
        // join次数大于10，join条件疑似数据倾斜(非主表条件)
        String sql = "INSERT OVERWRITE TABLE dwd_tmp.test partition(pday,source_type)\n"
            + "SELECT  loan_no,IF(t0.settle_days IS NOT NULL, t1.settle_term, NULL) AS settle_term\n"
            + "       ,t1.date_settle_days_last\n"
            + "       ,COUNT(distinct loan_no) AS loan_cnt\n"
            + "       ,COALESCE(t2.over_due_days_head_3terms_max ,0)\n"
            + "       ,t3.date_tran_last\n"
            + "       ,GET_JSON_OBJECT(outputjson, '$.score') AS score\n"
            + "       ,get_json_object(outputjson, '$.val') AS val\n"
            + "       ,'${last2Days_p}'                                   AS pday\n"
            + "       ,t0.source_type   AS source_type\n"
            + "FROM dwd_tmp.tmp00_his AS t0\n"
            + "LEFT JOIN dwd_tmp.tmp01_his AS t1\n"
            + "ON t0.loan_no = t1.loan_no\n"
            + "LEFT JOIN dwd_tmp.tmp02_his AS t2\n"
            + "ON t0.loan_no = t2.loan_no AND 1=1\n"
            + "LEFT JOIN dwd_tmp.tmp03_his AS t3\n"
            + "ON t2.loan_no = t3.loan_no\n"
            + "LEFT JOIN dwd_tmp.tmp04_his AS t4\n"
            + "ON t2.loan_no = t4.loan_no;\n";
        HiveSqlUtil.parser(sql);
    }

    @Test
    public void crossJoin() {
        // cross join / full join
    }

    // 嵌套式子查询join
    // WITH{
    //  ****
    // }

    @Test
    public void csvRead() throws IOException {
        File file = new File("D:\\workspaces\\dw\\dw-workflow\\public_cp_sc_offline_dw_safe3");
        Iterator<File> iterator = FileUtils.iterateFiles(file, new String[] {"sql"}, Boolean.TRUE);
        while (iterator.hasNext()) {
            File file1 = iterator.next();
            try {
                HiveSqlUtil.parser1(FileUtils.readFileToString(file1, StandardCharsets.UTF_8));
            } catch (Exception e) {
                log.error("异常文件:{}", file1.getName());
            }
        }
    }

    @Test
    public void csvRead2() throws IOException {
        File fileDir = new File("D:\\");
        Iterator<File> iterator = FileUtils.iterateFiles(fileDir, new String[] {"csv"}, Boolean.FALSE);
        File file2 = null;
        CsvReader csvReader = new CsvReader();
        while (iterator.hasNext()) {
            File file = iterator.next();
            file2 = new File(fileDir,file.getName()+".txt");
            try (CsvParser csvParser = csvReader.parse(file, StandardCharsets.UTF_8)) {
                CsvRow row = csvParser.nextRow();
                while ((row = csvParser.nextRow()) != null) {
                    String workflow_id = row.getField(0);
                    String workflow_name = row.getField(1);
                    String task_name = row.getField(2);
                    String node_sql = row.getField(4);
                    /*if (!node_sql.toLowerCase().contains("prd_lps_ap_appl_btl_relation")
                    ) {
                        continue;
                    }*/
                /*CntVO cntVO = HiveSqlUtil.parser(node_sql);
                List<String> list = cntVO.getTableList().stream().map(sqlExprTableSource -> {
                    return workflow_id + "," + sqlExprTableSource.getExpr();
                }).collect(Collectors.toList());*/
                    //HiveSqlUtil.parser(node_sql);
                    FileUtils.writeLines(file2, Arrays.asList(node_sql), true);
                }
            }
        }
    }

    /**
     * 文件名与insert名称不对应
     */
    @Test
    public void fileNameCompareInsertName() throws IOException {
        File fileDir = new File("D:\\workspaces\\dw\\dw-workflow\\public_cp_sc_offline_dw_zp");
        File targetFile  = new File("D:\\cp.txt");
        Iterator<File> iterator = FileUtils.iterateFiles(fileDir, new String[] {"sql"}, Boolean.TRUE);
        while (iterator.hasNext()) {
            File file = iterator.next();
            if (StringUtils.equalsAny(file.getName(),
                "010_dim_a_loan_req_no_trade_draw_risk_j_pdi.sql"
                ,"052_dwd_user_scan_btl_scan_pda.sql"
                ,"200_dim_z_appl_no_zzl_pure_book_submit_j_a.sql"
                ,"226_dim_z_staff_no_wj_ditui_bp_info_20200728_a.sql"
                ,"248_dim_p_appl_req_no_credit_record_jwm_a.sql"
                ,"262_dim_p_user_bairong_user_detail_offline_crs_all_pdi.sql"
                ,"304_dim_a_risk_level_mapping_s.sql"
                ,"305_dwd_btl_event_mic_market_app_pdi.sql"
                ,"002_ads_p_u_behavior_record_trade_statistics_pdi.sql"
                ,"040_dwt_user_no_bairong_detail_pdi.sql"
                ,"036_dwt_app_req_no_apply_info_pda.sql"
                ,"111_dim_z_user_no_btl_quality_user_info_j_a.sql"
                ,"dim_third_code.sql","备忘.sql","dwd_btl_order_pdi.sql"
                ,"dwd_credit_apply_ice_a.sql"
                ,"dwd_trade_trial_behavior_record_pdi.sql"
                ,"dwd_t_loan_order_log.sql"
                ,"dwd_t_loan_data.sql")){
                continue;
            }

            String sql = FileUtils.readFileToString(file,StandardCharsets.UTF_8);
            HiveStatementParser parser = new HiveStatementParser(sql);
            List<SQLStatement> stmtList = parser.parseStatementList();
            StringBuilder sb = new StringBuilder();
            Set<String> tableName = new HashSet<>();
            for (SQLStatement stmt : stmtList) {
                if (stmt instanceof HiveInsertStatement) {
                    HiveInsertStatement insert =  (HiveInsertStatement)stmt;
                    tableName.add(insert.getTableName().getSimpleName());
                }
            }
            sb.append(",").append(tableName.toString().replace("[","").replace("]",""));
            sb.deleteCharAt(0).insert(0,"|").insert(0,file.getName()).append(System.lineSeparator());
            FileUtils.writeStringToFile(targetFile, sb.toString(), Boolean.TRUE);
        }
    }

    @Test
    public void dwtFromOnlyTable() throws IOException {
        File fileDir = new File("D:\\workspaces\\dw\\dw-workflow\\public_cp_sc_offline_dw_zp");
        Iterator<File> iterator = FileUtils.iterateFiles(fileDir, new String[] {"sql"}, Boolean.TRUE);
        File targetFile  = new File("D:\\cp1.txt");
        while (iterator.hasNext()) {
            File file = iterator.next();
            if (file.getName().indexOf("dwt_")<0){
                continue;
            }
            if (StringUtils.equalsAny(file.getName(),
                "010_dim_a_loan_req_no_trade_draw_risk_j_pdi.sql"
                ,"052_dwd_user_scan_btl_scan_pda.sql"
                ,"200_dim_z_appl_no_zzl_pure_book_submit_j_a.sql"
                ,"226_dim_z_staff_no_wj_ditui_bp_info_20200728_a.sql"
                ,"248_dim_p_appl_req_no_credit_record_jwm_a.sql"
                ,"262_dim_p_user_bairong_user_detail_offline_crs_all_pdi.sql"
                ,"304_dim_a_risk_level_mapping_s.sql"
                ,"305_dwd_btl_event_mic_market_app_pdi.sql"
                ,"002_ads_p_u_behavior_record_trade_statistics_pdi.sql"
                ,"040_dwt_user_no_bairong_detail_pdi.sql"
                ,"036_dwt_app_req_no_apply_info_pda.sql"
                ,"111_dim_z_user_no_btl_quality_user_info_j_a.sql"
                ,"dim_third_code.sql","备忘.sql","dwd_btl_order_pdi.sql"
                ,"dwd_credit_apply_ice_a.sql"
                ,"dwd_trade_trial_behavior_record_pdi.sql"
                ,"dwd_t_loan_order_log.sql"
                ,"dwd_t_loan_data.sql")){
                continue;
            }
            String sql = FileUtils.readFileToString(file,StandardCharsets.UTF_8);
            HiveStatementParser parser = new HiveStatementParser(sql);
            List<SQLStatement> stmtList = parser.parseStatementList();
            HiveRuleCheckVisitor visitor = new HiveRuleCheckVisitor();
            List<SQLExprTableSource> tableList = new ArrayList<>();
            stmtList.forEach(sqlStatement -> {
                 sqlStatement.accept(visitor);
                tableList.addAll(visitor.getTableList());
            });

            Set<String> set =
                tableList.stream().filter(tableSource -> tableSource.getSchema()!="dwd_tmp").map(tableSource ->
                tableSource.getSchema()+"."+tableSource.getTableName()).collect(Collectors.toSet());

            FileUtils.writeStringToFile(targetFile, String.format("%s:%s",file.getName(),set), Boolean.TRUE);
            FileUtils.writeStringToFile(targetFile, System.lineSeparator(), Boolean.TRUE);
        }
    }
}
