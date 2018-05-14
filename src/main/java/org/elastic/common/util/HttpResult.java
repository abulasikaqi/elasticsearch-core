package org.elastic.common.util;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by LL on 2017/4/14.
 */
public class HttpResult {

    public final static Map<String, Object> put(final boolean bool, final Object obj) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("success", bool);

        paramsMap.put("result", obj);

        return paramsMap;
    }
}
