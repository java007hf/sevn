package com.xsoft.sevn.utils;

import org.apache.commons.lang3.StringUtils;

public class URLSet {
    /**
     * 正则替换
     * @param url
     * @param name
     * @param accessToken
     * @return
     */
    public static String replaceValueByKey(String url, String name, String accessToken) {
        if (StringUtils.isNotBlank(url) && StringUtils.isNotBlank(accessToken)) {
            url = url.replaceAll("(" + name + "=[^&]*)", name + "=" + accessToken);
        }
        return url;
    }
}
