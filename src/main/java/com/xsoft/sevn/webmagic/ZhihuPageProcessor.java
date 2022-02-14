package com.xsoft.sevn.webmagic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

@Component
public class ZhihuPageProcessor implements PageProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZhihuPageProcessor.class);
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    public void process(Page page) {
//        LOGGER.info ("----crawl begin---- page = " + page.getHtml());

        page.addTargetRequests(page.getHtml().links().regex("https://www\\.zhihu\\.com/question/\\d+").all());
        page.putField("title", page.getHtml().xpath("//h1[@class='QuestionHeader-title']/text()").toString());
        page.putField("answer", page.getHtml().xpath("//div[@class='css-eew49z']/tidyText()").toString());
        if (page.getResultItems().get("title") == null) {
            // 如果是列表页，跳过此页，pipeline不进行后续处理
            page.setSkip(true);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
