package org.elastic.service.product;

import com.alibaba.fastjson.JSONObject;
import org.elastic.model.product.ProductInfo;
import org.elastic.model.product.ProductSearchData;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elastic.common.constants.Constants.PRODUCT_INDEX;
import static org.elastic.common.constants.Constants.PRODUCT_INDEX_TYPE;

/**
 * Created by LL on 2017/10/11.
 */
public class ProductServiceTest {

    TransportClient client;

    @Before
    public void setUp() throws Exception {

        Settings settings = Settings.builder()
                .put("cluster.name", "my-application")
                .put("client.transport.sniff", true)
                .build();

        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("10.0.1.220"), 9300))
        ;
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    @Test
    public void createIndex() throws Exception {

        IndicesAdminClient adminClient = client.admin().indices();
        CreateIndexRequest request = new CreateIndexRequest();
        request.index("product");

        adminClient.create(request).get();

    }


    @Test
    public void index() throws Exception {

        for (int i = 1; i < 10; i++) {
            Map<String, Object> params = new HashMap<>();
            params.put("productId", i);
            params.put("productName", "测试产品00" + i);
            params.put("productType", i);

            ProductService productService = new ProductService();
            String result = productService.add(PRODUCT_INDEX, PRODUCT_INDEX_TYPE, params);

            System.out.println(result);
        }
    }

    @Test
    public void update() throws Exception {
        ProductInfo info = new ProductInfo();
        info.setProductId(1);
        info.setProductType(1);
        info.setProductName("修改名额");

        String jsonData = JSONObject.toJSONString(info);

        System.out.println(jsonData);

        Map map = JSONObject.parseObject(jsonData, Map.class);

        System.out.println(map);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("productId", "1"));
        queryBuilder.must(QueryBuilders.termQuery("productType", "1"));

        SearchResponse res = client.prepareSearch(PRODUCT_INDEX).setTypes(PRODUCT_INDEX_TYPE).setSize(1)
                .setQuery(queryBuilder).get();

        System.out.println(res.status().getStatus());

        System.out.println(res.getHits().getAt(0).getId());

        System.out.println(RestStatus.OK.getStatus());

        ProductSearchData searchData = new ProductSearchData();

        List<String> mustList = new ArrayList<>();
        mustList.add("产品");

        searchData.setMustProductName(mustList);

        System.out.println(JSONObject.toJSONString(searchData));

    }

    @Test
    public void indexTest() throws Exception {
        ProductInfo info = new ProductInfo();
        info.setProductId(1);
        info.setProductType(2);
        info.setProductName("产品添加测试");
        info.setUnit("克");
        info.setBrief("tweet 域产生两个词条 black 和 cat ， tag 域产生单独的词条 Black-cats 。换句话说，我们的映射正常工作。");
        info.setPicUrls("https://www.elastic.co/guide/cn/elasticsearch/guide/current/mapping-intro.html");
        info.setPrice(20.36);
        info.setStatus(1);

        System.out.println(JSONObject.toJSONString(info));
    }

}