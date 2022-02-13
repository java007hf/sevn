package com.xsoft.sevn.webmagic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

@Component
public class ZhihuPipeline implements Pipeline {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZhihuPipeline.class);

    public void process(ResultItems resultItems, Task task) {
        String title = resultItems.get("title");
        String answer = resultItems.get("answer");

        try {
            LOGGER.info("保存知乎文章成功：tile = " + title + " , answer = " + answer);
        } catch (Exception ex) {
            LOGGER.error("保存知乎文章失败", ex);
        }
    }
}
