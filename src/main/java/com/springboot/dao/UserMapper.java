package com.springboot.dao;

import com.springboot.comm.base.BaseDao;
import com.springboot.domain.User;
import com.springboot.domain.UserExample;
import org.springframework.cache.annotation.CacheConfig;

public interface UserMapper extends BaseDao<User, UserExample> {
}