package com.gitee.linzl.commons.tools;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

/**
 * 远程服务调用工具
 *
 * @author linzl
 * @description
 * @email 2225010489@qq.com
 * @date 2019年3月27日
 */
@Component
public class RestTemplateUtil {
    private static RestTemplate restTemplate;

    public RestTemplateUtil(@Nullable RestTemplate restTemplate) {
		RestTemplateUtil.restTemplate = restTemplate;
    }

    public static <T> T postForJSON(String json, String url, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(json, headers);
        return restTemplate.postForEntity(url, entity, responseType).getBody();
    }

    /**
     * @param json
     * @param url
     * @param responseType <pre class="code">
     *                                ParameterizedTypeReference<ResultBean<QhEtcStoreConfirmResponse>> responseType = new ParameterizedTypeReference<ResultBean<QhEtcStoreConfirmResponse>>() {
     *                                };
     *                                </pre>
     * @return
     */
    public static <T> T postForJSON(String json, String url, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(json, headers);
        return restTemplate.exchange(url, HttpMethod.POST, entity, responseType).getBody();
    }

    /**
     * 上传文件
     *
     * @param file         待上传的文件
     * @param url          上传路径
     * @param responseType 响应的参数类型
     * @return
     */
    public static <T> T upload(File file, String url, Class<T> responseType) {
        FileSystemResource resource = new FileSystemResource(file);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("file", resource);
        param.add("name", file.getName());
        return restTemplate.postForObject(url, param, responseType);
    }
}
