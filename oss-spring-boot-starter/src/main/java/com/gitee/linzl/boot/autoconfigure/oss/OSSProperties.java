package com.gitee.linzl.boot.autoconfigure.oss;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import com.gitee.linzl.boot.autoconfigure.enums.OSSType;

import lombok.Getter;
import lombok.Setter;

/**
 * @description
 * @author linzl
 * @email 2225010489@qq.com
 * @date 2018年4月10日
 */
@Setter
@Getter
@ConfigurationProperties(prefix = OSSProperties.OSSPREFIX)
public class OSSProperties {
	public static final String OSSPREFIX = "spring.oss";

	/** 云存储类型,默认本地存储 */
	private OSSType type = OSSType.DEFAULT;

	@NestedConfigurationProperty
	private QiniuProperties qiniu;

	@NestedConfigurationProperty
	private AliyunProperties aliyun;

	@NestedConfigurationProperty
	private TencentProperties tencent;

	@Setter
	@Getter
	public static class BasePropertie {
		/** OSS 存储空间名 */
		private String bucket;
		/** 密钥 */
		private String accessKey;
		/** 密钥 */
		private String secretKey;
	}

	@Setter
	@Getter
	public static class QiniuProperties extends BasePropertie {
	}

	@Setter
	@Getter
	public static class AliyunProperties extends BasePropertie {
		private String endpoint;
	}

	@Setter
	@Getter
	public static class TencentProperties extends BasePropertie {
	}
}
