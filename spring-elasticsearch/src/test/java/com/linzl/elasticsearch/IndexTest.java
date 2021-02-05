/*
package com.linzl.elasticsearch;

import java.io.IOException;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.action.support.ActiveShardCount;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IndexTest {

	@Autowired
	private RestHighLevelClient client;
	
	@Test
	public void createIndex() throws IOException {
		// Create Index Request
		CreateIndexRequest request = new CreateIndexRequest("twitter");
		request = Requests.createIndexRequest("twitter");// 最好使用这种

		// Settings for this index
		request.settings(Settings.builder().put("index.number_of_shards", 3)

				.put("index.number_of_replicas", 2));
		
		//An index may be created with mappings for its document types
		request.mapping("tweet", 
			    "  {\n" +
			    "    \"tweet\": {\n" +
			    "      \"properties\": {\n" +
			    "        \"message\": {\n" +
			    "          \"type\": \"text\"\n" +
			    "        }\n" +
			    "      }\n" +
			    "    }\n" +
			    "  }", 
			    XContentType.JSON);
		
		//Aliases can be set at index creation time
		request.alias(
			    new Alias("twitter_alias")  
			);
		
		//以下可选
		//Timeout to wait for the all the nodes to acknowledge the index creation
		request.timeout(TimeValue.timeValueMinutes(2));
		request.timeout("2m");
		//Timeout to connect to the master node
		request.masterNodeTimeout(TimeValue.timeValueMinutes(1)); 
		request.masterNodeTimeout("1m");
		//The number of active shard copies to wait for before the create index API returns a response
		request.waitForActiveShards(2); 
		request.waitForActiveShards(ActiveShardCount.DEFAULT);
		
		//同步执行
		CreateIndexResponse createIndexResponse = client.indices().create(request);
		System.out.println("createIndexResponse ="+createIndexResponse.index());
		//Indicates whether all of the nodes have acknowledged the request
		System.out.println("acknowledged ="+createIndexResponse.isAcknowledged());
		//Indicates whether the requisite number of shard copies were started for each shard in the index before timing out
		System.out.println("shardsAcknowledged ="+createIndexResponse.isShardsAcknowledged());
		
		//不能执行两次一样的创建索引，否则会报索引已存在异常
		
		//异步执行
//		client.indices().createAsync(request, new ActionListener<CreateIndexResponse>() {
//			@Override
//			public void onResponse(CreateIndexResponse createIndexResponse) {
//				System.out.println("createIndexResponse="+createIndexResponse.index());
//				System.out.println("acknowledged ="+createIndexResponse.isAcknowledged());
//				System.out.println("shardsAcknowledged ="+createIndexResponse.isShardsAcknowledged());
//			}
//
//			@Override
//			public void onFailure(Exception e) {
//
//			}
//		});
	}
	
	@Test
	public void deleteIndex() {
		DeleteIndexRequest request = new DeleteIndexRequest("does_not_exist");
		request = Requests.deleteIndexRequest("does_not_exist");
		request.timeout(TimeValue.timeValueMinutes(2));
		request.timeout("2m");

		request.masterNodeTimeout(TimeValue.timeValueMinutes(1));
		request.masterNodeTimeout("1m");

		request.indicesOptions(IndicesOptions.lenientExpandOpen());

		try {
			DeleteIndexResponse deleteIndexResponse = client.indices().delete(request);
			System.out.println("deleteIndexResponse=" + deleteIndexResponse);
		} catch (ElasticsearchException e) {
			if (e.status() == RestStatus.NOT_FOUND) {
				System.out.println("索引不存在，不能删除");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// client.indices().deleteAsync(request, new
		// ActionListener<DeleteIndexResponse>() {
		// @Override
		// public void onResponse(DeleteIndexResponse deleteIndexResponse) {
		//
		// }
		//
		// @Override
		// public void onFailure(Exception e) {
		//
		// }
		// });
	}
	
	@Test
	public void openIndex() {
		OpenIndexRequest request = new OpenIndexRequest("twitter");
		request = Requests.openIndexRequest("twitter");
		//可选
		request.timeout(TimeValue.timeValueMinutes(2)); 
		request.timeout("2m");
		request.masterNodeTimeout(TimeValue.timeValueMinutes(1)); 
		request.masterNodeTimeout("1m");
		request.waitForActiveShards(2); 
		request.waitForActiveShards(ActiveShardCount.ONE);
		
		request.indicesOptions(IndicesOptions.strictExpandOpen());
		
		try {
			OpenIndexResponse openIndexResponse = client.indices().open(request);
			//Indicates whether all of the nodes have acknowledged the request
			System.out.println("acknowledged ="+openIndexResponse.isAcknowledged());
			//Indicates whether the requisite number of shard copies were started for each shard in the index before timing out
			System.out.println("shardsAcknowledged ="+openIndexResponse.isShardsAcknowledged());
		} catch (IOException e1) {
			System.out.println("没有该索引");
		}
		
//		client.indices().openAsync(request,  new ActionListener<OpenIndexResponse>() {
//		    @Override
//		    public void onResponse(OpenIndexResponse openIndexResponse) {
//				System.out.println("acknowledged ="+openIndexResponse.isAcknowledged());
//				System.out.println("shardsAcknowledged ="+openIndexResponse.isShardsAcknowledged());
//		    }
//
//		    @Override
//		    public void onFailure(Exception e) {
//		        System.out.println("失败");
//		    }
//		});
	}
}
*/
