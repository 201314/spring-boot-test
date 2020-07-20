package com.gitee.linzl.boot.autoconfigure.oss;

import com.gitee.linzl.oss.service.OSSService;
import com.gitee.linzl.oss.service.impl.DefaultServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

/**
 * additional-spring-configuration-metadata.json格式与spring-configuration-metadata.json一致，
 * 
 * 用于不使用@ConfigurationProperties注解的属性配置,可以参考mybatisplus-spring-boot-starter
 * 
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OSSProperties.class)
@ConditionalOnProperty(prefix = OSSProperties.OSSPREFIX, name = "type", havingValue = "default")
public class DefaultOSSAutoConfiguration {

	private OSSProperties properties;

	public DefaultOSSAutoConfiguration(OSSProperties properties) {
		this.properties = properties;
	}

	@Bean
	public OSSService ossService() {
		return new DefaultServiceImpl(properties);
	}

	@PreDestroy
	public void close() {
	}
}
