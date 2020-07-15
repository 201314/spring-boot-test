package com.gitee.linzl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UploadFileTest {
	@Autowired
	private RestTemplate rest;

	@Test
	public void testUpload() throws Exception {
		String url = "http://localhost:9080/file/upload";
		String filePath = "D://hello.txt";

		File file = new File(filePath);
		InputStream input = new FileInputStream(file);
		byte[] bytesArray = new byte[(int) file.length()];
		input.read(bytesArray);
		ByteArrayResource resource = new ByteArrayResource(bytesArray) {
			@Override
			public String getFilename() {
				return file.getName();
			}
		};
		MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
		param.add("jarFile", resource);
		param.add("file", resource);
		FileSystemResource newResource = new FileSystemResource(new File(filePath));
		param.add("jarFile2", newResource);

		Student test = new Student();
		test.setFileName("文件名");
		test.setAge(110);
		param.add("test", JSON.toJSONString(test));
		param.add("issueOrderNo", 1122);
		param.add("fileType", 1);

		String string = rest.postForObject(url, param, String.class);
		System.out.println(string);
	}
}
