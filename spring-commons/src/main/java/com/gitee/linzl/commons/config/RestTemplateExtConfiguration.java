package com.gitee.linzl.commons.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 设置httpClient
 *
 * @author linzhenlie-jk
 * @date 2021/7/21
 */
@Component
@ConditionalOnClass({RedisTemplate.class, HttpClient.class})
@Slf4j
public class RestTemplateExtConfiguration {

    @Bean
    public RestTemplateCustomizer restTemplate() {
        RestTemplateCustomizer customizer = new RestTemplateCustomizer() {
            @Override
            public void customize(RestTemplate restTemplate) {
                HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
                // 设置连接超时时间,单位毫秒
                httpRequestFactory.setConnectTimeout(10000);
                // 设置从连接池获取连接实例的超时
                httpRequestFactory.setConnectionRequestTimeout(2000);
                // 设置连接超时时间,单位毫秒
                httpRequestFactory.setReadTimeout(72000);
                // 允许重定向
                HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
                httpRequestFactory.setHttpClient(httpClient);
                restTemplate.setRequestFactory(httpRequestFactory);
            }
        };
        return customizer;
    }
}
