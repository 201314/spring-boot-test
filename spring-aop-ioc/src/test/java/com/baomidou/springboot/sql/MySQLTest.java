package com.baomidou.springboot.sql;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.alibaba.druid.sql.ast.SQLIndexDefinition;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * @author linzhenlie-jk
 * @date 2023/5/26
 */
@Slf4j
public class MySQLTest {
    @Test
    public void uniquePk() {
        String sql = "CREATE TABLE `ln_loan_extension_record` (\n" +
            "    `id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '物理主键',\n" +
            "    `request_no` varchar(80) NOT NULL DEFAULT '' COMMENT '其他系统请求流水号',\n" +
            "    `applicant` varchar(20) NOT NULL DEFAULT '' COMMENT '申请系统，如客服KF，催收CS',\n" +
            "    `cust_no` varchar(64) NOT NULL DEFAULT '' COMMENT '客户号',\n" +
            "    `contract_no` varchar(64) NOT NULL DEFAULT '' COMMENT '合同号',\n" +
            "    `loan_no` varchar(64) NOT NULL DEFAULT '' COMMENT '借据号',\n" +
            "    `tr_proc_rp_no` varchar(64) DEFAULT NULL COMMENT '展期关联入账减免流水',\n" +
            "    `before_loan_amt` decimal(17, 2) NOT NULL DEFAULT '0.00' COMMENT '原始借款本金',\n" +
            "    `after_loan_amt` decimal(17, 2) NOT NULL DEFAULT '0.00' COMMENT '展期后借款本金',\n" +
            "    `before_term` int(10) unsigned DEFAULT NULL COMMENT '原始借款期数',\n" +
            "    `after_term` int(10) unsigned DEFAULT NULL COMMENT '展期后借款期数',\n" +
            "    `start_term` int(10) unsigned DEFAULT NULL COMMENT '展期当时期数',\n" +
            "    `apply_date` date DEFAULT NULL COMMENT '展期申请日期，客服申请日期',\n" +
            "    `valid_date` date DEFAULT NULL COMMENT '展期截止日期，如多少天内可申请展期',\n" +
            "    `extension_date` datetime DEFAULT NULL COMMENT '展期操作日期，系统延期成功日期',\n" +
            "    `product_code` varchar(50) DEFAULT NULL COMMENT '产品代码',\n" +
            "    `status` varchar(2) NOT NULL DEFAULT '' COMMENT '借据展期状态:00初始化,\n" +
            "02处理中,\n" +
            "03展期成功,\n" +
            "04展期失败,\n" +
            "99系统异常',\n" +
            "    `before_rpy_type` varchar(2) DEFAULT NULL COMMENT '原始还款方式:00-等额本金，01-等额本息，02-先息后本',\n" +
            "    `after_rpy_type` varchar(2) DEFAULT NULL COMMENT '展期后还款方式:00-等额本金，01-等额本息，02-先息后本',\n" +
            "    `before_rpy_date` int(10) unsigned DEFAULT NULL COMMENT '原始还款日',\n" +
            "    `after_rpy_date` int(10) unsigned DEFAULT NULL COMMENT '展期后还款日',\n" +
            "    `before_date_end` date DEFAULT NULL COMMENT '原始贷款止期',\n" +
            "    `after_date_end` date DEFAULT NULL COMMENT '原始贷款止期',\n" +
            "    `before_loan_status` varchar(2) DEFAULT NULL COMMENT '原始借据状态:RP-正确,\n" +
            "OD-逾期',\n" +
            "    `over_due_days` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '展期时候客户级别当前逾期天数',\n" +
            "    `max_over_days` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '展期时候客户级别最高逾期天数',\n" +
            "    `over_due_amt` decimal(17, 2) NOT NULL DEFAULT '0.00' COMMENT '展期时候客户级别当前逾期金额',\n" +
            "    `max_over_amt` decimal(17, 2) NOT NULL DEFAULT '0.00' COMMENT '展期时候客户级别最高逾期金额',\n" +
            "    `date_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
            "    `created_by` varchar(100) NOT NULL DEFAULT 'sys' COMMENT '创建人',\n" +
            "    `date_updated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',\n" +
            "    `updated_by` varchar(100) NOT NULL DEFAULT 'sys' COMMENT '修改人',\n" +
            "    PRIMARY KEY (`id`),\n" +
            "    UNIQUE KEY `uniq_re_ap_loan` (`request_no`, `applicant`, `loan_no`)\n" +
            "    USING\n" +
            "        BTREE,\n" +
            "        KEY `idx_ct` (`cust_no`)\n" +
            "    USING\n" +
            "        BTREE,\n" +
            "        KEY `idx_loan` (`loan_no`)\n" +
            "    USING\n" +
            "        BTREE\n" +
            ") ENGINE = InnoDB DEFAULT CHARSET = utf8 COMMENT = '借据延期申请及处理表'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        parser.getExprParser();

        List<SQLStatement> stmtList = parser.parseStatementList();
        // 将AST通过visitor输出
        StringBuilder out = new StringBuilder();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(out);
        MySqlCreateTableStatement createTable = (MySqlCreateTableStatement) stmtList.get(0);
        List<SQLTableElement> list = createTable.getTableElementList();
        list.stream().forEach(sqlTableElement -> {
            if (sqlTableElement instanceof MySqlUnique) {
                MySqlUnique mySqlUnique = (MySqlUnique) sqlTableElement;
                SQLIndexDefinition indexDefinition = mySqlUnique.getIndexDefinition();
                log.info("唯一索引:" + indexDefinition.getColumns());
            }
        });
    }
    @Test
    public void mysqlToHive() {
        String sql = "CREATE TABLE `u_bankcard_cert` (\n" +
            "    `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
            "    `cert_no` varchar(64) NOT NULL DEFAULT '' COMMENT '认证号',\n" +
            "    `user_no` varchar(64) NOT NULL DEFAULT '' COMMENT '用户号',\n" +
            "    `card_no_encryptx` varchar(128) NOT NULL DEFAULT '' COMMENT 'card_no 加密字段',\n" +
            "    `card_no_md5x` varchar(32) NOT NULL DEFAULT '' COMMENT 'card_no md5字段',\n" +
            "    `card_type` varchar(5) DEFAULT NULL COMMENT '银行卡类型：详见system_dict的card_type',\n" +
            "    `bank_code` varchar(50) DEFAULT NULL COMMENT '银行编号',\n" +
            "    `mobile_no_encryptx` varchar(64) NOT NULL DEFAULT '' COMMENT 'mobile_no 加密字段',\n" +
            "    `mobile_no_md5x` varchar(32) NOT NULL DEFAULT '' COMMENT 'mobile_no md5字段',\n" +
            "    `cust_name_encryptx` varchar(520) NOT NULL DEFAULT '' COMMENT 'cust_name 加密字段',\n" +
            "    `cust_name_md5x` varchar(32) NOT NULL DEFAULT '' COMMENT 'cust_name md5字段',\n" +
            "    `cert_time` datetime NOT NULL COMMENT '认证时间',\n" +
            "    `cert_state` char(1) NOT NULL COMMENT '认证状态：详见system_dict的cert_state',\n" +
            "    `cert_msg` varchar(100) DEFAULT NULL COMMENT '绑卡失败信息描述',\n" +
            "    `business_type` varchar(20) NOT NULL DEFAULT '' COMMENT '业务类型-认证用途 CD授信 DB借款 RP还款 RB 主动绑卡 UB 修改银行卡密码 ',\n" +
            "    `bind_type` char(1) NOT NULL COMMENT '绑定类型：1：绑定新银行卡，2：重新绑定',\n" +
            "    `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',\n" +
            "    `created_by` varchar(100) NOT NULL DEFAULT 'sys' COMMENT '记录创建者',\n" +
            "    `date_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',\n" +
            "    `updated_by` varchar(100) NOT NULL DEFAULT 'sys' COMMENT '记录更新者',\n" +
            "    `term` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '期数',\n" +
            "    `loan_amt` decimal(17, 2) NOT NULL DEFAULT '0.00' COMMENT '借款金额',\n" +
            "    `loan_amt2` decimal(17, 2) DEFAULT NULL COMMENT '借款金额',\n" +
            "    `date_loan` date NOT NULL DEFAULT '9999-01-01 00:00:00' COMMENT '动支申请提交日期',\n" +
            "    `date_cash` datetime DEFAULT NULL COMMENT '放款日期',\n" +
            "    PRIMARY KEY (`id`),\n" +
            "    UNIQUE KEY `idx_cert_no` (`cert_no`,`user_no`),\n" +
            "    UNIQUE KEY `idx_cert_no2` (`mobile_no_md5x`,`user_no`),\n" +
            "    KEY `idx_user_no` (`user_no`),\n" +
            "    KEY `idx_mobile_no_md5` (`mobile_no_md5x`),\n" +
            "    KEY `idx_card_no_md5` (`card_no_md5x`)\n" +
            ") ENGINE = InnoDB AUTO_INCREMENT = 179438434 DEFAULT CHARSET = utf8 COMMENT = '银行卡认证记录表'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        MySqlToHiveOutputVisitor visitor = new MySqlToHiveOutputVisitor();
        stmtList.forEach(sqlStatement -> {
            sqlStatement.accept(visitor);
        });
    }
    @Test
    public void mysqlFileToHive() throws IOException {
        File file = new File("D:\\mysql.sql");
        File file2 = new File(file.getParentFile(),file.getName()+".txt");
        String sqlContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        MySqlStatementParser parser = new MySqlStatementParser(sqlContent);
        List<SQLStatement> stmtList = parser.parseStatementList();
        MySqlToHiveOutputVisitor visitor = new MySqlToHiveOutputVisitor();
        stmtList.forEach(sqlStatement -> {
            sqlStatement.accept(visitor);
            System.out.println(visitor.getContent());
        });
    }

    @Test
    public void parseMysql() throws IOException {
        String sql = "SELECT new_third_category\n" +
            "FROM ads_r_f_vintage_a_gebaoqi\n" +
            " where '${Param_Product}' = '借条API&商城'\n" +
            " and assets_class in ('借条API&商城') \n" +
            "group by 1\n" +
            "order by convert(new_third_category using gbk)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmtList.forEach(sqlStatement -> {
            sqlStatement.accept(visitor);
        });
        System.out.println(visitor.getOriginalTables());
    }

}
