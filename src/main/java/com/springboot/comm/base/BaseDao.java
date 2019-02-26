package com.springboot.comm.base;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description TODO
 * @Date 2019/2/25 17:31
 * @Created by gongxz
 */
public interface BaseDao<T,TExample> {
    long countByExample(TExample example);

    int deleteByExample(TExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(T record);

    int insertSelective(T record);

    List<T> selectByExample(TExample example);

    T selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") T record, @Param("example") TExample tExample);

    int updateByExample(@Param("record") T record, @Param("example") TExample tExample);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);
}
