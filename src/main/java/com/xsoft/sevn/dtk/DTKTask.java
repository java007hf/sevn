package com.xsoft.sevn.dtk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        mDTKUserInfo.setTaobao_sgcookie ("E1006tDFGxtUWaW5lGeul1uOD%2FW4CYAM60%2BCG9q3lm1UXQL7OCx0FsH5w5p12SYe3akXs%2Fp2MK7P5mP2AspiCvLYFv3jNiyrp0PzH%2Fv12IGnNtfm%2BBcsK%2FNS1EeQtzwyLoXd");
        mDTKUserInfo.setTaobao_x5sec ("7b22617365727665723b32223a223930363930616639393732336532366631383761366534633932643365313565434e505231354147454a4f386749336b396247526353694367414977724a624d382f2f2f2f2f2f2f41546f43617a453d227d");

        mDTKDateRequest.init (mDTKUserInfo);
//            mDTKDateRequest.cleanData();
//            mDTKDateRequest.requestJson();
//            mDTKDateRequest.destory ();
    }
}
