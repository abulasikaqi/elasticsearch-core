package org.elastic.mappings;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

/**
 * Created by LL on 2017/10/18.
 */
public class ProductMappings {

    public static void main(String[] args) throws IOException {

        XContentBuilder m = getMapping();


        System.out.println(m.string());
    }

    public static XContentBuilder getMapping() {
        XContentBuilder builder = null;

        try {
            builder = XContentFactory.jsonBuilder().startObject()
//                    .startObject("_ttl").field("enabled", false).endObject()

                    .startObject("properties")
                        .startObject("productId")
                            .field("type", "integer")
                        .endObject()
                        .startObject("productType")
                            .field("type", "integer")
                        .endObject()
                        .startObject("productName")
                            .field("type", "text")
                            .startObject("fields")
                            .startObject("keyword").field("type", "keyword").endObject()
                            .endObject()
                        .endObject()
                        .startObject("status")
                            .field("type", "integer")
                        .endObject()
                        .startObject("price")
                            .field("type", "float")
                        .endObject()
                        .startObject("brief")
                            .field("type", "text")
                        .endObject()
                        .startObject("picUrls")
                            .field("type", "text")
                            .field("index",false)
                        .endObject()
                        .startObject("unit")
                            .field("type", "keyword")
                        .endObject()
                    .endObject()
            .endObject();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder;
    }
}
