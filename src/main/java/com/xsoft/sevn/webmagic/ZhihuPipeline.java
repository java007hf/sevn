package com.xsoft.sevn.webmagic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Date;
import java.util.UUID;

@Component
public class ZhihuPipeline implements Pipeline {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZhihuPipeline.class);

    @Autowired
    private CmsContentMapper cmsContentMapper;

    public void process(ResultItems resultItems, Task task) {
        String title = resultItems.get("title");
        String answer = resultItems.get("answer");

//        CmsContentPO contentPO = new CmsContentPO();
//        contentPO.setContentId(UUID.randomUUID().toString());
//        contentPO.setTitle(title);
//        contentPO.setReleaseDate(new Date());
//        contentPO.setContent(answer);


        try {
            LOGGER.info("保存知乎文章成功：tile = " + title + " , answer = " + answer);
//            cmsContentMapper.insert (UUID.randomUUID().toString(),
//                    title, answer, new Date ());
        } catch (Exception ex) {
            LOGGER.error("保存知乎文章失败", ex);
        }
    }
}
