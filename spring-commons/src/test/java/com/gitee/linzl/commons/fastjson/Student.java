package com.gitee.linzl.commons.fastjson;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Student {
    private String fileName;
    private Integer age;
    @MaskMobile
    private String mobile;
    @MaskName
    private String fullName;
    @MaskIdCard
    private String idCardNo;
    @MaskBankCard
    private String bankCard;
    @MaskSensitiveField(left = 2,right = 2)
    private String userName;

    private String userName2;
    @MaskSensitiveField(padding = "$1$2**$3", regex = "^(\\d{3})(\\d{2})\\d{2}(\\d{4})$")
    private String userName3;

    @Override
    public String toString() {
        return JSON.toJSONString(this, new MaskValueFilter());
    }

    public static void main(String[] args) {
        Student stu2 = new Student();
        stu2.setFileName("我是Student2222");
        stu2.setMobile("13828498029===");
        stu2.setFullName("邓小小");
        stu2.setIdCardNo("445222197906498761");
        stu2.setBankCard("6217790001073282390");
        stu2.setUserName("linzl哈哈最长就2");
        stu2.setUserName2("中国人");
        stu2.setUserName3("13690251143");
        System.out.println("stu222" + stu2);
    }
}
