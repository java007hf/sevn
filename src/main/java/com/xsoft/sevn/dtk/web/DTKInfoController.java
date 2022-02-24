package com.xsoft.sevn.dtk.web;

import com.xsoft.sevn.dtk.DTKUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DTKInfoController {
    @Autowired
    private DTKUserInfo mDTKUserInfo;

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
}
