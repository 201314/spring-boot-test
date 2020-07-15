package com.gitee.linzl.boot.autoconfigure.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(ignoreUnknownFields = false, prefix = "ftp")
@Data
public class FtpClientProperties {
	/**
	 * 登录地址
	 */
	private String host = "127.0.0.1";
	/**
	 * 端口号
	 */
	private int port = FTPClient.DEFAULT_PORT;
	/**
	 * 登录用户
	 */
	private String username = "linzl";
	/**
	 * 登录密码
	 */
	private String password = "linzl";
	/**
	 * ftp流缓存大小
	 */
	private int bufferSize = 8096;
	/**
	 * 初始化连接数
	 */
	private Integer initialSize = 3;
	/**
	 * 默认编码
	 */
	private String encoding = "UTF-8";

	/**
	 * 默认为主动模式
	 */
	private boolean isActiveMode = false;
	/**
	 * 主动模式下，本机内网IP
	 */
	private String activeExternalIPAddress;
	/**
	 * 主动模式下， 本机外网IP(经过NAT)
	 */
	private String reportActiveExternalIPAddress;
}
