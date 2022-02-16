package com.xsoft.sevn.utils;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class GetRenderDoc {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetRenderDoc.class);
    private Session mSession;
    private SessionFactory mFactory;
    private String mContext;
    private Launcher mLauncher;

    public void initWebfolder() {
        LOGGER.info ("===initWebfolder===");
//        ArrayList<String> command = new ArrayList<String>();
//        //不显示google 浏览器
//        command.add("--headless");
//        command.add("--disable-gpu");
//        mLauncher = new Launcher();
//
//        mFactory = mLauncher.launch(command);
//        mContext = mFactory.createBrowserContext();
//        mSession = mFactory.create(mContext);
    }

    public Document getDocument(String url){
        LOGGER.info ("===getDocument===");
        Document doc = null;
        mSession.navigate(url);
        // 默认timeout是10*1000 ms，也可以像下面这样手动设置
        mSession.waitDocumentReady(15 * 1000);
        LOGGER.info ("===getDocument===1111");
        // 通过session得到渲染后的html内容
        String html = mSession.getContent();
        LOGGER.info ("===getDocument===2222");
        doc = Jsoup.parse (html);

        return doc;
    }

    public void destoryWebfolder() {
        LOGGER.info ("===destoryWebfolder===");
//        mFactory.disposeBrowserContext(mContext);
//        // 真正的关闭后台进程
//        mLauncher.getProcessManager().kill();
    }


    public Document getDocumentByHtmlUnit(String url) {
        LOGGER.info ("===getDocumentByHtmlUnit===");
        System.setProperty("phantomjs.binary.path", "/home/xxx/software/phantomjs/bin/phantomjs");//这里写你安装的phantomJs文件路径
        WebDriver webDriver = new PhantomJSDriver ();
        webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        webDriver.get("http://www.jianshu.com");
        WebElement webElement = webDriver.findElement(By.className("recommended-authors"));
        System.out.println(webElement.getAttribute("outerHTML"));

//        Document document = Jsoup.parse(pageXml);//获取html文档
        return null;
    }
}
