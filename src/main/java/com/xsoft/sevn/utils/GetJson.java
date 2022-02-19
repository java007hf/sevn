package com.xsoft.sevn.utils;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GetJson {
    public String getHttpString(String url, HashMap<String, String> headerParams){
        try {
            URL realUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.80 Safari/537.36");
            if (headerParams != null) {
                for(Map.Entry<String, String> entry : headerParams.entrySet ()) {
                    connection.setRequestProperty(entry.getKey (), entry.getValue ());
                }
            }
            // 建立实际的连接
            connection.connect();
            //请求成功
            if(connection.getResponseCode()==200){
                InputStream is=connection.getInputStream();
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                //10MB的缓存
                byte [] buffer=new byte[10485760];
                int len=0;
                while((len=is.read(buffer))!=-1){
                    baos.write(buffer, 0, len);
                }
                String jsonString=baos.toString("UTF-8");

                baos.close();
                is.close();

                return jsonString;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public JSONObject getHttpJson(String url, int comefrom, HashMap<String, String> headerParams){
        String str = getHttpString(url, headerParams);

        if (str == null) {
            return null;
        }
        //转换成json数据处理
        JSONObject jsonArray=getJsonString(str,comefrom);
        return jsonArray;
    }

    public JSONObject getJsonString(String str, int comefrom){
        JSONObject jo = null;
        if(comefrom==1){
            return new JSONObject(str);
        }else if(comefrom==2){
            int indexStart = 0;
            //字符处理
            for(int i=0;i<str.length();i++){
                if(str.charAt(i)=='('){
                    indexStart = i;
                    break;
                }
            }
            String strNew = "";
            //分割字符串
            for(int i=indexStart+1;i<str.length()-1;i++){
                strNew += str.charAt(i);
            }
            return new JSONObject(strNew);
        }
        return jo;
    }
}
