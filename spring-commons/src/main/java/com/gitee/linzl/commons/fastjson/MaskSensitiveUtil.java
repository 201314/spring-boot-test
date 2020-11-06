package com.gitee.linzl.commons.fastjson;

import org.apache.commons.lang3.StringUtils;

/**
 * 脱敏工具
 *
 * @author linzhenlie
 * @date 2019/10/8
 */
public class MaskSensitiveUtil {
    private static final String maskSymbol = "*";

    /**
     * [姓名] 只显示第一个汉字，其他隐藏为星号<例子：李**>
     *
     * @param fullName
     * @return
     */
    public static String chineseName(final String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return "";
        }
        String name = StringUtils.left(fullName, 1);
        return StringUtils.rightPad(name, StringUtils.length(fullName), maskSymbol);
    }

    /**
     * [身份证号] 显示第1位，最后1位其他隐藏。共计18位或者15位。<例子：4****************X>
     *
     * @param idCardNum
     * @return
     */
    public static String idCardNum(final String idCardNum) {
        if (StringUtils.isBlank(idCardNum)) {
            return "";
        }
        String left = StringUtils.left(idCardNum, 1);
        String right = StringUtils.right(idCardNum, 1);

        return StringUtils.rightPad(left, StringUtils.length(idCardNum) - 1, maskSymbol).concat(right);
    }

    /**
     * [手机号码] 前三位，后四位，其他隐藏<例子:138****1234>
     *
     * @param num
     * @return
     */
    public static String mobile(final String num) {
        if (StringUtils.isBlank(num)) {
            return "";
        }

        return StringUtils.left(num, 3).concat(StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num) - 3, maskSymbol));
    }

    /**
     * [银行卡号] 后四位，其他用星号隐藏<例子:**** **** **** 1234>
     *
     * @param cardNum
     * @return
     */
    public static String bankCard(final String cardNum) {
        if (StringUtils.isBlank(cardNum)) {
            return "";
        }
        String num = StringUtils.right(cardNum, 4);
        return StringUtils.leftPad(num, StringUtils.length(cardNum), maskSymbol);
    }

    /**
     * [地址] 只显示到地区，不显示详细地址；我们要对个人信息增强保护<例子：北京市海淀区****>
     *
     * @param sensitiveSize 敏感信息长度
     */
    public static String address(final String address, final int sensitiveSize) {
        if (StringUtils.isBlank(address)) {
            return "";
        }
        int length = StringUtils.length(address);
        return StringUtils.rightPad(StringUtils.left(address, length - sensitiveSize), length, maskSymbol);
    }

    /**
     * [电子邮箱] 邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示<例子:g**@163.com>
     */
    public static String email(final String email) {
        if (StringUtils.isBlank(email)) {
            return "";
        }
        int index = StringUtils.indexOf(email, "@");
        if (index <= 1) {
            return email;
        }
        return StringUtils.rightPad(StringUtils.left(email, 1), index, maskSymbol)
                .concat(StringUtils.mid(email, index, StringUtils.length(email)));
    }

    /**
     * [公司开户银行联号] 公司开户银行联行号,显示前两位，其他用星号隐藏，每位1个星号<例子:12********>
     */
    public static String cnapsCode(final String code) {
        if (StringUtils.isBlank(code)) {
            return "";
        }
        return StringUtils.rightPad(StringUtils.left(code, 2), StringUtils.length(code), maskSymbol);
    }

    public static void main(String[] args) {
        System.out.println(chineseName("邓爷爷"));
        System.out.println(mobile("13828498026"));
        System.out.println(idCardNum("445222199809132941"));
        System.out.println(bankCard("6222600260001072444"));
        System.out.println(email("2225010487@qq.com"));
    }
}
