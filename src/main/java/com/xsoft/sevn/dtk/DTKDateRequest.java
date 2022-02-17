package com.xsoft.sevn.dtk;

import com.xsoft.sevn.utils.GetJson;
import com.xsoft.sevn.utils.GetRenderDoc;
import com.xsoft.sevn.utils.URLSet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
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
    private int mParseIndex = 0;

    @Autowired
    private CommoditMapper commoditMapper;

    public void init(String token) {
        mToken = token;
        mGetRenderDoc = new GetRenderDoc();
        mGetRenderDoc.initWebfolder ();
        mParseIndex = 0;
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

    private void process(JSONObject stock, int currentPage) {
        JSONObject searchData = stock.getJSONObject("data").getJSONObject("search");
        int totalSize = searchData.getInt("total");
        int pageSize = totalSize/100;
        if (totalSize%100 != 0) pageSize++;

        JSONArray listArray = searchData.getJSONArray("list");

        LOGGER.info("totalSize = " + totalSize);
        LOGGER.info("pageSize = " + pageSize);
        LOGGER.info("currentPage = " + currentPage);

        for (int i=0; i<listArray.length (); i++) {
            mParseIndex++;
            LOGGER.info("=====current Index==== " + mParseIndex);
            JSONObject commoditJSON = listArray.getJSONObject (i);

            Commodit commodit = new Commodit ();

            //获取商品JSON数据
            parseCommoditInfo(commodit, commoditJSON);

            //根据商品id 获取推荐的短连接
            parseContenteditable(commodit);

            saveDB(commodit);
        }

        if (pageSize > currentPage) {
            requestJson(currentPage+1);
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

        commodit.dtkCommoditURL = addCommoditID_URL;
        commodit.shortLink = shortLink;

        try {
            URL redirectsURL = Jsoup.connect(shortLink).followRedirects(true).execute().url();
            commodit.redirectsURL = redirectsURL.toString();

            LOGGER.info("parseContenteditable shortLink = " + shortLink);
            LOGGER.info("parseContenteditable redirectsURL = " + redirectsURL);

            //渲染后的数据
            Document doc = mGetRenderDoc.getDocument(redirectsURL.toString());
            Elements elements = doc.getElementsByClass("item-info-con");
            if (elements.size() <= 0) {
                LOGGER.info("parseContenteditable have not parse redirectsURL data");
                return;
            }

            Element itemElement = elements.get(0);
            String taobaoCommoditURL = itemElement.getElementsByIndexEquals(0).get(1).attr("href");

            commodit.taobaoCommoditURL = taobaoCommoditURL;
            LOGGER.info("parseContenteditable commodit = " + taobaoCommoditURL);

            Document taobaoCommoditURL_first = Jsoup.connect(taobaoCommoditURL).get ();
            LOGGER.info("parseContenteditable taobaoCommoditURL_first = " + taobaoCommoditURL_first);
            String scriptData = taobaoCommoditURL_first.getElementsByTag ("script").get (0).data ();
            int start = scriptData.indexOf ("https://s.click.taobao.com");
            int end = scriptData.indexOf ("'", start);
            String real_jump_address = scriptData.substring (start, end).trim ().replaceAll ("amp;","");
            LOGGER.info("parseContenteditable real_jump_address = " + real_jump_address);
            try {
                String daa = getRedirectUrl(real_jump_address, taobaoCommoditURL);
                LOGGER.info("parseContenteditable daa = " + daa);
            } catch (Exception e) {

            }
//            String taobaoCommoditID = getTaobaoCommoditID(taobaoCommoditURL);
//
//            //retry once
//            if (taobaoCommoditID.equals ("")) {
//                LOGGER.info("parseContenteditable111");
//                taobaoCommoditID = getTaobaoCommoditID(taobaoCommoditURL);
//            }
//            if (taobaoCommoditID.equals ("")) {
//                LOGGER.info("parseContenteditable222");
//                taobaoCommoditID = getTaobaoCommoditID(taobaoCommoditURL);
//            }
//            if (taobaoCommoditID.equals ("")) {
//                LOGGER.info("parseContenteditable333");
//                taobaoCommoditID = getTaobaoCommoditID(taobaoCommoditURL);
//            }
//            LOGGER.info("parseContenteditable taobaoCommoditID = " + taobaoCommoditID);

        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.info("parseContenteditable commodit = " + commodit);
    }

    private String getRedirectUrl(String path, String refer) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL (path)
                .openConnection();
        conn.setRequestProperty("referer", refer);
        conn.setRequestProperty("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.80 Safari/537.36");
        conn.setInstanceFollowRedirects(false);
        conn.setConnectTimeout(5000);

        System.out.println(conn.getHeaderFields().toString());

        return conn.getHeaderField("Location");
    }

    private String getTaobaoCommoditID(String taobaoCommoditURL) {
        Document taobaoCommoditDoc = mGetRenderDoc.getDocument(taobaoCommoditURL);
//            LOGGER.info("parseContenteditable taobaoCommoditDoc = " + taobaoCommoditDoc);

        Elements linkElements = taobaoCommoditDoc.getElementsByTag ("link");

        String requestInfo = "";
        for (Element linkElement : linkElements) {
            String relString = linkElement.attr ("rel");
            if (relString.equals ("canonical")) {
                requestInfo = linkElement.attr ("href");
                break;
            }
        }

        return requestInfo;
    }

    private void saveDB(Commodit commodit) {
//        commoditMapper.insert (commodit);
    }

    public void destory() {
        mGetRenderDoc.destoryWebfolder ();
        mGetRenderDoc = null;
        mParseIndex = 0;
    }
}
