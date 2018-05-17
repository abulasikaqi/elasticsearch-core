package org.elastic.search;

import org.elasticsearch.index.query.BoolQueryBuilder;

/**
 * Created by LL on 2018/5/17 0017.
 */
public abstract class SearchData {

    public abstract BoolQueryBuilder builder();
}
