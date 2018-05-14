package org.elastic.service.base;

import com.alibaba.fastjson.JSONObject;
import org.elastic.common.constants.Constants;
import org.elastic.mappings.MappingsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by LL on 2017/10/18.
 */
public class InitService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(InitService.class);

    /**
     * 判断索引是否存在，如果不存在则创建
     */
    protected void init() {
        initProductIndex();
    }

    /**
     * 这是哪儿--索引
     */
    private void initProductIndex() {
        try {
            // 判断索引是否存在
            if (!indexExists(Constants.NJK_INDEX)) {
                // 创建索引
                createIndex(Constants.NJK_INDEX);
                // 添加映射
//                XContentBuilder xContentBuilder = ProductMappings.getMapping();
//                logger.info(xContentBuilder.string());
//                createMapping(Constants.NJK_INDEX, Constants.NJK_PRODUCT_TYPE, xContentBuilder);

                String mappingJson = MappingsProperties.getString("product.mapping");

                Map map = JSONObject.parseObject(mappingJson, Map.class);
                logger.info(map.toString());
                createMapping(Constants.NJK_INDEX, Constants.NJK_PRODUCT_TYPE, map);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}
