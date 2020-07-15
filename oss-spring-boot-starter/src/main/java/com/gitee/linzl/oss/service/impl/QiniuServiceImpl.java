package com.gitee.linzl.oss.service.impl;

import com.gitee.linzl.boot.autoconfigure.oss.OSSProperties;
import com.gitee.linzl.oss.service.OSSService;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;

@Slf4j
public class QiniuServiceImpl implements OSSService {
	private Auth auth;
	private UploadManager uploadManager;
	private OSSProperties properties;

	public QiniuServiceImpl(Auth auth, UploadManager uploadManager, OSSProperties properties) {
		this.auth = auth;
		this.uploadManager = uploadManager;
		this.properties = properties;
	}

	@Override
	public void upload(byte[] bytes, String path) {
		String upToken = auth.uploadToken(properties.getQiniu().getBucket());
		try {
			Response response = uploadManager.put(bytes, path, upToken);
			// 解析上传成功的结果
			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
		} catch (QiniuException ex) {
			Response r = ex.response;
			log.error(r.toString());
			try {
				log.error("bodyString:【{}】",r.bodyString());
			} catch (QiniuException ex2) {
			}
		}
	}

	/**
	 * 七牛不推荐使用
	 */
	@Override
	public void upload(InputStream inputStream, String path) {// 构造一个带指定Zone对象的配置类
		String upToken = auth.uploadToken(properties.getQiniu().getBucket());
		try {
			Response response = uploadManager.put(inputStream, path, upToken, null, null);
			// 解析上传成功的结果
			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
		} catch (QiniuException ex) {
            Response r = ex.response;
            log.error(r.toString());
            try {
                log.error("bodyString:【{}】",r.bodyString());
            } catch (QiniuException ex2) {
            }
		}
	}

	@Override
	public void upload(File file, String path) {
		String upToken = auth.uploadToken(properties.getQiniu().getBucket());
		try {
			Response response = uploadManager.put(file, path, upToken);
			// 解析上传成功的结果
			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
		} catch (QiniuException ex) {
            Response r = ex.response;
            log.error(r.toString());
            try {
                log.error("bodyString:【{}】",r.bodyString());
            } catch (QiniuException ex2) {
            }
		}
	}

	@Override
	public long appendUpload(InputStream inputStream, long position, String path) {
		// 没有
		return 0;
	}

}
