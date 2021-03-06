package com.xsoft.sevn.dtk;

import com.xsoft.sevn.utils.GetJson;
import com.xsoft.sevn.utils.GetRenderDoc;
import com.xsoft.sevn.utils.URLSet;
import org.json.JSONArray;
import org.json.JSONException;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Component
public class DTKDateRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DTKDateRequest.class);
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
    private DTKUserInfo mDTKUserInfo;

    public void init(DTKUserInfo userInfo) {
        mDTKUserInfo = userInfo;

        mGetRenderDoc = new GetRenderDoc();
        mGetRenderDoc.initWebfolder ();
        mParseIndex = 0;
    }

    public void cleanData() {
        LOGGER.info("=====cleanData====");
        commoditMapper.cleanData();
    }

    public void requestJson() {
        String requestURL = URLSet.replaceValueByKey(DTK_List_URL, "page", 1 + "");
        JSONObject stock = new GetJson().getJsonObject(requestURL, null, 1);
        int pages = getPageNum(stock);

        for (int i=1; i<=pages; i++) {
            requestJsonByPage(i);
        }
    }

    public void requestJsonByPage(int index) {
        String requestURL = URLSet.replaceValueByKey(DTK_List_URL, "page", index + "");

        LOGGER.info("requestURL = " + requestURL);
        JSONObject stock = new GetJson().getJsonObject(requestURL, null, 1);
        process(stock, index);
    }

    private int getPageNum(JSONObject stock) {
        JSONObject searchData = stock.getJSONObject("data").getJSONObject("search");
        int totalSize = searchData.getInt("total");
        int pageSize = totalSize/100;
        if (totalSize%100 != 0) pageSize++;

        return pageSize;
    }

    private void process(JSONObject stock, int currentPage) {
        JSONObject searchData = stock.getJSONObject("data").getJSONObject("search");
        JSONArray listArray = searchData.getJSONArray("list");

        LOGGER.info("currentPage = " + currentPage);

        for (int i=0; i<listArray.length (); i++) {
            mParseIndex++;
            LOGGER.info("=====current Index==== " + mParseIndex);
            JSONObject commoditJSON = listArray.getJSONObject (i);
            Commodit commodit = new Commodit ();

            //获取商品JSON数据
            parseCommoditInfo(commodit, commoditJSON);
            //根据商品id 获取推荐的短连接
//            parseContenteditable(commodit);

            saveCommodit(commodit);
        }
    }

    public void continueGetData() {
        LOGGER.info("=====continueGetData====");
        List<Commodit> commoditList = commoditMapper.queryAllNoCommoditList();
        LOGGER.info("=====continueGetData====" + commoditList.size());

        for(Commodit commodit : commoditList) {
            //根据商品id 获取推荐的短连接
            parseContenteditable(commodit);
            addPromTime(commodit);
            updateCommodit(commodit);

            try {
                //不设置的话会被服务器限频
                Thread.sleep (10000);
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }
        }
    }

    private void addPromTime(Commodit commodit) {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj);
        commodit.addPromTime = formattedDate;
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
        String addToken_URL = URLSet.replaceValueByKey(addCommoditID_URL, "jaw_uid", mDTKUserInfo.getDTKToken());
        JSONObject stock = new GetJson().getJsonObject(addToken_URL, null, 1);

        try {
            JSONObject dataJSON = stock.getJSONObject("data");
            String shortLink = dataJSON.getString("shortLink");
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
        String taobaoCommoditURL = "";
        try {
            Document doc = mGetRenderDoc.getDocument(redirectsURL);
            Elements elements = doc.getElementsByClass("item-info-con");
            if (elements.size() <= 0) {
                LOGGER.info("parseContenteditable have not parse redirectsURL data");
                return "";
            }

            Element itemElement = elements.get(0);
            taobaoCommoditURL = itemElement.getElementsByIndexEquals(0).get(1).attr("href");
        } catch (Exception e) {
            e.printStackTrace ();
            LOGGER.error ("getTaobaoCommoditTempURL mGetRenderDoc.restart()");
            mGetRenderDoc.restart();
        }

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

    private static final String Taobao_Item_Detail_URL = "https://mdskip.taobao.com/core/initItemDetail.htm?isUseInventoryCenter=false&cartEnable=true&service3C=false&isApparel=false&isSecKill=false&tmallBuySupport=true&isAreaSell=false&tryBeforeBuy=false&offlineShop=false&itemId=665428529632";
    private void getItemDetail(String itemID, Commodit commodit) {
        String currentItemDetailURL = URLSet.replaceValueByKey (Taobao_Item_Detail_URL, "itemId", itemID);
        String cookie = "sgcookie="+mDTKUserInfo.getTaobao_sgcookie()+"; x5sec="+mDTKUserInfo.getTaobao_x5sec ();

        HashMap<String, String> headerParams = new HashMap<String, String> ();
        headerParams.put ("Host", "mdskip.taobao.com");
        headerParams.put ("Cookie", cookie);
        headerParams.put ("referer", "https://detail.tmall.com/");

        try {
            LOGGER.info("getItemDetail currentItemDetailURL = " + currentItemDetailURL);
            LOGGER.info("getItemDetail headerParams = " + headerParams);

            JSONObject infoJSON = new GetJson().getJsonObject(currentItemDetailURL, headerParams, 1);

            JSONArray tmallShopPromJSONArray = infoJSON.getJSONObject("defaultModel").getJSONObject("itemPriceResultDO").getJSONArray("tmallShopProm");
            if (tmallShopPromJSONArray.length() != 0) {
                //todo 这里还存在有多个活动的情况，是一个数组如：https://detail.tmall.com/item.htm?id=662336710669
                JSONObject tmallShopPromJSON = tmallShopPromJSONArray.getJSONObject(0);
                commodit.campaignName = tmallShopPromJSON.getString("campaignName");
                commodit.tmallPromStartTime = Long.parseLong(tmallShopPromJSON.getString("startTime"));
                commodit.tmallPromEndTime = Long.parseLong(tmallShopPromJSON.getString("endTime"));
                commodit.promPlanMsg = tmallShopPromJSON.getJSONArray("promPlanMsg").toString();
            }
        } catch (JSONException e) {
            e.printStackTrace ();
            //增加阻塞机制
            mDTKUserInfo.needNewX5sec ();
        }
    }

    private void saveCommodit(Commodit commodit) {
        try {
            commoditMapper.insert (commodit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCommodit(Commodit commodit) {
        try {
            commoditMapper.updateCommodit(commodit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destory() {
        mGetRenderDoc.destoryWebfolder ();
        mGetRenderDoc = null;
        mParseIndex = 0;
    }
}
