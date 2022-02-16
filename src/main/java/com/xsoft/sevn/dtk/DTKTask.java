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
    private ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    private static final String Token = "K8dB7HRsOIHPyaYjkwoLGTSH0_zKZnl6P6rbRgsJZxq13Iy66y1V5bjLitRpBkLsNs-X9PJy3z-GNIyvofpw1A";

    @Autowired
    private DTKDateRequest dtkDateRequest;

    public void crawl() {
        // 定时任务，每10分钟爬取一次
        timer.scheduleWithFixedDelay(() -> {
            Thread.currentThread().setName("DTKCrawlerThread");

            LOGGER.info ("----crawl begin----");
            dtkDateRequest.init (Token);
            dtkDateRequest.cleanData();
            dtkDateRequest.requestJson(1);
            dtkDateRequest.destory ();
        }, 0, 10, TimeUnit.MINUTES);
    }
}
