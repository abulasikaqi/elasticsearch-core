package org.elastic.service.base;

import com.alibaba.fastjson.JSONObject;
import org.elastic.common.constants.Constants;
import org.elastic.mappings.MappingsProperties;
import org.elastic.search.BaseSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by LL on 2017/10/18.
 */
public class InitService extends BaseSearchService {

    private static final Logger logger = LoggerFactory.getLogger(InitService.class);

    /**
     * 判断索引是否存在，如果不存在则创建
     */
    protected void init() {
        initProductIndex();
    }

    private void initProductIndex() {
        try {
            // 判断索引是否存在
            if (!indexExists(Constants.PRODUCT_INDEX)) {

                logger.info("索引不存在，自动创建索引！");

                // 创建索引
                createIndex(Constants.PRODUCT_INDEX_TYPE);
                // 添加映射
//                XContentBuilder xContentBuilder = ProductMappings.getMapping();
//                logger.info(xContentBuilder.string());
//                createMapping(Constants._INDEX, Constants._PRODUCT_TYPE, xContentBuilder);

                // 获取索引mapping
                String mappingJson = MappingsProperties.getString("product.mapping");

                Map map = JSONObject.parseObject(mappingJson, Map.class);
                logger.info(map.toString());
                createMapping(Constants.PRODUCT_INDEX, Constants.PRODUCT_INDEX_TYPE, map);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}
