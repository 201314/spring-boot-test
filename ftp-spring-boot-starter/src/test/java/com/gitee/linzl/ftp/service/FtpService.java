package com.gitee.linzl.ftp.service;

import java.io.File;

import org.springframework.stereotype.Service;

import com.gitee.linzl.ftp.annotation.AutoFTPClient;
import com.gitee.linzl.ftp.core.FtpClientTemplate;

@Service
public class FtpService {

	@AutoFTPClient
	public void sendFile(FtpClientTemplate ftp) {
		ftp.upload("/test1", new File("D:\\test\\jce_policy-8.txt"));
	}

	@AutoFTPClient
	public void sendFile(String hello, FtpClientTemplate ftp) {
		System.out.println("hello==hello");
		ftp.upload("/test1", new File("D:\\test\\jce_policy-8.txt"));
	}

	@AutoFTPClient
	public void sendFile(String hello) {
		System.out.println("hello==hello");
	}

	@AutoFTPClient
	public void sendFile() {
		System.out.println("没有参数");
	}
}
