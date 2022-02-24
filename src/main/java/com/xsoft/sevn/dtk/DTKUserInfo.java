package com.xsoft.sevn.dtk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DTKUserInfo {
    private String mDTKToken = "";
    private String mTaobao_sgcookie = "";
    private String mTaobao_x5sec = "";
    private Object Lock = new Object ();
    private static final Logger LOGGER = LoggerFactory.getLogger(DTKUserInfo.class);

    public void setDTKToken (String DTKToken) {
        this.mDTKToken = DTKToken;
        LOGGER.info ("setDTKToken = " + DTKToken);
    }

    public String getDTKToken () {
        return mDTKToken;
    }

    public void setTaobao_sgcookie (String taobao_sgcookie) {
        this.mTaobao_sgcookie = taobao_sgcookie;
        LOGGER.info ("setTaobao_sgcookie = " + taobao_sgcookie);
    }

    public String getTaobao_sgcookie () {
        return mTaobao_sgcookie;
    }

    public void setTaobao_x5sec (String taobao_x5sec) {
        this.mTaobao_x5sec = taobao_x5sec;
        LOGGER.info ("setTaobao_x5sec = " + taobao_x5sec);
    }

    public String getTaobao_x5sec () {
        return mTaobao_x5sec;
    }

    public void needNewX5sec () {
        synchronized(Lock) {
            try {
                LOGGER.info ("needNewX5sec wait....");
                Lock.wait ();
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }
        }
    }

    public void setNew5sec (String taobao_x5sec) {
        synchronized(Lock) {
            LOGGER.info ("setNew5sec notify....");
            setTaobao_x5sec (taobao_x5sec);
            Lock.notify ();
        }
    }
}
