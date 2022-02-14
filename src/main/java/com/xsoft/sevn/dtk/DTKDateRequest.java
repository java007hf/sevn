package com.xsoft.sevn.dtk;

import com.xsoft.sevn.utils.GetJson;
import com.xsoft.sevn.utils.URLSet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;

@Component
public class DTKDateRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DTKDateRequest.class);
    private String URL = "https://dtkapi.ffquan.cn/go_getway/proxy/search?platform=1&page=1&px=zh&sortType=1&zsUid=57476&tmcs=1&version=1&api_v=1&flow_identifier=normal";

    public void requestJson(int index) {
        String requestURL = URLSet.replaceAccessTokenReg(URL, "page", index + "");
        LOGGER.info("requestURL = " + requestURL);
        JSONObject stock = new GetJson().getHttpJson(requestURL,1);
        process(stock, index);
    }

    private void process(JSONObject stock, int currentIndex) {
        JSONObject searchData = stock.getJSONObject("data").getJSONObject("search");
        int totalSize = searchData.getInt("total");
        int pageSize = totalSize/100;
        if (totalSize%100 != 0) pageSize++;

        LOGGER.info("pageSize = " + pageSize);

        JSONArray listArray = searchData.getJSONArray("list");


        if (pageSize > currentIndex) {
            requestJson(currentIndex+1);
        }
    }

    private void saveDB(Commodit commodit) {

    }
}
