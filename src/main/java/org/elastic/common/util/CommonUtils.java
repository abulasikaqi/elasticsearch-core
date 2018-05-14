package org.elastic.common.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 工具类
 *
 * @author LL
 */
public class CommonUtils {


    /**
     * String\List\Set\Map为空判断 null "null" "" 都认为是true
     *
     * @param obj obj	
     * @return b
     */
    @SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object obj) {
        if (obj == null)
            return true;

        if (obj instanceof String) {
            String str = (String) obj;
            return str.trim().length() == 0 || str.toLowerCase().equals("null");
        } else if (obj instanceof List) {
            List ls = (List) obj;
            return ls.size() == 0;
        } else if (obj instanceof Set) {
            Set ls = (Set) obj;
            return ls.size() == 0;
        } else if (obj instanceof Map) {
            Map ls = (Map) obj;
            return ls.size() == 0;
        } else if (obj instanceof Object[]) {
            Object[] ls = (Object[]) obj;
            return ls.length == 0;
        }
        return false;
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static void main(String[] args) {
        System.out.println(2<<3);
    }
}
