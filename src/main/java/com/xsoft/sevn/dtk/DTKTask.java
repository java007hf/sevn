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
        mDTKUserInfo.setTaobao_sgcookie ("E100hZaYC67cB0Ga4RxSyKMGsBmdkMb7uhx6vYnO2u0waNMAQeYWudRUdtCnMtm7XnFluQLlpxIZEa6WmtuSKqBFMI4jC^%^2F9MQNVjMffiJZdM8EyYnq5taRoHgdOhLfCNT0JF");
        mDTKUserInfo.setTaobao_x5sec ("7b22617365727665723b32223a2231626337636634396263656232653163326663356130343564323232633166344349715738704147454f65746864612b2f2b2b4a77514577724a624d382f2f2f2f2f2f2f41546f43617a453d227d");

        mDTKDateRequest.init (mDTKUserInfo);
//            mDTKDateRequest.cleanData();
//            mDTKDateRequest.requestJson();
//            mDTKDateRequest.destory ();
    }
}
