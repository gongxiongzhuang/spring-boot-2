package com.springboot.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.springboot.dao.UserMapper;
import com.springboot.domain.User;
import com.springboot.domain.UserExample;
import com.springboot.service.TestService;
import com.springboot.comm.page.PageList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description TODO
 * @Author gongxz
 * @Date 2019/2/14 15:20
 **/

@Service
@CacheConfig(cacheNames = "test")
public class TestServiceImpl implements TestService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserMapper userDao;

    @Override
    @Cacheable
    public User findByName(String name) {
        logger.info("查询数据");
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andNameEqualTo(name);
        //return null;
        return userDao.selectByExample(userExample).get(0);
    }

    @Override
    public User findById(Long id) {
        return null;
        //return userDao.selectByPrimaryKey(id);
    }

    @Override
    public User findByNameAndUuid(String name, String uuid) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andNameEqualTo(name);
        criteria.andUuidEqualTo(uuid);
        //return null;
        return userDao.selectByExample(userExample).get(0);
    }

    @Override
    @Cacheable
    public PageList<User> findAll() {
        PageHelper.startPage(2, 8);
        List<User> list = userDao.selectByExample(null);
        //自定义分页插件
        return new PageList<>(list);
    }

    @Override
    @Cacheable(key = "1")
    public List<User> findList() {
        return  userDao.selectByExample(null);
    }

    public PageInfo<User> find() {
        PageHelper.startPage(2, 8);
        List<User> list = userDao.selectByExample(null);
        //自定义分页插件
        return new PageInfo<>(list);
    }
}
