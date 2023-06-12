package Utils;


import com.Dao.Info;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.*;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class EsUtil {
    private void EsUtil(){}
    private static RestHighLevelClient client;
    static{
        //获取RestClient连接
        //注意：高级别客户端其实是对低级别客户端的代码进行了封装，所以连接池使用的是低级别客户端中的连接池
        client = new RestHighLevelClient(
                //这里的代码其实就是低级别客户端的代码
                RestClient.builder(
                                new HttpHost("bigdata01",9200,"http"),
                                new HttpHost("bigdata02",9200,"http"),
                                new HttpHost("bigdata03",9200,"http"))
                        .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                            @Override
                            public HttpAsyncClientBuilder customizeHttpClient(
                                    HttpAsyncClientBuilder httpClientBuilder) {
                                return httpClientBuilder.setDefaultIOReactorConfig(
                                        IOReactorConfig.custom()
                                                //设置线程池中线程的数量，默认是1个，建议设置为和客户端机器可用CPU数量一致
                                                .setIoThreadCount(1)
                                                .build());
                            }
                        }));
    }

    /**
     * 获取客户端
     * @return
     */
    public static RestHighLevelClient getRestClient(){
        return client;
    }

    /**
     * 关闭客户端
     * 注意：调用高级别客户端的close方法时，会将低级别客户端创建的连接池整个关闭掉，最终导致client无法继续使用
     * 所以正常是用不到这个close方法的，只有在程序结束的时候才需要调用
     * @throws IOException
     */
    public static void closeRestClient() throws IOException {
        client.close();
    }

    /**
     * 建立索引
     * @param index
     * @param id
     * @param map
     * @throws Exception
     */
    public static void addIndex(String index, String id,Map<String,String> map) throws IOException {
        IndexRequest request = new IndexRequest(index);
        request.id(id);
        request.source(map);
        //执行
        client.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 全文检索功能
     * @param key
     * @param index
     * @return
     * @throws IOException
     */
    public static Info[] search(String key, String index) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        //指定索引库，支持指定一个或者多个，也支持通配符
        searchRequest.indices(index);

        //指定searchType
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);

        //组装查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //如果传递了搜索参数，则拼接查询条件
        if(StringUtils.isNotBlank(key)){
            searchSourceBuilder.query(QueryBuilders.multiMatchQuery(key,"title","describe","content"));
        }
//        //分页
//        searchSourceBuilder.from(start);
//        searchSourceBuilder.size(row);

        //高亮
        //设置高亮字段
        HighlightBuilder highlightBuilder =  new HighlightBuilder()
                .field("title")
                .field("describe");//支持多个高亮字段
        //设置高亮字段的前缀和后缀内容
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");
        searchSourceBuilder.highlighter(highlightBuilder);
        searchSourceBuilder.sort("time", SortOrder.DESC);
        searchSourceBuilder.size(10000); // 设置最大返回结果数

        //指定查询条件
        searchRequest.source(searchSourceBuilder);

        //执行查询操作
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        List<Info> infoList = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            String id = hit.getId();
            StringBuilder title = new StringBuilder((String) hit.getSourceAsMap().get("title"));
            String author = (String) hit.getSourceAsMap().get("author");
            StringBuilder description = new StringBuilder((String) hit.getSourceAsMap().get("describe"));

            HighlightField highlightField = hit.getHighlightFields().get("title");
            if (highlightField != null) {
                Text[] fragments = highlightField.getFragments();
                title = new StringBuilder();
                for (Text text : fragments) {
                    title.append(text);
                }
            }

            HighlightField highlightField2 = hit.getHighlightFields().get("describe");
            if (highlightField2 != null) {
                Text[] fragments = highlightField2.getFragments();
                description = new StringBuilder();
                for (Text text : fragments) {
                    description.append(text);
                }
            }

            Info info = new Info(id, title.toString(), author, description.toString());
            infoList.add(info);
        }

        return infoList.toArray(new Info[0]);

//        //存储返回给页面的数据
//        Map<String, Object> map = new HashMap<String, Object>();
//        //获取查询返回的结果
//        SearchHits hits = searchResponse.getHits();
//        //获取数据总量
//        long numHits = hits.getTotalHits().value;
//        map.put("count",numHits);
//        ArrayList<Info> arrayList = new ArrayList<>();
//        //获取具体内容
//        SearchHit[] searchHits = hits.getHits();
//        //迭代解析具体内容
//        for (SearchHit hit: searchHits) {
//            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            String id = hit.getId();
//            String title = sourceAsMap.get("title").toString();
//            String author = sourceAsMap.get("author").toString();
//            String describe = sourceAsMap.get("describe").toString();
//            //String time = sourceAsMap.get("time").toString();
//
//            //获取高亮字段内容
//            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
//            //获取title字段的高亮内容
//            HighlightField highlightField = highlightFields.get("title");
//            if(highlightField!=null){
//                Text[] fragments = highlightField.getFragments();
//                title = "";
//                for (Text text : fragments) {
//                    title += text;
//                }
//            }
//            //获取describe字段的高亮内容
//            HighlightField highlightField2 = highlightFields.get("describe");
//            if(highlightField2!=null){
//                Text[] fragments = highlightField2.fragments();
//                describe = "";
//                for (Text text : fragments) {
//                    describe += text;
//                }
//            }
//            //把文章信息封装到Article对象中
//            Info article = new Info();
//            article.setId(id);
//            article.setTitle(title);
//            article.setAuthor(author);
//            article.setDescribe(describe);
//            //article.setTime(time);
//            //最后再把拼装好的article添加到list对象汇总
//            arrayList.add(article);
//        }
//        map.put("dataList",arrayList);
//        return map;
    }
}
