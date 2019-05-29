package com.springboot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Date 2019/4/18 15:09
 * @Created by gongxz
 */
@Component
public class Test {
    @Value("${my.sign}")
    private String test;

    public String getTest() {
        return test;
    }
}
