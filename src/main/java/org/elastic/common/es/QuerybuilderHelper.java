package org.elastic.common.es;

import org.elastic.common.util.CommonUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL on 2017/10/13.
 */
public class QuerybuilderHelper {

    /**
     * 排序方式
     * @param orderField 排序字段
     * @param order 排序方式
     * @return sort
     */
    public static List<FieldSortBuilder> getFieldSortBuilders(String orderField, String order) {
        FieldSortBuilder fieldSortBuilder = null;
        // 排序字段
        if (CommonUtils.isNotEmpty(orderField) && !"SCORE".equals(orderField)) {
            fieldSortBuilder = SortBuilders.fieldSort(orderField);
            // 升降序
            if(order == null){
                order = "desc";
            }
            order = order.toLowerCase();
            if(CommonUtils.isNotEmpty(order)){
                if (SortOrder.DESC.toString().equals(order)) {
                    // 降序
                    fieldSortBuilder.order(SortOrder.DESC);
                } else if (SortOrder.ASC.toString().equals(order)) {
                    // 升序
                    fieldSortBuilder.order(SortOrder.ASC);
                }
            } else {
                // 默认降序
                fieldSortBuilder.order(SortOrder.DESC);
            }
        }

        if(CommonUtils.isNotEmpty(fieldSortBuilder)){
            List<FieldSortBuilder> sorts = new ArrayList<>();
            sorts.add(fieldSortBuilder);
            return sorts;
        }

        return null;
    }

    public static BoolQueryBuilder queryStringBuilder(List<String> must,
                                                      List<String> should,
                                                      List<String> mustNot,
                                                      String field) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (CommonUtils.isNotEmpty(must)) {
            BoolQueryBuilder mustBuilder = QueryBuilders.boolQuery();
            for (String str : must) {
                mustBuilder.must(QueryBuilders.queryStringQuery(str).defaultField(field));
            }
            boolQueryBuilder.must(mustBuilder);
        }

        if (CommonUtils.isNotEmpty(should)) {
            BoolQueryBuilder shouldBuilder = QueryBuilders.boolQuery();
            for (String str : should) {
                shouldBuilder.should(QueryBuilders.queryStringQuery(str).defaultField(field));
            }
            boolQueryBuilder.must(shouldBuilder);
        }

        if (CommonUtils.isNotEmpty(mustNot)) {
            BoolQueryBuilder mustNotBuilder = QueryBuilders.boolQuery();
            for (String str : mustNot) {
                mustNotBuilder.mustNot(QueryBuilders.queryStringQuery(str).defaultField(field));
            }
            boolQueryBuilder.must(mustNotBuilder);
        }

        return boolQueryBuilder;
    }
}
