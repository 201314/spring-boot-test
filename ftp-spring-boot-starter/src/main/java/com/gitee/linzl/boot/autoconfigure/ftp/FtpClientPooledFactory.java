package com.gitee.linzl.boot.autoconfigure.ftp;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @description ftp连接池工厂类
 * @author linzl
 * @email 2225010489@qq.com
 * @date 2019年5月27日
 * 
 *       可考虑继承 BasePooledObjectFactory
 */
@Slf4j
public class FtpClientPooledFactory implements PooledObjectFactory<FTPClient> {
	private FtpClientProperties properties;

	public FtpClientPooledFactory(FtpClientProperties properties) {
		this.properties = properties;
	}

	@Override
	public PooledObject<FTPClient> makeObject() throws Exception {
		FTPClient ftpClient = new FTPClient();
		ftpClient.setControlEncoding(properties.getEncoding());
		ftpClient.setCharset(Charset.forName(properties.getEncoding()));// 解决中文乱码问题
		ftpClient.setDefaultTimeout(10 * 1000);
		ftpClient.setConnectTimeout(10 * 1000);// 连接超时设置,建议采用配置

		// FTPClientConfig ftpClientConfig = new
		// FTPClientConfig(FTPClientConfig.SYST_UNIX);// FTPClientConfig.SYST_UNIX
		// ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
		// ftpClientConfig.setServerLanguageCode(Locale.CHINESE.getLanguage());
		// ftpClient.configure(ftpClientConfig);
		try {
			ftpClient.connect(properties.getHost(), properties.getPort());
			int replyCode = ftpClient.getReplyCode(); // 是否成功登录服务器
			if (FTPReply.isPositiveCompletion(replyCode)) {
				if (ftpClient.login(properties.getUsername(), properties.getPassword())) {
					if (log.isDebugEnabled()) {
						log.debug("连接FTP服务器返回码:【{}】", ftpClient.getReplyCode());
					}
					if (properties.isActiveMode()) {
						// 主动模式,客户端主动开放端口给ftp服务器,适合在外网
						ftpClient.enterLocalActiveMode();
						// 主动模式下设置客户方端口范围9000-9100,限制下端口开放范围
						ftpClient.setActivePortRange(9000, 9100);
					} else {
						// 被动模式,ftp服务器开放端口给客户端连接,客户端被动接收,适合在部署在内网客户端，因为一般有防火墙限制,客户端很难开放端口给ftp服务端
						ftpClient.enterLocalPassiveMode();
					}

					// 本机内网IP
					if (StringUtils.hasLength(properties.getActiveExternalIPAddress())) {
						ftpClient.setActiveExternalIPAddress(properties.getActiveExternalIPAddress());
					}
					// 本机外网IP（经过NAT）
					if (StringUtils.hasLength(properties.getReportActiveExternalIPAddress())) {
						ftpClient.setReportActiveExternalIPAddress(properties.getReportActiveExternalIPAddress());
					}
					// 设置下数据连接超时时间
					ftpClient.setDataTimeout(10 * 1000);
					ftpClient.setSoTimeout(10 * 1000);
					ftpClient.setBufferSize(properties.getBufferSize());
					ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
					ftpClient.enterLocalPassiveMode();
				} else {
					close(ftpClient);
				}
			}
			return new DefaultPooledObject<>(ftpClient);
		} catch (Exception e) {
			log.error("建立FTP连接失败", e);
			if (ftpClient.isAvailable()) {
				ftpClient.disconnect();
			}
			ftpClient = null;
			throw new Exception("建立FTP连接失败", e);
		}
	}

	@Override
	public void destroyObject(PooledObject<FTPClient> p) throws Exception {
		FTPClient ftpClient = getObject(p);
		close(ftpClient);
	}

	@Override
	public boolean validateObject(PooledObject<FTPClient> p) {
		FTPClient ftpClient = getObject(p);
		if (ftpClient == null || !ftpClient.isConnected()) {
			return false;
		}
		try {
			return ftpClient.changeWorkingDirectory("/");
		} catch (Exception e) {
			log.error("验证FTP连接失败:【{}】", e.getMessage());
			return false;
		}
	}

	@Override
	public void activateObject(PooledObject<FTPClient> p) throws Exception {
	}

	@Override
	public void passivateObject(PooledObject<FTPClient> p) throws Exception {
	}

	private FTPClient getObject(PooledObject<FTPClient> p) {
		if (p == null || p.getObject() == null) {
			return null;
		}
		return p.getObject();
	}

	public void close(FTPClient ftpClient) throws IOException {
		if (Objects.nonNull(ftpClient) && ftpClient.isConnected()) {
			try {
				ftpClient.logout();// 退出FTP服务器,退出登录
				ftpClient.disconnect();// 关闭FTP服务器的连接
				ftpClient = null;
			} catch (Exception e) {
				throw new RuntimeException("连接FTP服务失败！", e);
			}
			log.debug("成功关闭ftp");
		}
	}
}
