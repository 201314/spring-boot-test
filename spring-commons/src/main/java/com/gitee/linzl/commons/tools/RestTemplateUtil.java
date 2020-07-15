package com.gitee.linzl.commons.tools;

import java.io.File;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 远程服务调用工具
 * 
 * @description
 * @author linzl
 * @email 2225010489@qq.com
 * @date 2019年3月27日
 */
public class RestTemplateUtil {
	private RestTemplate restTemplate;

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public RestTemplate getRestTemplate() {
		return this.restTemplate;
	}

	public <T> T postForJSON(String json, String url, Class<T> responseType) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(json, headers);
		return restTemplate.postForEntity(url, entity, responseType).getBody();
	}

	/**
	 * @param json
	 * @param url
	 * @param responseType
	 * 
	 *            <pre class="code">
	 *            ParameterizedTypeReference<ResultBean<QhEtcStoreConfirmResponse>> responseType = new ParameterizedTypeReference<ResultBean<QhEtcStoreConfirmResponse>>() {
	 *            };
	 *            </pre>
	 * 
	 * @return
	 */
	public <T> T postForJSON(String json, String url, ParameterizedTypeReference<T> responseType) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(json, headers);
		return restTemplate.exchange(url, HttpMethod.POST, entity, responseType).getBody();
	}

	/**
	 * 上传文件
	 * 
	 * @param file
	 *            待上传的文件
	 * @param url
	 *            上传路径
	 * @param responseType
	 *            响应的参数类型
	 * @return
	 */
	public <T> T upload(File file, String url, Class<T> responseType) {
		FileSystemResource resource = new FileSystemResource(file);
		MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
		param.add("file", resource);
		param.add("name", file.getName());
		return restTemplate.postForObject(url, param, responseType);
	}
}
