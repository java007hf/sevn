package com.xsoft.sevn.dtk;

import com.xsoft.sevn.utils.GetJson;
import com.xsoft.sevn.utils.URLSet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DTKDateRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DTKDateRequest.class);
    private String URL = "https://dtkapi.ffquan.cn/go_getway/proxy/search?platform=1&page=1&px=zh&sortType=1&zsUid=57476&tmcs=1&version=1&api_v=1&flow_identifier=normal";

    public void requestJson(int index, int zsUID) {
        String addIndexURL = URLSet.replaceAccessTokenReg(URL, "page", index + "");
        String requestURL = URLSet.replaceAccessTokenReg(addIndexURL, "zsUid", zsUID + "");

        LOGGER.info("requestURL = " + requestURL);
        JSONObject stock = new GetJson().getHttpJson(requestURL,1);
        process(stock, index, zsUID);
    }

    private void process(JSONObject stock, int currentIndex, int zsUID) {
        JSONObject searchData = stock.getJSONObject("data").getJSONObject("search");
        int totalSize = searchData.getInt("total");
        int pageSize = totalSize/100;
        if (totalSize%100 != 0) pageSize++;

        JSONArray listArray = searchData.getJSONArray("list");

        LOGGER.info("totalSize = " + totalSize);
        LOGGER.info("pageSize = " + pageSize);
        LOGGER.info("currentIndex = " + currentIndex);

        for (int i=0; i<listArray.length (); i++) {
            JSONObject commoditJSON = listArray.getJSONObject (i);

            Commodit commodit = new Commodit ();
            commodit.brand_name = commoditJSON.getString ("brand_name");
            commodit.brand_id = commoditJSON.getString ("brand_id");
            commodit.add_time = commoditJSON.getString ("add_time");
            commodit.category_name = commoditJSON.getString ("category_name");
            commodit.cid = Integer.parseInt (commoditJSON.getString ("cid"));
            commodit.commission_rate = Float.parseFloat (commoditJSON.getString ("commission_rate"));
            commodit.coupon_amount = Float.parseFloat (commoditJSON.getString ("coupon_amount"));
            commodit.coupon_end_time = commoditJSON.getString ("coupon_end_time");
            commodit.coupon_end_time_stamp = commoditJSON.getInt ("coupon_end_time_stamp");
            commodit.coupon_num = Integer.parseInt (commoditJSON.getString ("coupon_num"));
            commodit.coupon_over = Integer.parseInt (commoditJSON.getString ("coupon_over"));
            commodit.d_title = commoditJSON.getString ("d_title");
            commodit.id = commoditJSON.getString ("id");
            commodit.is_chaoshi = Boolean.parseBoolean (commoditJSON.getString ("is_chaoshi"));
            commodit.is_choice = Boolean.parseBoolean (commoditJSON.getString ("is_choice"));
            commodit.is_tmall = Boolean.parseBoolean (commoditJSON.getString ("is_tmall"));
            commodit.main_pic = commoditJSON.getString ("main_pic");
            commodit.original_price = Float.parseFloat (commoditJSON.getString ("original_price"));
            commodit.price = Float.parseFloat (commoditJSON.getString ("price"));
            commodit.shop_name = commoditJSON.getString ("shop_name");
            commodit.commission = (float) (commodit.price * commodit.commission_rate * 0.01);

            saveDB(commodit);
        }

        if (pageSize > currentIndex) {
            requestJson(currentIndex+1, zsUID);
        }
    }

    private void saveDB(Commodit commodit) {
        LOGGER.info("commodit = " + commodit);
    }
}
