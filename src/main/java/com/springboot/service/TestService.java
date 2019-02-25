package com.springboot.service;

import com.springboot.domain.User;
import com.springboot.vo.base.PageList;

/**
 * @Description TODO
 * @Author gongxz
 * @Date 2019/2/14 15:20
 **/

public interface TestService {

    User findByName(String name);

    User findById(Long id);

    User findByNameAndUuid(String name, String uuid);

    PageList<User> findAll();
}
