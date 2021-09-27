package com.gitee.linzl;

import com.alibaba.fastjson.JSON;
import com.gitee.linzl.log.mask.fastjson.MaskIdCard;
import com.gitee.linzl.log.mask.fastjson.MaskName;
import com.gitee.linzl.log.mask.fastjson.MaskValueFilter;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Student {
    @MaskName
    private String fileName;
    private Integer age;
    private String mobile;
    private String fullName;
    @MaskIdCard
    private String idCardNo;
    private String bankCard;
    private String userName;

    private String userName2;
    private String userName3;
    private String email;

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
        stu2.setEmail("2225010412@qq.com");
        System.out.println("stu222" + stu2);
    }
}
