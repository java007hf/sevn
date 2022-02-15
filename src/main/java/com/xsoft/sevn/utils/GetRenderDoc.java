package com.xsoft.sevn.utils;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class GetRenderDoc {
    public Document getDocument(String url){
        ArrayList<String> command = new ArrayList<String>();
        //不显示google 浏览器
        command.add("--headless");
        Launcher launcher = new Launcher();
        Document doc = null;
        try {
            SessionFactory factory = launcher.launch(command);
            Session session = factory.create();
            session.navigate(url);
            session.waitDocumentReady();
            String content = (String) session.getContent();
            doc = Jsoup.parse(content);
        }catch (Exception e){
            e.printStackTrace();
        }

        return doc;
    }
}
