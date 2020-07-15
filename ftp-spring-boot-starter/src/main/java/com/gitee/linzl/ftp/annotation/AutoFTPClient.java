package com.gitee.linzl.ftp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在需要使用FTPClient实例的方法上使用
 * 
 * @description
 * 
 * @author linzl
 * @email 2225010489@qq.com
 * @date 2019年7月7日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoFTPClient {

}
