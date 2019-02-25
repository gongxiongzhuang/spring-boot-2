package com.springboot.dao;

import com.springboot.dao.base.BaseDao;
import com.springboot.domain.Permission;
import com.springboot.domain.PermissionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PermissionMapper extends BaseDao<Permission, PermissionExample> {
}