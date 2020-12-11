package com.gitee.linzl.commons.tools;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author linzhenlie-jk
 * @date 2020/12/11
 */
public class BeanUtilTest {
    @Setter
    @Getter
    public static class Product {
        private Long id;

        private String name;
        private String nameMd5x;
        private String nameEncrypt;
    }

    public static void main(String[] args) {
        Product prd = new Product();
        prd.setName("我是产品");

        Product prd2 = new Product();
        prd2.setName("我是产品2");
        prd2.setId(12L);
        prd2.setNameMd5x("实现细节");

        List<CompareResult> list = BeanUtil.compare(prd, prd2);
        System.out.println(JSON.toJSONString(list));
    }
}
