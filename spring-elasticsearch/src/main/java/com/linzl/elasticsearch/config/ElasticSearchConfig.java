package com.linzl.elasticsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {
	@Bean
	public RestHighLevelClient restHighLevelClient() {
		RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost("192.168.3.124", 8200, "http"),

				new HttpHost("192.168.3.125", 9200, "http"),
				// new HttpHost("192.168.3.126", 9200, "http"),
				new HttpHost("192.168.3.127", 8200, "http"));
		
		// RestClient restClient = RestClient.builder(new HttpHost("192.168.3.124",
		// 8200, "http"),
		//
		// new HttpHost("192.168.3.125", 9200, "http"),
		// // new HttpHost("192.168.3.126", 9200, "http"),
		// new HttpHost("192.168.3.127", 8200, "http")).build();

		RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);
		return client;
	}

}
