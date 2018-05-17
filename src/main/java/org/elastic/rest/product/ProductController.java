package org.elastic.rest.product;

import com.alibaba.fastjson.JSONObject;
import org.elastic.common.util.HttpResult;
import org.elastic.model.product.ProductInfo;
import org.elastic.model.product.ProductSearchData;
import org.elastic.service.product.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elastic.common.constants.Constants.PRODUCT_INDEX;
import static org.elastic.common.constants.Constants.PRODUCT_INDEX_TYPE;

/**
 * Created by LL on 2017/10/11.
 */
@Path("product")
@Component
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 批量添加索引
     * @param indexData jsonList
     * @return json
     */
    @POST
    @Path("batchIndex")
    public String batchIndex(@FormParam("indexData") String indexData) {
        logger.info("product/batchIndex : indexData=" + indexData);

        if (StringUtils.isBlank(indexData)) {
            return JSONObject.toJSONString(HttpResult.put(false, "indexData not null!"));
        }

        List<Map> list = JSONObject.parseArray(indexData, Map.class);

        String result = productService.batchAdd(PRODUCT_INDEX, PRODUCT_INDEX_TYPE, list);

        logger.info(result);

        return result;
    }

    /**
     * 单个索引添加
     * @param indexData jsonList
     * @return json
     */
    @POST
    @Path("indexJson")
    public String indexJson(@FormParam("indexData") String indexData) {
        logger.info("product/index : indexData=" + indexData);

        if (StringUtils.isBlank(indexData)) {
            return JSONObject.toJSONString(HttpResult.put(false, "indexData not null!"));
        }

        Map data = JSONObject.parseObject(indexData, Map.class);

        String result = productService.add(PRODUCT_INDEX, PRODUCT_INDEX_TYPE, data);

        logger.info(result);

        return result;
    }

    /**
     * 单个索引添加
     * @param productId 产品ID
     * @param productType 产品类别
     * @param productName 产品名称
     * @return json
     */
    @POST
    @Path("indexParams")
    public String indexParams(@FormParam("productId") String productId,
                        @FormParam("productType") String productType,
                        @FormParam("productName") String productName) {
        logger.info("product/index : productId=" + productId + ",productType=" + productType + ",productName=" + productName);

        if (StringUtils.isBlank(productId) || StringUtils.isBlank(productType) || StringUtils.isBlank(productName)) {
            String error = "";
            if (StringUtils.isBlank(productId)) {
                error = "productId";
            } else if (StringUtils.isBlank(productType)) {
                error = "productType";
            } else if (StringUtils.isBlank(productName)) {
                error = "productName";
            }

            logger.error(String.format("%s can't null!", error));

            return JSONObject.toJSONString(HttpResult.put(false, String.format("%s can't null!", error)));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("productId", Integer.parseInt(productId));
        data.put("productType", Integer.parseInt(productType));
        data.put("productName", productName);

        // 执行添加
        String result = productService.add(PRODUCT_INDEX, PRODUCT_INDEX_TYPE, data);

        logger.info(result);

        return result;
    }

    /**
     * 搜索产品信息
     * @param jsonStr 产品搜索封装类
     * @param from 起始值
     * @param pageSize 查询数量
     * @param orderField 排序字段
     * @param order 排序方式
     * @return json
     */
    @POST
    @Path("search")
    public String search(@FormParam("jsonStr") String jsonStr,
                         @FormParam("from") int from,
                         @FormParam("pageSize") int pageSize,
                         @FormParam("orderField") String orderField,
                         @FormParam("order") String order) {

        logger.info("product/search : jsonStr=" + jsonStr);

        if (StringUtils.isBlank(jsonStr)) {
            return JSONObject.toJSONString(HttpResult.put(false, "jsonStr not null!"));
        }

        ProductSearchData data = JSONObject.parseObject(jsonStr, ProductSearchData.class);

        String result = productService.searchProduct(PRODUCT_INDEX, PRODUCT_INDEX_TYPE, data, from, pageSize, orderField, order);

        logger.info(result);
        return result;
    }

    /**
     * 更新产品信息
     * @param jsonData 产品
     * @return json
     */
    @Path("update")
    @POST
    public String update(@FormParam("jsonData") String jsonData) {
        logger.info("product/update : jsonData=" + jsonData);

        // 参数判断
        if (StringUtils.isBlank(jsonData)) {
            logger.error("jsonData not null");
            return JSONObject.toJSONString(HttpResult.put(false, "jsonData not null"));
        }

//        Map<String, ?> data = JSONObject.parseObject(jsonData, Map.class);
        ProductInfo data = JSONObject.parseObject(jsonData, ProductInfo.class);

        String result = productService.update(PRODUCT_INDEX, PRODUCT_INDEX_TYPE, data);

        logger.info(result);

        return result;
    }

    /**
     * 删除
     * @param productId 产品ID
     *                  @param productType  类别
     * @return json
     */
    @Path("delete")
    @POST
    public String delete(@FormParam("productId") String productId,
                         @FormParam("productType") String productType) {
        logger.info("product/update : productId=" + productId + ",productType=" + productType);

        // 参数判断
        if (StringUtils.isBlank(productType) || StringUtils.isBlank(productId)) {
            String error = "";
            if (StringUtils.isBlank(productId)) {
                error = "productId";
            } else if (StringUtils.isBlank(productType)) {
                error = "productType";
            }

            logger.error(String.format("%s can't null!", error));

            return JSONObject.toJSONString(HttpResult.put(false, String.format("%s can't null!", error)));
        }

        String result = productService.delete(PRODUCT_INDEX, PRODUCT_INDEX_TYPE, productId, productType);

        logger.info(result);

        return result;
    }


}
