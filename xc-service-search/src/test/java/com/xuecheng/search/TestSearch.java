package com.xuecheng.search;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSearch {
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    RestClient restClient;

    @Test
    public void testSearchAll() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置搜索方式
        //matchAllQuery表示搜索全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //设置源字段过滤，
        //第一个参数结果表示包括那些字段，第二个参数表示结果集不包括那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索，向ES发起请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //设置日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //获得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //由于前边设器了源文档字段过虑，这时description是取不到的
            //String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            //价格
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
        }
    }

    //分页查询
    @Test
    public void testSearchPage() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
        //页码
        int page = 1;
        //每页的记录数
        int size = 1;
        //计算记录的起始下标
        int from = (page-1)*size;
        searchSourceBuilder.from(from);//起始记录，下标从0开始
        searchSourceBuilder.size(size);//每页显示的记录数

        //matchAllQuery表示搜索全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //设置源字段过滤，
        //第一个参数结果表示包括那些字段，第二个参数表示结果集不包括那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索，向ES发起请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //设置日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //获得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //由于前边设器了源文档字段过虑，这时description是取不到的
            //String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            //价格
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
        }
    }

    //分页查询
    @Test
    public void testTermQuery() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
        //页码
        int page = 1;
        //每页的记录数
        int size = 1;
        //计算记录的起始下标
        int from = (page-1)*size;
        searchSourceBuilder.from(from);//起始记录，下标从0开始
        searchSourceBuilder.size(size);//每页显示的记录数

        //matchAllQuery表示搜索全部
        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //设置源字段过滤，
        //第一个参数结果表示包括那些字段，第二个参数表示结果集不包括那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索，向ES发起请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //设置日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //获得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //由于前边设器了源文档字段过虑，这时description是取不到的
            //String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            //价格
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
        }
    }
    //根据id查询
    @Test
    public void testTermQueryById() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
        //页码
        int page = 1;
        //每页的记录数
        int size = 1;
        //计算记录的起始下标
        int from = (page-1)*size;
        searchSourceBuilder.from(from);//起始记录，下标从0开始
        searchSourceBuilder.size(size);//每页显示的记录数

        //根据id查询
        String[] ids = new String[]{"1","2"};
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id",ids));
        //设置源字段过滤，
        //第一个参数结果表示包括那些字段，第二个参数表示结果集不包括那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索，向ES发起请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //设置日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //获得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //由于前边设器了源文档字段过虑，这时description是取不到的
            //String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            //价格
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
        }
    }

    //matchQuery()
    @Test
    public void testMatchQuery() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
/*        //页码
        int page = 1;
        //每页的记录数
        int size = 1;
        //计算记录的起始下标
        int from = (page-1)*size;
        searchSourceBuilder.from(from);//起始记录，下标从0开始
        searchSourceBuilder.size(size);//每页显示的记录数*/

        //根据id查询
//        String[] ids = new String[]{"1","2"};
        //matchQuery
        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发框架")
                                                            .minimumShouldMatch("80%"));
        //设置源字段过滤，
        //第一个参数结果表示包括那些字段，第二个参数表示结果集不包括那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索，向ES发起请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //设置日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //获得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //由于前边设器了源文档字段过虑，这时description是取不到的
            //String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            //价格
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
        }
    }
    //MultiMatchQuery
    @Test
    public void testMultiMatchQuery() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置搜索方式
        //matchAllQuery表示搜索全部
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring css","name","description")
                .minimumShouldMatch("50%")
                .field("name",10));
        //设置源字段过滤，
        //第一个参数结果表示包括那些字段，第二个参数表示结果集不包括那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索，向ES发起请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //设置日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //获得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //由于前边设器了源文档字段过虑，这时description是取不到的
            //String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            //价格
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
        }
    }

    //BoolQuery
    @Test
    public void testBoolQuery() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置搜索方式
        //BoolQuery表示搜索全部
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");
        //定义一个boolquery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        //设置源字段过滤，
        //第一个参数结果表示包括那些字段，第二个参数表示结果集不包括那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索，向ES发起请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //设置日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //获得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //由于前边设器了源文档字段过虑，这时description是取不到的
            //String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            //价格
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
        }
    }

    //filter
    @Test
    public void testFilter() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置搜索方式
        //BoolQuery表示搜索全部
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");
        //定义一个boolquery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
//        boolQueryBuilder.must(termQueryBuilder);
        //定义一个过滤器
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","201001"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(90).lte(100));
        searchSourceBuilder.query(boolQueryBuilder);
        //设置源字段过滤，
        //第一个参数结果表示包括那些字段，第二个参数表示结果集不包括那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索，向ES发起请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //设置日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //获得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //由于前边设器了源文档字段过虑，这时description是取不到的
            //String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            //价格
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
        }
    }

    //sort
    @Test
    public void testSort() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //定义一个boolquery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //定义一个过滤器
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));

        searchSourceBuilder.query(boolQueryBuilder);
        //添加排序
        searchSourceBuilder.sort("studymodel", SortOrder.DESC);
        searchSourceBuilder.sort("price", SortOrder.ASC);
        //设置源字段过滤，
        //第一个参数结果表示包括那些字段，第二个参数表示结果集不包括那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索，向ES发起请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //设置日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //获得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //由于前边设器了源文档字段过虑，这时description是取不到的
            //String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            //价格
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
        }
    }

    //HighLight
    @Test
    public void testHighLight() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置搜索方式
        //BoolQuery表示搜索全部
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("开发框架", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");
        //定义一个boolquery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);

        //定义一个过滤器

        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));
        searchSourceBuilder.query(boolQueryBuilder);
        //设置源字段过滤，
        //第一个参数结果表示包括那些字段，第二个参数表示结果集不包括那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});

        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();

        highlightBuilder.preTags("<tag>");
        highlightBuilder.postTags("</tag>");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
//        highlightBuilder.fields().add(new HighlightBuilder.Field("description"));
        searchSourceBuilder.highlighter(highlightBuilder);

        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索，向ES发起请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //设置日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //获得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //源文档的name字段内容
            String name = (String) sourceAsMap.get("name");
            //取出name的高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields!=null){
                //取出name的高亮字段
                HighlightField nameHighlightField = highlightFields.get("name");
                if(nameHighlightField!=null){
                    Text[] fragments = nameHighlightField.getFragments();
                    StringBuffer stringBuffer = new StringBuffer();

                    for (Text text : fragments) {
                        stringBuffer.append(text);
                    }
                    name = stringBuffer.toString();
                }
            }
            //由于前边设器了源文档字段过虑，这时description是取不到的
            //String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            //价格
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
        }
    }
}
