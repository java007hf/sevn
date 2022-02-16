package com.xsoft.sevn.dtk;

import com.xsoft.sevn.utils.GetJson;
import com.xsoft.sevn.utils.GetRenderDoc;
import com.xsoft.sevn.utils.URLSet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class DTKDateRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DTKDateRequest.class);
    private String mToken = "";
    private GetRenderDoc mGetRenderDoc;

//    天猫超市
//    private String DTK_List_URL = "https://dtkapi.ffquan.cn/go_getway/proxy/search-v2?platform=1&page=1&px=zh&shop=2&version=2&api_v=1&flow_identifier=normal";
    //天猫
    private String DTK_List_URL = "https://dtkapi.ffquan.cn/go_getway/proxy/search-v2?platform=1&page=1&px=zh&tmall=1&version=2&api_v=1&flow_identifier=normal";
    //DTK推荐页面
    private static final String DTK_Item_URL = "https://dtkapi.ffquan.cn/taobaoapi/tpl-tpwd?gid=37259812&location=1&referer=&need_fav=-1&jaw_uid=K8dB7HRsOIHPyaYjkwoLGTSH0_zKZnl6P6rbRgsJZxq13Iy66y1V5bjLitRpBkLsNs-X9PJy3z-GNIyvofpw1A";

    @Autowired
    private CommoditMapper commoditMapper;

    public void init(String token) {
        mToken = token;
        mGetRenderDoc = new GetRenderDoc();
        mGetRenderDoc.initWebfolder ();
    }

    public void cleanData() {
//        commoditMapper.cleanData();
    }

    public void requestJson(int index) {
        String requestURL = URLSet.replaceAccessTokenReg(DTK_List_URL, "page", index + "");

        LOGGER.info("requestURL = " + requestURL);
        JSONObject stock = new GetJson().getHttpJson(requestURL,1);
        process(stock, index);
    }

    private void process(JSONObject stock, int currentIndex) {
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

            //获取商品JSON数据
            parseCommoditInfo(commodit, commoditJSON);

            //根据商品id 获取推荐的短连接
            parseContenteditable(commodit);

            saveDB(commodit);
        }

        if (pageSize > currentIndex) {
            requestJson(currentIndex+1);
        }
    }

    private void parseCommoditInfo(Commodit commodit, JSONObject commoditJSON) {
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
    }

    private void parseContenteditable(Commodit commodit) {
        String addCommoditID_URL = URLSet.replaceAccessTokenReg(DTK_Item_URL, "gid", commodit.id);
        String addToken_URL = URLSet.replaceAccessTokenReg(addCommoditID_URL, "jaw_uid", mToken);
        JSONObject stock = new GetJson().getHttpJson(addToken_URL,1);
        String shortLink = stock.getJSONObject("data").getString("shortLink");

        try {
            URL redirectsURL = Jsoup.connect(shortLink).followRedirects(true).execute().url();

            LOGGER.info("shortLink shortLink = " + shortLink);
            LOGGER.info("shortLink redirectsURL = " + redirectsURL);

            //渲染后的数据
//            Document doc = mGetRenderDoc.getDocument(redirectsURL.toString());

            //静态页面不能获取最终的数据
//            Document doc = Jsoup.connect(redirectsURL.toString ()).followRedirects(true).execute().parse ();

            Document doc = mGetRenderDoc.getDocumentByHtmlUnit (redirectsURL.toString());
            LOGGER.info("shortLink doc = " + doc);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.info("shortLink = " + shortLink);
    }

    int i = 0;
    private void saveDB(Commodit commodit) {
//        commoditMapper.insert (commodit);
//        LOGGER.info((i++) + " commodit = " + commodit);
    }

    public void destory() {
        mGetRenderDoc.destoryWebfolder ();
        mGetRenderDoc = null;
    }
}
