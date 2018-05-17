package org.elastic.model.product;

import org.elastic.common.es.QuerybuilderHelper;
import lombok.Data;
import org.elastic.search.SearchData;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.List;

/**
 * 产品--搜索类
 * Created by LL on 2017/10/13.
 */
@Data
public class ProductSearchData extends SearchData {

    private List<String> mustProductName;

    private List<String> shouldProductName;

    private List<String> mustNotProductName;

    @Override
    public BoolQueryBuilder builder() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        if (mustProductName != null || shouldProductName != null || mustNotProductName != null) {

            BoolQueryBuilder builder = QuerybuilderHelper.queryStringBuilder(mustProductName, shouldProductName, mustNotProductName, "productName");
            queryBuilder.must(builder);
        }
        return queryBuilder;
    }

}
