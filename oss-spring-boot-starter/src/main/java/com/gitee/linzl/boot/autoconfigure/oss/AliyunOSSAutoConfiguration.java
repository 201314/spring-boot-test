package com.gitee.linzl.boot.autoconfigure.oss;

import javax.annotation.PreDestroy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.gitee.linzl.oss.service.OSSService;
import com.gitee.linzl.oss.service.impl.AliyunServiceImpl;

/**
 * additional-spring-configuration-metadata.json格式与spring-configuration-metadata.json一致，
 * 
 * 用于不使用@ConfigurationProperties注解的属性配置,可以参考mybatisplus-spring-boot-starter
 * 
 */
@Configuration
@EnableConfigurationProperties(OSSProperties.class)
@ConditionalOnProperty(prefix = OSSProperties.OSSPREFIX, name = "type", havingValue = "aliyun")
public class AliyunOSSAutoConfiguration {

	private OSSProperties properties;

	private OSS client;

	public AliyunOSSAutoConfiguration(OSSProperties properties) {
		this.properties = properties;
	}

	@Bean
	public OSS client() {
		// 创建OSSClient实例
		client = new OSSClientBuilder().build(properties.getAliyun().getEndpoint(),
				properties.getAliyun().getAccessKey(), properties.getAliyun().getSecretKey());
		return client;
	}

	@Bean
	public OSSService ossService(OSS client) {
		return new AliyunServiceImpl(client, properties);
	}

	@PreDestroy
	public void close() {
		if (this.client != null) {
			this.client.shutdown();
		}
	}
}
