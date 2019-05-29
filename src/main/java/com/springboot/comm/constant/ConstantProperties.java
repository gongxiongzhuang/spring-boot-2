package com.springboot.comm.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Date 2019/4/18 11:59
 * @Created by gongxz
 */
@Component
public class ConstantProperties {

    @Value("${logging.config}")
    private String appKey = "";

    @Value("${my.sign}")
    private String sign;

    @Value("${my.test}")
    private String test;

    public String getAppKey() {
        return appKey;
    }

    public String getSign() {
        return sign;
    }

    public String getTest() {
        return test;
    }
}
