package com.springboot.dao;

import com.springboot.dao.base.BaseDao;
import com.springboot.domain.User;
import com.springboot.domain.UserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends BaseDao<User, UserExample> {
}