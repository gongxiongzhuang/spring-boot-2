package com.springboot.controller;

import com.springboot.domain.User;
import com.springboot.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description 测试类
 * @Author gongxz
 * @Date 2019/2/14 15:04
 **/
@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping("/user/name/{name}")
    public User findByName(@PathVariable("name") String name, HttpServletRequest request) {
        String uuid = request.getParameter("uuid");
        return testService.findByNameAndUuid(name,uuid);
    }

    @RequestMapping("/user/id/{id}")
    public User findById(@PathVariable("id") Long id) {
        return testService.findById(id);
    }

    @RequestMapping("/requestError")
    public String requestError() {
        return "requestError";
    }
}
