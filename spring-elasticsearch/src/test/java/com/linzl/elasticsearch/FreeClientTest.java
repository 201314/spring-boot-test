/*
package com.linzl.elasticsearch;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.linzl.elasticsearch.model.News;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FreeClientTest {

	private String index;

	private String type;

	@Autowired
	private RestHighLevelClient rhlClient;

	@Before
	public void prepare() {
		index = "demo";
		type = "demo";
	}

	*/
/**
	 * /POST http://{{host}}:{{port}}/demo/demo/
	 * 
	 * { "title":"????????????????????????????????????????????????????????????????????????????????????", "tag":"??????",
	 * "publishTime":"2018-01-24T23:59:30Z" }
	 *//*

	@Test
	public void addTest() {
		IndexRequest indexRequest = new IndexRequest(index, type);
		News news = new News();
		news.setTitle("????????????????????????????????????????????????????????????????????????????????????");
		news.setTag("??????");
		news.setPublishTime(new Date());
		String source = JSON.toJSONString(news);
		indexRequest.source(source, XContentType.JSON);
		try {
			rhlClient.index(indexRequest);
			// rhlClient.indexAsync(indexRequest, listener, headers);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	*/
/**
	 * /POST http://{{host}}:{{port}}/_bulk
	 * 
	 * {"index":{"_index":"demo","_type":"demo"}}
	 * 
	 * {"title":"???????????????????????????????????????????????????????????????","tag":"??????","publishTime":"2018-01-27T08:34:00Z"}
	 * 
	 * {"index":{"_index":"demo","_type":"demo"}}
	 * 
	 * {"title":"??????????????????????????? ?????????????????????","tag":"??????","publishTime":"2018-01-26T14:34:00Z"}
	 * 
	 * {"index":{"_index":"demo","_type":"demo"}}
	 * 
	 * {"title":"???????????????????????????????????????????????????????????????","tag":"??????","publishTime":"2018-01-26T08:34:00Z"}
	 * 
	 * {"index":{"_index":"demo","_type":"demo"}}
	 * 
	 * {"title":"??????????????????????????????????????????????????????????????????","tag":"??????","publishTime":"2018-01-26T20:34:00Z"}
	 *//*

	@Test
	public void batchAddTest() {// ??????????????????
		BulkRequest bulkRequest = new BulkRequest();
		List<IndexRequest> requests = generateRequests();
		for (IndexRequest indexRequest : requests) {
			bulkRequest.add(indexRequest);
		}
		try {
			rhlClient.bulk(bulkRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<IndexRequest> generateRequests() {
		List<IndexRequest> requests = new ArrayList<>();
		requests.add(generateNewsRequest("??????????????????????????????????????? ????????????????????????", "??????", new Date()));
		requests.add(generateNewsRequest("??????????????????????????? ?????????????????????", "??????", new Date()));
		requests.add(generateNewsRequest("???????????????????????????????????? ???????????????????????????", "??????", new Date()));
		requests.add(generateNewsRequest("?????????????????????????????????????????? ????????????????????????", "??????", new Date()));
		return requests;
	}

	public IndexRequest generateNewsRequest(String title, String tag, Date publishTime) {
		IndexRequest indexRequest = new IndexRequest(index, type);
		News news = new News();
		news.setTitle(title);
		news.setTag(tag);
		news.setPublishTime(publishTime);
		String source = JSON.toJSONString(news);
		indexRequest.source(source, XContentType.JSON);
		return indexRequest;
	}

	*/
/**
	 * ???????????????2018???1???26????????????????????????????????????????????????????????????????????????
	 * 
	 * /POST http://{{host}}:{{port}}/demo/demo/_search
	 * 
	 * { "from": "0", "size": "10", "_source": [ "title","tag" ],
	 * 
	 * "query": { "bool": { "must": [ { "term": { "tag.keyword": "??????" } }, {
	 * "match": { "title": "?????????" } } ] } } }
	 *//*


	@Test
	public void queryTest() {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.from(0);
		sourceBuilder.size(10);
		sourceBuilder.fetchSource(new String[] { "title", "tag" }, new String[] {});
		MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", "?????????");
		TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("tag.keyword", "??????");
		// RangeQueryBuilder rangeQueryBuilder =
		// QueryBuilders.rangeQuery("publishTime");
		// rangeQueryBuilder.gte("2018-01-26T08:00:00Z");
		// rangeQueryBuilder.lte("2018-01-26T20:00:00Z");
		BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
		boolBuilder.must(matchQueryBuilder);
		boolBuilder.must(termQueryBuilder);
		// boolBuilder.must(rangeQueryBuilder);
		sourceBuilder.query(boolBuilder);
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		searchRequest.source(sourceBuilder);
		try {
			SearchResponse response = rhlClient.search(searchRequest);
			System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	*/
/**
	 * /POST http://{{host}}:{{port}}/demo/demo/AWE1fnSx00f4t28WJ4D6/_update {
	 * "doc":{ "tag":"??????" } }
	 *//*

	@Test
	public void updateTest() {
		UpdateRequest updateRequest = new UpdateRequest(index, type, "kxTYZmIBrWkIOj9Ymudt");
		Map<String, String> map = new HashMap<>();
		map.put("tag", "??????");
		updateRequest.doc(map);
		try {
			rhlClient.update(updateRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	*/
/**
	 * /DELETE http://{{host}}:{{port}}/delete_demo/demo/AWExGSdW00f4t28WAPen
	 *//*

	@Test
	*/
/**
	 * ????????????????????????
	 *//*

	public void delete() {
		DeleteRequest deleteRequest = new DeleteRequest(index, type, "kxTYZmIBrWkIOj9Ymudt");
		DeleteResponse response = null;
		try {
			response = rhlClient.delete(deleteRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(response);
	}

	@Test
	*/
/**
	 * ????????????????????????
	 *//*

	public void deleteAsyn() {
		DeleteRequest request = new DeleteRequest(index, type, "kxTYZmIBrWkIOj9Ymudt");
		rhlClient.deleteAsync(request, new ActionListener<DeleteResponse>() {
			@Override
			public void onResponse(DeleteResponse deleteResponse) {

			}

			@Override
			public void onFailure(Exception e) {

			}
		});
	}

	*/
/**
	 * /POST http://{{host}}:{{port}}/delete_demo/demo/_delete_by_query
	 * 
	 * { "query":{ "match":{ "content":"test1" } } }
	 *//*


	*/
/**
	 * /POST http://{{host}}:{{port}}/delete_demo/demo/_delete_by_query
	 * 
	 * { "query":{ "term":{ "content.keyword":"test1" } } }
	 *//*


	*/
/**
	 * ?????????????????????
	 *//*

	@Test
	public void deleteByQuery() {
		try {
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			sourceBuilder.timeout(new TimeValue(2, TimeUnit.SECONDS));
			TermQueryBuilder termQueryBuilder1 = QueryBuilders.termQuery("content.keyword", "test1");
			sourceBuilder.query(termQueryBuilder1);
			SearchRequest searchRequest = new SearchRequest(index);
			searchRequest.types(type);
			searchRequest.source(sourceBuilder);
			SearchResponse response = rhlClient.search(searchRequest);
			SearchHits hits = response.getHits();
			List<String> docIds = new ArrayList<>(hits.getHits().length);
			for (SearchHit hit : hits) {
				docIds.add(hit.getId());
			}

			BulkRequest bulkRequest = new BulkRequest();
			for (String id : docIds) {
				DeleteRequest deleteRequest = new DeleteRequest(index, type, id);
				bulkRequest.add(deleteRequest);
			}
			rhlClient.bulk(bulkRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void checkHealth() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("wait_for_status", "green");

		Response response = null;
		try {
			response = rhlClient.getLowLevelClient().performRequest("GET", "/_cluster/health", parameters);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ClusterHealthStatus healthStatus = null;
		try (InputStream is = response.getEntity().getContent()) {
			Map<String, Object> map = XContentHelper.convertToMap(XContentType.JSON.xContent(), is, true);
			healthStatus = ClusterHealthStatus.fromString((String) map.get("status"));
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (healthStatus == ClusterHealthStatus.GREEN) {

		}
	}

	*/
/**
	 * ??????high level client??????????????????????????????low level client
	 *//*

	@Test
	public void otherTest() {
		Settings indexSettings = Settings.builder().put("index.number_of_shards", 1).put("index.number_of_replicas", 0)
				.build();

		String payload = null;
		try {
			payload = XContentFactory.jsonBuilder().startObject()

					.startObject("settings").value(indexSettings).endObject()

					.startObject("mappings").startObject("doc")

					.startObject("properties").startObject("time")

					.field("type", "date")

					.endObject().endObject().endObject().endObject().endObject().string();
		} catch (IOException e) {
			e.printStackTrace();
		}

		NStringEntity entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);

		Response response = null;
		try {
			response = rhlClient.getLowLevelClient().performRequest("PUT", "my-index", Collections.emptyMap(), entity);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {

		}
	}

	@Autowired
	private RestClient restClient;

	@Test
	public void deleteOther() {
		String endPoint = "/" + index + "/" + type + "/_delete_by_query";
		String source = genereateQueryString();
		NStringEntity entity = new NStringEntity(source, ContentType.APPLICATION_JSON);
		try {
			restClient.performRequest("POST", endPoint, Collections.<String, String>emptyMap(), entity);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String genereateQueryString() {
		IndexRequest indexRequest = new IndexRequest();
		XContentBuilder builder;
		try {
			builder = JsonXContent.contentBuilder().startObject().startObject("query").startObject("term")
					.field("content.keyword", "test1").endObject().endObject().endObject();
			indexRequest.source(builder);
		} catch (IOException e) {
		}
		String source = indexRequest.source().utf8ToString();
		return source;
	}
}*/
