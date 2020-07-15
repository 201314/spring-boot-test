package com.linzl.elasticsearch.rest;

import java.io.IOException;
import java.util.Date;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.linzl.elasticsearch.model.News;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private RestHighLevelClient rhlClient;

	public void addNews() {
		IndexRequest indexRequest = new IndexRequest("news_index", "military");
		News news = new News();
		news.setTitle("中国产小型无人机的“对手”来了，俄微型拦截导弹便宜量又多");
		news.setTag("军事");
		news.setPublishTime(new Date());
		String source = JSON.toJSONString(news);
		indexRequest.source(source, XContentType.JSON);
		try {
			rhlClient.index(indexRequest);
		} catch (IOException e) {
		}
	}

	public void delUser() {

	}

	public void updateUser() {

	}

	public void searchUser() {

	}
}
