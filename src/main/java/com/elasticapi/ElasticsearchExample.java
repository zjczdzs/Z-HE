package com.elasticapi;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;


public class ElasticsearchExample {
    public static void main(String[] args) throws IOException {
        // 创建客户端
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

        // 准备用于索引的文档数据
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("user", "GPT-4");
            builder.field("message", "Hello Elasticsearch");
            builder.timeField("timestamp", System.currentTimeMillis());
        }
        builder.endObject();

        // 发送索引请求
        IndexRequest request = new IndexRequest("my_index");
        request.id("1");
        request.source(builder);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);

        System.out.println(response);

        // 关闭客户端
        client.close();
    }
}
