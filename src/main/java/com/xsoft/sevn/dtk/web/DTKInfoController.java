package com.xsoft.sevn.dtk.web;

import com.xsoft.sevn.dtk.DTKDateRequest;
import com.xsoft.sevn.dtk.DTKUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DTKInfoController {
    @Autowired
    private DTKUserInfo mDTKUserInfo;

    @Autowired
    private DTKDateRequest mDTKDateRequest;

    @RequestMapping(value = "/token/{token}")
    public String setDTKToken(@PathVariable String token) {
        mDTKUserInfo.setDTKToken (token);
        return "success";
    }

    @RequestMapping(value = "/sg/{sgcookie}")
    public String setTaobaoSg(@PathVariable String sgcookie) {
        mDTKUserInfo.setTaobao_sgcookie (sgcookie);
        return "success";
    }

    @RequestMapping(value = "/x5/{x5sec}")
    public String setTaobaoX5(@PathVariable String x5sec) {
        mDTKUserInfo.setNew5sec (x5sec);
        return "success";
    }

    @RequestMapping(value = "/resetData")
    public String resetData() {
        new Thread() {
            @Override
            public void run() {
                Thread.currentThread().setName("resetData");
                mDTKDateRequest.cleanData();
                mDTKDateRequest.requestJson();
                super.run();
            }
        }.start();

        return "success";
    }

    @RequestMapping(value = "/getdata")
    public String getdata() {
        new Thread() {
            @Override
            public void run() {
                Thread.currentThread().setName("getdata");
                mDTKDateRequest.continueGetData();
            }
        }.start();

        return "success";
    }
}
