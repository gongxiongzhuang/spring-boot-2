package com.springboot.service.impl;

import com.springboot.dao.UserMapper;
import com.springboot.domain.User;
import com.springboot.domain.UserExample;
import com.springboot.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description TODO
 * @Author gongxz
 * @Date 2019/2/14 15:20
 **/
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private UserMapper userDao;

    @Override
    public User findByName(String name) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andNameEqualTo(name);
        return userDao.selectByExample(userExample).get(0);
    }

    @Override
    public User findById(Long id) {
        return userDao.selectByPrimaryKey(id);
    }

    @Override
    public User findByNameAndUuid(String name, String uuid) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andNameEqualTo(name);
        criteria.andUuidEqualTo(uuid);
        return userDao.selectByExample(userExample).get(0);
    }

    @Override
    public List<User> findAll() {
        return userDao.selectByExample(null);
    }
}
