package com.gitee.linzl.boot.autoconfigure.ftp;

import com.gitee.linzl.ftp.core.FtpClientTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.util.Objects;

@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ FtpClientTemplate.class })
@EnableConfigurationProperties(FtpClientProperties.class)
public class FtpClientAutoConfiguration {
	@Autowired
	private FtpClientProperties properties;
	private FtpClientPool pool;

	@Bean
	public FtpClientPooledFactory getFtpClientFactory() {
		log.debug("第1个:getFtpClientFactory");
		return new FtpClientPooledFactory(properties);
	}

	@Bean
	public FtpClientPool getFtpTemplate(FtpClientPooledFactory ftpClientFactory) {
		log.debug("第2个:getFtpTemplate");
		GenericObjectPoolConfig<FTPClient> poolConfig = new GenericObjectPoolConfig<>();
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		poolConfig.setTestWhileIdle(true);
		poolConfig.setMinEvictableIdleTimeMillis(160000);
		poolConfig.setSoftMinEvictableIdleTimeMillis(150000);
		poolConfig.setTimeBetweenEvictionRunsMillis(130000);
		this.pool = new FtpClientPool(ftpClientFactory, poolConfig);
		int size = Math.min(properties.getInitialSize().intValue(), poolConfig.getMaxIdle());
		for (int i = 0; i < size; i++) {
			try {
				this.pool.addObject();
			} catch (Exception e) {
				log.error("add FtpClient error...", e);
			}
		}
		return this.pool;
	}

	@PreDestroy
	public void close() {
		if (Objects.nonNull(this.pool)) {
			this.pool.close();
		}
	}
}