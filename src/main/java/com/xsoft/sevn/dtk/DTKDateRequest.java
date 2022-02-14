package com.xsoft.sevn.dtk;

import com.xsoft.sevn.utils.GetJson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;

@Component
public class DTKDateRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DTKDateRequest.class);
    private String URL = "https://dtkapi.ffquan.cn/go_getway/proxy/search?platform=1&page=1&px=zh&sortType=1&zsUid=57476&tmcs=1&version=1&api_v=1&flow_identifier=normal";

    public void requestJson() {
        JSONObject stock = new GetJson().getHttpJson(URL,1);
        LOGGER.info(stock.toString());
    }

    private void process(JSONObject stock) {

    }

    private void saveDB() {

    }
}
