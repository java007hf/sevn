package com.xsoft.sevn.dtk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DTKTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(DTKTask.class);
    private static final String Token = "K8dB7HRsOIHPyaYjkwoLGTSH0_zKZnl6P6rbRgsJZxq13Iy66y1V5bjLitRpBkLsNs-X9PJy3z-GNIyvofpw1A";

    @Autowired
    private DTKUserInfo mDTKUserInfo;

    @Autowired
    private DTKDateRequest mDTKDateRequest;

    public void crawl() {
        LOGGER.info ("----crawl begin----");
        mDTKUserInfo.setDTKToken (Token);
        mDTKUserInfo.setTaobao_sgcookie ("E100dsMGbTjI4f07WKK%2BJIF40H7ZIZlMZwssS5dIa2p7LNIkqTl%2FC23yOHMgb00ZwAACR7TQYRMzqfTRa%2F6v%2F8mN0dqHyWa48k3f7NIs8IFUwo6U40kHDSryrnVzMQeOtKg%2B");
        mDTKUserInfo.setTaobao_x5sec ("7b22617365727665723b32223a226332633834633434623434323439366139386262363265326333623630613239434a793638354147454f6e4a686f666473374f77625443736c737a7a2f2f2f2f2f2f38424f674a724d513d3d227d");

        mDTKDateRequest.init (mDTKUserInfo);
//            mDTKDateRequest.cleanData();
//            mDTKDateRequest.requestJson();
//            mDTKDateRequest.destory ();
    }
}
