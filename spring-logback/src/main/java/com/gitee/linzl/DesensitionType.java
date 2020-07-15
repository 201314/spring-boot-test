package com.gitee.linzl;

import lombok.Getter;

@Getter
public enum DesensitionType {
    PHONE("11位手机号", "^(\\d{3})\\d{4}(\\d{4})$", "$1****$2"),
    IDENTITYNO("16或者18身份证号", "^(\\d{4})\\d{8,10}(\\d{4})$", "$1****$2"),
    BANKCARDNO("银行卡号", "^(\\d{4})\\d*(\\d{4})$", "$1****$2"),

    CUSTOM("自定义正则处理", ""),
    TRUNCATE("字符串截取处理", "");

    String describe;

    String[] regular;

    DesensitionType(String describe, String... regular) {
        this.describe = describe;
        this.regular = regular;
    }
}