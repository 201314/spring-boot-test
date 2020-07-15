package com.gitee.linzl.boot.autoconfigure.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class FtpClientPool extends GenericObjectPool<FTPClient> {
	public FtpClientPool(PooledObjectFactory<FTPClient> factory) {
		super(factory);
	}

	public FtpClientPool(final PooledObjectFactory<FTPClient> factory,
			final GenericObjectPoolConfig<FTPClient> config) {
		super(factory, config);
	}
}
