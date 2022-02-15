package com.xsoft.sevn.dtk;

import java.util.HashMap;

public class CIDType {
    public static final HashMap<Integer, String> CIDMap = new HashMap<Integer, String>() {
        {
            CIDMap.put(4, "居家日用");
            CIDMap.put(6, "美食");
            CIDMap.put(2, "母婴");
            CIDMap.put(3, "美妆");
            CIDMap.put(1, "女装");
            CIDMap.put(8, "数码家电");
            CIDMap.put(7, "文娱车品");
            CIDMap.put(10, "内衣");
            CIDMap.put(14, "家装家纺");
            CIDMap.put(5, "鞋品");
            CIDMap.put(9, "男装");
            CIDMap.put(12, "配饰");
            CIDMap.put(13, "户外运动");
            CIDMap.put(11, "箱包");
        }
    };
}
