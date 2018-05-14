package org.elastic.service.product;

import com.alibaba.fastjson.JSONObject;
import org.elastic.common.es.QuerybuilderHelper;
import org.elastic.common.es.SourcesBuilder;
import org.elastic.common.util.HttpResult;
import org.elastic.model.product.ProductInfo;
import org.elastic.model.product.ProductSearchData;
import org.elastic.service.base.BaseService;
import org.elastic.common.constants.Constants;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 产品信息索引操作
 * Created by LL on 2017/10/11.
 */
@Service
public class ProductService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService() {
        super();
    }

    /**
     * 创建单个索引
     *
     * @param data source
     * @return esId
     */
    public String add(Map<String, Object> data) {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("productId", data.get("productId")));
        queryBuilder.must(QueryBuilders.termQuery("productType", data.get("productType")));

        // 如果索引库不存在，则创建索引库
        if (!indexExists(Constants.NJK_INDEX)) {
            // 系统内部错误,索引库不存在
            return JSONObject.toJSONString(HttpResult.put(false, "system error, index not exists."));
        }
        // 去重判断
        if (dataExists(Constants.NJK_INDEX, Constants.NJK_PRODUCT_TYPE, queryBuilder)) {
            return JSONObject.toJSONString(HttpResult.put(false, "index is already exists --> " + data));
        }

        // 添加数据
        String esId = index(Constants.NJK_INDEX, Constants.NJK_PRODUCT_TYPE, data);
        if (esId != null) {
            // 刷新索引
            refreshIndex(Constants.NJK_INDEX);
            return JSONObject.toJSONString(HttpResult.put(true, esId));
        } else {
            // 系统内部错误
            return JSONObject.toJSONString(HttpResult.put(false, "system error"));
        }
    }

    /**
     * 批量添加
     *
     * @param source source
     * @return esId
     */
    public String batchAdd(List<Map> source) {
        BulkRequest request = new BulkRequest();

        for (Map data : source) {
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.index(Constants.NJK_INDEX).type(Constants.NJK_PRODUCT_TYPE).source(data);

            request.add(indexRequest);
        }

        long count = super.bulkIndex(request);

        // 刷新索引
        refreshIndex(Constants.NJK_INDEX);

        return  JSONObject.toJSONString(HttpResult.put(true, count));

    }

    /**
     * 搜索产品信息
     * @param data 产品搜索封装类
     * @param from 起始值
     * @param pageSize 查询数量
     * @param orderField 排序字段
     * @param order 排序方式
     * @return json
     */
    public String searchProduct(ProductSearchData data, int from, int pageSize, String orderField, String order) {
        pageSize = pageSize == 0 ? 20 : pageSize;
        // 排序
        List<FieldSortBuilder> sort = QuerybuilderHelper.getFieldSortBuilders(orderField, order);

        // 查询
        SearchHits searchHits = super.search(Constants.NJK_INDEX, Constants.NJK_PRODUCT_TYPE, data.builder(), sort, from, pageSize);

        // 结果为空
        if (searchHits == null) {
            return JSONObject.toJSONString(HttpResult.put(false, "No such results!"));
        }

        // 获取结果集
        SearchHit[] hits = searchHits.getHits();

        // 封装数据
        List<ProductInfo> result = new ArrayList<>();
        for (SearchHit hit : hits) {
            result.add(SourcesBuilder.getProductInfo(hit.getSource()));
        }

        return JSONObject.toJSONString(HttpResult.put(true, result));
    }

    /**
     * 更新索引
     * @param data 数据
     * @return json
     */
    public String update(Map<String, ?> data) {

        String esId = null;
        try {
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.must(QueryBuilders.termQuery("productId", data.get("productId")));
            queryBuilder.must(QueryBuilders.termQuery("productType", data.get("productType")));

            SearchHits searchHits = super.search(Constants.NJK_INDEX, Constants.NJK_PRODUCT_TYPE, queryBuilder, null, 0, 1);
            // 结果为空
            if (searchHits == null || searchHits.getTotalHits() == 0) {
                return JSONObject.toJSONString(HttpResult.put(false, "product not exists!"));
            }

            esId = searchHits.getAt(0).getId();

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        // 执行
        boolean bo = super.update(Constants.NJK_INDEX, Constants.NJK_PRODUCT_TYPE, esId, data);

        if (bo) {
            // 刷新索引
            refreshIndex(Constants.NJK_INDEX);
            return JSONObject.toJSONString(HttpResult.put(true, esId));
        } else {
            return JSONObject.toJSONString(HttpResult.put(false, "update error"));
        }
    }

    /**
     * 更新索引
     * @param data 数据
     * @return json
     */
    public String update(ProductInfo data) {

        String esId = null;
        try {
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.must(QueryBuilders.termQuery("productId", data.getProductId()));
            queryBuilder.must(QueryBuilders.termQuery("productType", data.getProductType()));

            SearchHits searchHits = super.search(Constants.NJK_INDEX, Constants.NJK_PRODUCT_TYPE, queryBuilder, null, 0, 1);
            // 结果为空
            if (searchHits == null || searchHits.getTotalHits() == 0) {
                return JSONObject.toJSONString(HttpResult.put(false, "product not exists!"));
            }

            esId = searchHits.getAt(0).getId();

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        // 执行
        boolean bo = super.update(Constants.NJK_INDEX, Constants.NJK_PRODUCT_TYPE, esId, data);

        if (bo) {
            // 刷新索引
            refreshIndex(Constants.NJK_INDEX);
            return JSONObject.toJSONString(HttpResult.put(true, esId));
        } else {
            return JSONObject.toJSONString(HttpResult.put(false, "update error"));
        }
    }

    public String delete(String productId, String productType) {
        String esId = null;
        try {
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.must(QueryBuilders.termQuery("productId", productId));
            queryBuilder.must(QueryBuilders.termQuery("productType", productType));

            SearchHits searchHits = super.search(Constants.NJK_INDEX, Constants.NJK_PRODUCT_TYPE, queryBuilder, null, 0, 1);
            // 结果为空
            if (searchHits == null || searchHits.getTotalHits() == 0) {
                return JSONObject.toJSONString(HttpResult.put(false, "product not exists!"));
            }

            esId = searchHits.getAt(0).getId();

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        // 执行
        boolean bo = super.delete(Constants.NJK_INDEX, Constants.NJK_PRODUCT_TYPE, esId);
        if (bo) {
            // 刷新索引
            refreshIndex(Constants.NJK_INDEX);
            return JSONObject.toJSONString(HttpResult.put(true, esId));
        } else {
            return JSONObject.toJSONString(HttpResult.put(false, "delete error"));
        }
    }
}
