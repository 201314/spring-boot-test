package com.gitee.linzl.oss.service.impl;

import java.io.File;
import java.io.InputStream;

import com.gitee.linzl.boot.autoconfigure.oss.OSSProperties;
import com.gitee.linzl.oss.service.OSSService;

/**
 * 默认实现本地储存文件
 * 
 * @description
 * @author linzl
 * @email 2225010489@qq.com
 * @date 2018年11月30日
 */
public class DefaultServiceImpl implements OSSService {
	private OSSProperties properties;

	public DefaultServiceImpl(OSSProperties properties) {
		this.properties = properties;
	}

	@Override
	public void upload(byte[] bytes, String path) {

	}

	@Override
	public void upload(InputStream inputStream, String path) {

	}

	@Override
	public void upload(File file, String path) {

	}

	@Override
	public long appendUpload(InputStream inputStream, long position, String path) {
		return 0;
	}
}
