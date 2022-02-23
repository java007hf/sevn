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

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Component
public class DTKDateRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DTKDateRequest.class);
    private String mDTKToken = "";
    private String mTaobao_sgcookie = "";
    private String mTaobao_x5sec = "";

    private GetRenderDoc mGetRenderDoc;

//    大淘客天猫超市
//    private String DTK_List_URL = "https://dtkapi.ffquan.cn/go_getway/proxy/search-v2?platform=1&page=1&px=zh&shop=2&version=2&api_v=1&flow_identifier=normal";
    //大淘客天猫
    private String DTK_List_URL = "https://dtkapi.ffquan.cn/go_getway/proxy/search-v2?platform=1&page=1&px=zh&tmall=1&version=2&api_v=1&flow_identifier=normal";
    //大淘客推荐页面
    private static final String DTK_Item_URL = "https://dtkapi.ffquan.cn/taobaoapi/tpl-tpwd?gid=37337736&location=1&referer=&need_fav=-1&jaw_uid=K8dB7HRsOIHPyaYjkwoLGTSH0_zKZnl6P6rbRgsJZxrjed_-9xhV9Q-37g7d4kbl2M9sRC-GN4tc_MC08_iANw";
    private static final String Tmall_Detail_URL = "https://detail.tmall.com/item.htm?id=573013177383";
    private int mParseIndex = 0;

    @Autowired
    private CommoditMapper commoditMapper;

    public void init(String token, String sg, String x5) {
        mDTKToken = token;
        mTaobao_sgcookie = sg;
        mTaobao_x5sec = x5;

        mGetRenderDoc = new GetRenderDoc();
        mGetRenderDoc.initWebfolder ();
        mParseIndex = 0;
    }

    public void cleanData() {
        commoditMapper.cleanData();
    }

    public void requestJson(int index) {
        String requestURL = URLSet.replaceValueByKey(DTK_List_URL, "page", index + "");

        LOGGER.info("requestURL = " + requestURL);
        JSONObject stock = new GetJson().getJsonObject(requestURL, null, 1);
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

            try {
                //不设置的话会被服务器限频
                Thread.sleep (10000);
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }
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
        LOGGER.info("parseContenteditable=========");
        String addCommoditID_URL = URLSet.replaceValueByKey(DTK_Item_URL, "gid", commodit.id);
        String addToken_URL = URLSet.replaceValueByKey(addCommoditID_URL, "jaw_uid", mDTKToken);
        JSONObject stock = new GetJson().getJsonObject(addToken_URL, null, 1);

        try {
            JSONObject dataJSON = stock.getJSONObject("data");
            String shortLink = stock.getJSONObject("data").getString("shortLink");
            commodit.dtkCommoditURL = addCommoditID_URL;
            commodit.shortLink = shortLink;
            LOGGER.info("parseContenteditable shortLink = " + shortLink);
        } catch (Exception e) {
            e.printStackTrace ();
            LOGGER.info("parseContenteditable not data");
            return;
        }


        try {
            String redirectsURL = getRedirects(commodit.shortLink);
            commodit.redirectsURL = redirectsURL;
            LOGGER.info("parseContenteditable redirectsURL = " + redirectsURL);

            //渲染后的数据
            String taobaoCommoditURL = getTaobaoCommoditTempURL(redirectsURL);
            commodit.taobaoCommoditURL = taobaoCommoditURL;
            LOGGER.info("parseContenteditable commodit = " + taobaoCommoditURL);

            String realCommoditId = getRealCommoditId (taobaoCommoditURL);
//            LOGGER.info("parseContenteditable realCommoditId = " + realCommoditId);
            String tmallDetailUrl = URLSet.replaceValueByKey(Tmall_Detail_URL, "id", realCommoditId);
            commodit.tmallDetailUrl = tmallDetailUrl;
            LOGGER.info("parseContenteditable tmallDetailUrl = " + tmallDetailUrl);

            getItemDetail (realCommoditId, commodit);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LOGGER.info("parseContenteditable commodit = " + commodit);
    }

    private String getTaobaoCommoditTempURL(String redirectsURL) {
        Document doc = mGetRenderDoc.getDocument(redirectsURL);
        Elements elements = doc.getElementsByClass("item-info-con");
        if (elements.size() <= 0) {
            LOGGER.info("parseContenteditable have not parse redirectsURL data");
            return "";
        }

        Element itemElement = elements.get(0);
        String taobaoCommoditURL = itemElement.getElementsByIndexEquals(0).get(1).attr("href");

        return taobaoCommoditURL;
    }

    private String getRedirects(String shortURL) throws Exception  {
        HttpURLConnection conn = (HttpURLConnection) new URL (shortURL)
                .openConnection();
        conn.setRequestProperty("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.80 Safari/537.36");
        conn.setInstanceFollowRedirects(false);
        conn.setConnectTimeout(5000);

        System.out.println(conn.getHeaderFields().toString());
        String location = conn.getHeaderField("Location");

        return location;
    }

    private String getRealCommoditId(String taobaoCommoditURL) throws Exception {
        Document taobaoCommoditURL_first = Jsoup.connect(taobaoCommoditURL).get ();
//        LOGGER.info("parseContenteditable taobaoCommoditURL_first = " + taobaoCommoditURL_first);
        String scriptData = taobaoCommoditURL_first.getElementsByTag ("script").get (0).data ();
        int start = scriptData.indexOf ("https://s.click.taobao.com");
        int end = scriptData.indexOf ("'", start);
        String real_jump_address = scriptData.substring (start, end).trim ().replaceAll ("amp;","");
        LOGGER.info("parseContenteditable real_jump_address = " + real_jump_address);

        HttpURLConnection conn = (HttpURLConnection) new URL (real_jump_address)
                .openConnection();
        conn.setRequestProperty("referer", taobaoCommoditURL);
        conn.setRequestProperty("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.80 Safari/537.36");
        conn.setInstanceFollowRedirects(false);
        conn.setConnectTimeout(5000);

//        System.out.println(conn.getHeaderFields().toString());
        String location = conn.getHeaderField("Location");

        int start1 = location.indexOf ("id=")+3;
        int end1 = location.indexOf ("&", start1);
        String realCommoditId = location.substring (start1, end1).trim ();

        return realCommoditId;
    }

    public void setX5sec(String x5) {
        mTaobao_x5sec = x5;
    }

    private static final String Taobao_Item_Detail_URL = "https://mdskip.taobao.com/core/initItemDetail.htm?isUseInventoryCenter=false&cartEnable=true&service3C=false&isApparel=false&isSecKill=false&tmallBuySupport=true&isAreaSell=true&tryBeforeBuy=false&offlineShop=false&itemId=616197497197&showShopProm=false&isPurchaseMallPage=false&itemGmtModified=1645148276000&isRegionLevel=true&household=false&sellerPreview=false&queryMemberRight=true&addressLevel=3&isForbidBuyItem=false&callback=setMdskip&timestamp=1645153861638&isg=eBOebLLrvzZxCI6ZBO5Zourza77tnIOb4sPzaNbMiInca6TP6FNexNCntjO9JdtxgtC3QetyzV7WZRLHRnsR2xDDB_5LaCPKFxvO.&isg2=BCoqifNAPOreyojuO7PmbTDpe5bMm671K9OALbTi0H0I58qhnCv7BVlWchN7FyaN&ref=https%3A%2F%2Fs.click.taobao.com%2Ft%3Fe%3Dm%253D2%2526s%253DjSIb%252BVy0WzFw4vFB6t2Z2ueEDrYVVa64K7Vc7tFgwiHjf2vlNIV67jGjtYXttE9FUQTSx8a5hQfY9SXzJMHQ52wRVU4dSzYt6ct%252Bc0Y5Z63ESIe%252BlrdZwQ6KhLeHSnTd9CgRSquDwp%252FfthUCVthSDQnzw7yGd%252BdrzRD18rVfQC6EuM7wkVK6rtkYvQZuIwx3oGeIQL4Fi9E0dSsB%252BMyCC3Hks1YB%252F9HKAtuQeASk5p%252F%252FtEaAB6jt1zZh%252F6SdwofmOSqR9HCIYBsK%252Flmy5qWuVCDXTT16t7f8xgxdTc00KD8%253D%26pvid%3D23116944%26af%3D1%26union_lens%3DlensId%253AOPT%25401645149205%25402107b30b_4779_17f0a88b57b_bf61%254001%253Brecoveryid%253A201_33.51.64.126_12209961_1645149205568%253Bprepvid%253A201_33.51.64.126_12209961_1645149205568";
    private static final String Taobao_cookie = "sgcookie=E100bU3EM8DQzRZi2XedyKiZNnMcfeRhffBCCcAiRrBxM%2FtWKn01JbmN%2FoRkpygB0Yse86YAenBUi8Z5ctH%2BTWiLjfGALQ1awKUo8Y%2B%2FDQSgFYPluZyB9T%2FdOY%2BBUPwqnpDV; x5sec=7b22617365727665723b32223a2264316463623537613366396561303330376638316234613730643766613663314350484131354147454b762b693757496b4d58536d774561436a6b774d7a6b784f5455774f7a4977724a624d382f2f2f2f2f2f2f41546f43617a453d227d";
    private void getItemDetail(String itemID, Commodit commodit) {
        String currentItemDetailURL = URLSet.replaceValueByKey (Taobao_Item_Detail_URL, "itemId", itemID);
        String cookie_add_sg = URLSet.replaceValueByKey (Taobao_cookie, "sgcookie", mTaobao_sgcookie);
        String cookie_add_x5 = URLSet.replaceValueByKey (cookie_add_sg, "x5sec", mTaobao_x5sec);

        HashMap<String, String> headerParams = new HashMap<String, String> ();
        headerParams.put ("Host", "mdskip.taobao.com");
        headerParams.put ("Cookie", cookie_add_x5);
        headerParams.put ("referer", "https://detail.tmall.com/");
        JSONObject infoJSON = new GetJson().getJsonObject(currentItemDetailURL, headerParams, 2);
//        LOGGER.info("getItemDetail 11111 = " + infoJSON);

        JSONArray tmallShopPromJSONArray = infoJSON.getJSONObject("defaultModel").getJSONObject("itemPriceResultDO").getJSONArray("tmallShopProm");
        if (tmallShopPromJSONArray.length() != 0) {
            JSONObject tmallShopPromJSON = tmallShopPromJSONArray.getJSONObject(0);
            commodit.campaignName = tmallShopPromJSON.getString("campaignName");
            commodit.tmallPromStartTime = Long.parseLong(tmallShopPromJSON.getString("startTime"));
            commodit.tmallPromEndTime = Long.parseLong(tmallShopPromJSON.getString("endTime"));
            commodit.promPlanMsg = tmallShopPromJSON.getJSONArray("promPlanMsg").toString();
        }
    }

    private void saveDB(Commodit commodit) {
        commoditMapper.insert (commodit);
    }

    public void destory() {
        mGetRenderDoc.destoryWebfolder ();
        mGetRenderDoc = null;
        mParseIndex = 0;
    }
}
