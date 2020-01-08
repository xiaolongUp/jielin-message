package com.jielin.message.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 排序工具类
 */
public class SortUtil {

    /**
     * 根据map的key排序，返回value集合
     */
    public static List<String> sortByMapKey(Map<String, String> map) {
        List<String> params = new ArrayList<>();
        if (map == null || map.isEmpty()) {
            return params;
        }
        //map的key转换成int
        Map<Integer, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (StringUtils.isNotBlank(entry.getKey())) {
                int orderNum = Integer.parseInt(entry.getKey());
                if (StringUtils.isNotBlank(entry.getValue())) {
                    result.put(orderNum, entry.getValue());
                }
            }
        }

        result.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()).forEachOrdered(e -> params.add(e.getValue()));
        return params;
    }


}
