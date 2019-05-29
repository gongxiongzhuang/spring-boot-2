package com.springboot.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.springboot.comm.page.PageList;
import com.springboot.comm.utils.redis.CacheUtils;
import com.springboot.dao.UserMapper;
import com.springboot.domain.User;
import com.springboot.domain.UserExample;
import com.springboot.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description TODO
 * @Author gongxz
 * @Date 2019/2/14 15:20
 **/

@Service
@Transactional
public class TestServiceImpl implements TestService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserMapper userDao;
/*
    @Autowired
    private RedisService redisService;*/

    @Autowired
    private CacheUtils cacheUtils;

    @Override
    @Cacheable(cacheNames = "test")
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
    @Cacheable(cacheNames = "testAll")
    public PageList<User> findAll() {
        PageHelper.startPage(1, 1);
        List<User> list = userDao.selectByExample(null);
/*        //自定义分页插件
        Map<String, String> map = new HashMap<>();
        map.put("a", "a");
        map.put("b", "a");
        cacheUtils.set("abc",map,1000);
        Map mapC = cacheUtils.get("abc",Map.class);
        logger.info(mapC.get("a").toString());*/
        return new PageList<>(list);
    }

    @Override
    public PageList<User> updateUsers() {
        User user = new User();
        user.setId(13L);
        user.setAge(17);
        userDao.updateByPrimaryKeySelective(user);
        return putFindAll();
    }

    private Lock lock = new ReentrantLock();

    private int count = 0;

    @Override
    public void incr() {
        try {
            if (lock.tryLock(20, TimeUnit.SECONDS)) {
                try {
                    int stockTest = cacheUtils.getInt("stockTest");
                    if (stockTest > 0) {
                        cacheUtils.decr("stockTest", 1);
                    } else {
                        count++;
                        System.out.println("商品已经抢空啦！请明天再来"+count);
                    }
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("抢夺资源错误!",e);
        }
    }

    @Override
    @CachePut(cacheNames = "testAll")
    public PageList<User> putFindAll() {
        PageHelper.startPage(2, 2);
        List<User> list = userDao.selectByExample(null);
/*        //自定义分页插件
        Map<String, String> map = new HashMap<>();
        map.put("a", "a");
        map.put("b", "a");
        cacheUtils.set("abc",map,1000);
        Map mapC = cacheUtils.get("abc",Map.class);
        logger.info(mapC.get("a").toString());*/
        return new PageList<>(list);
    }

    @Override
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
