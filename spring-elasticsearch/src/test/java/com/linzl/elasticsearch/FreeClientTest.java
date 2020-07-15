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

	/**
	 * /POST http://{{host}}:{{port}}/demo/demo/
	 * 
	 * { "title":"中国产小型无人机的“对手”来了，俄微型拦截导弹便宜量又多", "tag":"军事",
	 * "publishTime":"2018-01-24T23:59:30Z" }
	 */
	@Test
	public void addTest() {
		IndexRequest indexRequest = new IndexRequest(index, type);
		News news = new News();
		news.setTitle("中国产小型无人机的“对手”来了，俄微型拦截导弹便宜量又多");
		news.setTag("军事");
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

	/**
	 * /POST http://{{host}}:{{port}}/_bulk
	 * 
	 * {"index":{"_index":"demo","_type":"demo"}}
	 * 
	 * {"title":"中印边防军于拉达克举行会晤强调维护边境和平","tag":"军事","publishTime":"2018-01-27T08:34:00Z"}
	 * 
	 * {"index":{"_index":"demo","_type":"demo"}}
	 * 
	 * {"title":"费德勒收郑泫退赛礼 进决赛战西里奇","tag":"体育","publishTime":"2018-01-26T14:34:00Z"}
	 * 
	 * {"index":{"_index":"demo","_type":"demo"}}
	 * 
	 * {"title":"欧文否认拿动手术威胁骑士兴奋全明星联手詹皇","tag":"体育","publishTime":"2018-01-26T08:34:00Z"}
	 * 
	 * {"index":{"_index":"demo","_type":"demo"}}
	 * 
	 * {"title":"皇马官方通告拉莫斯伊斯科伤情将缺阵西甲关键战","tag":"体育","publishTime":"2018-01-26T20:34:00Z"}
	 */
	@Test
	public void batchAddTest() {// 批量插入数据
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
		requests.add(generateNewsRequest("中印边防军于拉达克举行会晤 强调维护边境和平", "军事", new Date()));
		requests.add(generateNewsRequest("费德勒收郑泫退赛礼 进决赛战西里奇", "体育", new Date()));
		requests.add(generateNewsRequest("欧文否认拿动手术威胁骑士 兴奋全明星联手詹皇", "体育", new Date()));
		requests.add(generateNewsRequest("皇马官方通告拉莫斯伊斯科伤情 将缺阵西甲关键战", "体育", new Date()));
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

	/**
	 * 查询目标：2018年1月26日早八点到晚八点关于费德勒的前十条体育新闻的标题
	 * 
	 * /POST http://{{host}}:{{port}}/demo/demo/_search
	 * 
	 * { "from": "0", "size": "10", "_source": [ "title","tag" ],
	 * 
	 * "query": { "bool": { "must": [ { "term": { "tag.keyword": "体育" } }, {
	 * "match": { "title": "费德勒" } } ] } } }
	 */

	@Test
	public void queryTest() {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.from(0);
		sourceBuilder.size(10);
		sourceBuilder.fetchSource(new String[] { "title", "tag" }, new String[] {});
		MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", "费德勒");
		TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("tag.keyword", "体育");
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

	/**
	 * /POST http://{{host}}:{{port}}/demo/demo/AWE1fnSx00f4t28WJ4D6/_update {
	 * "doc":{ "tag":"网球" } }
	 */
	@Test
	public void updateTest() {
		UpdateRequest updateRequest = new UpdateRequest(index, type, "kxTYZmIBrWkIOj9Ymudt");
		Map<String, String> map = new HashMap<>();
		map.put("tag", "网球");
		updateRequest.doc(map);
		try {
			rhlClient.update(updateRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * /DELETE http://{{host}}:{{port}}/delete_demo/demo/AWExGSdW00f4t28WAPen
	 */
	@Test
	/**
	 * 该操作是同步执行
	 */
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
	/**
	 * 该操作是异步执行
	 */
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

	/**
	 * /POST http://{{host}}:{{port}}/delete_demo/demo/_delete_by_query
	 * 
	 * { "query":{ "match":{ "content":"test1" } } }
	 */

	/**
	 * /POST http://{{host}}:{{port}}/delete_demo/demo/_delete_by_query
	 * 
	 * { "query":{ "term":{ "content.keyword":"test1" } } }
	 */

	/**
	 * 先查询，后删除
	 */
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

	/**
	 * 部分high level client还不支持的，可以使用low level client
	 */
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
}