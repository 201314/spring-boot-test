package com.gitee.linzl.oss.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.AppendObjectRequest;
import com.aliyun.oss.model.AppendObjectResult;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.UploadFileRequest;
import com.aliyun.oss.model.UploadFileResult;
import com.gitee.linzl.boot.autoconfigure.oss.OSSProperties;
import com.gitee.linzl.oss.listener.PutObjectProgressListener;
import com.gitee.linzl.oss.service.OSSService;

public class AliyunServiceImpl implements OSSService {
	private OSS client;
	private OSSProperties properties;

	public AliyunServiceImpl(OSS client, OSSProperties properties) {
		this.client = client;
		this.properties = properties;
	}

	@Override
	public void upload(byte[] bytes, String path) {
		upload(bytes, path, false);
	}

	/**
	 * 
	 * @param bytes
	 * @param path
	 * @param uploadProgress
	 *            是否显示进度条
	 */
	public void upload(byte[] bytes, String path, boolean uploadProgress) {
		if (uploadProgress) {
			client.putObject(
					new PutObjectRequest(properties.getAliyun().getBucket(), path, new ByteArrayInputStream(bytes))
							.<PutObjectRequest>withProgressListener(new PutObjectProgressListener()));
		} else {
			client.putObject(
					new PutObjectRequest(properties.getAliyun().getBucket(), path, new ByteArrayInputStream(bytes)));
		}
		client.shutdown();
	}

	@Override
	public void upload(InputStream inputStream, String path) {
		client.putObject(properties.getAliyun().getBucket(), path, inputStream);
		client.shutdown();
	}

	/**
	 * 断点续传
	 */
	@Override
	public void upload(File file, String path) {
		// 设置断点续传请求
		UploadFileRequest uploadFileRequest = new UploadFileRequest(properties.getAliyun().getBucket(), path);
		// 指定上传的本地文件
		uploadFileRequest.setUploadFile(file.getPath());
		// 指定上传并发线程数
		uploadFileRequest.setTaskNum(5);
		// 指定上传的分片大小
		uploadFileRequest.setPartSize(1 * 1024 * 1024);
		// 开启断点续传
		uploadFileRequest.setEnableCheckpoint(true);
		// 断点续传上传
		UploadFileResult result = null;
		try {
			result = client.uploadFile(uploadFileRequest);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			// 关闭client
			if (result.getMultipartUploadResult() != null) {
				System.out.println(result.getMultipartUploadResult().getLocation());
			}
			client.shutdown();
		}
	}

	/**
	 * 追加上传
	 */
	@Override
	public long appendUpload(InputStream inputStream, long position, String path) {
		AppendObjectRequest appendObjectRequest = new AppendObjectRequest(properties.getAliyun().getBucket(), path,
				inputStream);
		// 第一次追加
		appendObjectRequest.setPosition(position);
		AppendObjectResult appendObjectResult = client.appendObject(appendObjectRequest);
		client.shutdown();
		return appendObjectResult.getNextPosition();
	}

}
