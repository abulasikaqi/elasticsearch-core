package org.elastic.search;

import com.alibaba.fastjson.JSONObject;
import org.elastic.common.es.ClientHelper;
import org.elastic.common.util.CommonUtils;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 通用接口父类
 * Created by LL on 2017/10/11.
 */
public class BaseSearchService {

    private static final Logger logger = LoggerFactory.getLogger(BaseSearchService.class);

    private TransportClient client;

    public BaseSearchService() {
        this.client = ClientHelper.getTcClient();
    }

    /**
     * 判断主索引是否存在
     * @param indexName 主索引名称
     * @return bo
     */
    protected boolean indexExists(String indexName) {

        return client.admin().indices().prepareExists(indexName).get().isExists();
    }

    /**
     * 创建索引库
     * @param indexName 索引库名称
     */
    protected void createIndex(String indexName) {

        try {
            client.admin().indices().prepareCreate(indexName).get();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 添加映射
     * @param indexName 索引名称
     * @param indexType 索引类别
     * @param builder 映射
     */
    public void createMapping(String indexName, String indexType, XContentBuilder builder){
        try {
            PutMappingRequest mapping = Requests.putMappingRequest(indexName)
                    .type(indexType)
                    .source(builder);
            client.admin().indices().putMapping(mapping).actionGet();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 添加映射
     * @param indexName 索引名称
     * @param indexType 索引类别
     * @param mappingSource 映射
     */
    public void createMapping(String indexName, String indexType, Map mappingSource){
        try {
            PutMappingRequest mapping = Requests.putMappingRequest(indexName)
                    .type(indexType)
                    .source(mappingSource);
            client.admin().indices().putMapping(mapping).actionGet();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 添加数据
     * @param indices indices
     * @param indexType type
     * @param source source
     * @return esId
     */
    protected String index(String indices, String indexType, Map<String, Object> source) {
        try {
            IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indices, indexType).setSource(source);

            logger.info("index |--> " + indexRequestBuilder.toString());

            return indexRequestBuilder.get().getId();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * 查询数据是否已经存在
     *
     * @param indexName 索引名
     * @param indexType 索引类别
     * @return bool
     */
    protected boolean dataExists(String indexName, String indexType,  QueryBuilder queryBuilder) {

        SearchHits result = search(indexName, indexType, queryBuilder);

        return result != null && result.getTotalHits() > 0;
    }

    /**
     * 搜索
     * @param indices 索引
     * @param indexType 类别
     * @param queryBuilder query
     * @return 结果集
     */
    private SearchHits search(String indices, String indexType, QueryBuilder queryBuilder) {
        return search(indices, indexType, queryBuilder, null, 0, 0);
    }

    /**
     * 搜索
     * @param indices 索引
     * @param indexType 类别
     * @param queryBuilder 查询条件
     * @param sortBuilders 排序方式
     * @param from 起始数
     * @param size 查询数量
     * @return hits
     */
    protected SearchHits search(String indices, String indexType, QueryBuilder queryBuilder,
                                List<FieldSortBuilder> sortBuilders, int from, int size) {
        try {
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indices)
                    .setTypes(indexType)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setFrom(from)
                    .setSize(size)
                    .setQuery(queryBuilder);

            if(CommonUtils.isNotEmpty(sortBuilders)){
                for (FieldSortBuilder fieldSortBuilder : sortBuilders) {
                    searchRequestBuilder.addSort(fieldSortBuilder);
                }
            }

            System.out.println("curl:" + searchRequestBuilder.toString());

            SearchResponse response = searchRequestBuilder.get();

            int status = response.status().getStatus();
            if (status == RestStatus.OK.getStatus()) {
                return response.getHits();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    /**
     * 刷新
     * @param indices 索引
     */
    protected void refreshIndex(String indices) {
        try {
            // 刷新索引
            client.admin().indices().prepareRefresh(indices).get();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


    /**
     * 更新
     * @param indices 索引
     * @param indexType 类别
     * @param esId esId
     * @param source data
     * @return bo
     */
    protected boolean update(String indices, String indexType, String esId, Map<String, ?> source) {
        int status = 0;
        try {
            status = client.prepareUpdate(indices, indexType, esId)
                    .setDoc(source)
                    .get()
                    .status().getStatus();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return status == 200;
    }

    /**
     * 更新
     * @param indices 索引
     * @param indexType 类别
     * @param esId esId
     * @param data data
     * @return bo
     */
    protected boolean update(String indices, String indexType, String esId, Object data) {
        int status = 0;
        try {
            status = client.prepareUpdate(indices, indexType, esId)
                    .setDoc(JSONObject.toJSONString(data), XContentType.JSON)
                    .get()
                    .status().getStatus();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return status == 200;
    }

    /**
     * 删除
     * @param indices 索引
     * @param indexType 类别
     * @param esId esId
     * @return bo
     */
    protected boolean delete(String indices, String indexType, String esId) {
        int status = 0;
        try {
            status = client.prepareDelete(indices, indexType, esId)
                    .get()
                    .status().getStatus();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return status == 200;
    }


    /**
     * 根据条件删除
     * @param indices 索引
     * @param query query
     * @return 数量
     */
    protected long deleteByQuery(String indices, QueryBuilder query) {

        BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(query)
                .source(indices)
                .get();

        return response.getDeleted();
    }

    /**
     * 批量操作
     * @param request source
     * @return esId
     */
    protected long bulkIndex(BulkRequest request) {
        try {
            // 5.0 getTookInMillis();
            return client.bulk(request).get().getIngestTookInMillis();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0;
    }

}
