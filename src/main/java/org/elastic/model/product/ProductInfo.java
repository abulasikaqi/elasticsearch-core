package org.elastic.model.product;

import lombok.Data;

/**
 * Created by LL on 2017/10/13.
 */
@Data
public class ProductInfo {
    /**
     * 产品ID
     */
    private Integer productId;

    /**
     * 产品类型
     *
     * 1餐饮 2游玩 3住宿 4特产 5农家菜
     */
    private Integer productType;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品状态
     *
     * 0待审核，1上架，2无货，3下架
     */
    private Integer status;

    /**
     * 产品单价
     */
    private Double price;

    /**
     * 产品描述
     */
    private String brief;

    /**
     * 产品图片s
     */
    private String picUrls;

    /**
     * 单位
     */
    private String unit;

}
