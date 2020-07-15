package com.gitee.linzl.oss.service;

import java.io.File;
import java.io.InputStream;

public interface OSSService {
	/**
	 * 上传byte数组
	 * 
	 * @param bytes
	 */
	public void upload(byte[] bytes, String path);

	/**
	 * 上传文件流
	 */
	public void upload(InputStream inputStream, String path);

	/**
	 * 断点续传是分片上传的封装和加强，是用分片上传实现的；
	 * 
	 * 文件较大或网络环境较差时，推荐使用分片上传；
	 * 
	 * @param file
	 */
	public void upload(File file, String path);

	/**
	 * 追加上传,视频/音频等适用。阿里支持，七牛没有该接口
	 * 
	 * @param inputStream
	 * @return 返回下次追加内容的位置
	 */
	public long appendUpload(InputStream inputStream, long position, String path);

}
