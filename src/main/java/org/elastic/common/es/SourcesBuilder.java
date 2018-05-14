package org.elastic.common.es;

import org.elastic.common.util.CommonUtils;
import org.elastic.model.product.ProductInfo;

import java.util.Map;

/**
 * Created by LL on 2017/10/13.
 */
public class SourcesBuilder {

    /**
     * 获取产品信息
     * @param source data
     * @return 产品信息
     */
    public static ProductInfo getProductInfo(Map<String, Object> source) {
        ProductInfo info = new ProductInfo();

        info.setProductId(getIntValue(source.get("productId")));
        info.setProductName(getStringValue(source.get("productName")));
        return info;
    }

    private static double getDoubleValue(Object object) {
        return CommonUtils.isEmpty(object) ? 0 : Double.parseDouble(object.toString());
    }


    private static Long getLongValue(Object object) {
        return CommonUtils.isEmpty(object) ? 0 : Long.parseLong(object.toString());
    }


    private static String getStringValue(Object object) {
        return CommonUtils.isEmpty(object) ? "" : object.toString();
    }


    private static int getIntValue(Object object) {
        return CommonUtils.isEmpty(object) ? 0 : Integer.parseInt(object.toString());
    }

    private static float getFloatValue(Object object) {
        return CommonUtils.isEmpty(object) ? 0 : Float.parseFloat(object.toString());
    }
}

