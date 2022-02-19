package com.xsoft.sevn.utils;


import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetJson {
    private String getHttpString(String url, HashMap<String, String> headerParams){
        String result = "";
        try {
            URL realUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
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

                String charset = "UTF-8";
                Pattern pattern = Pattern.compile("charset=\\S*");
                Matcher matcher = pattern.matcher(connection.getContentType());
                if (matcher.find()) {
                    charset = matcher.group().replace("charset=", "");
                }

//                System.out.println("charset = " + charset);

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();

                return result;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public JSONObject getJsonObject(String url, HashMap<String, String> headerParams, int comefrom){
        JSONObject jo = null;
        String str = getHttpString(url, headerParams);

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
