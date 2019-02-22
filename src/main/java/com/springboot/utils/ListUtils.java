package com.springboot.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description list工具类
 * @Date 2019/2/22 13:06
 * @Created by gongxz
 */
public class ListUtils {

    /**
     * 多个对象转化成List
     * @param params
     * @param <T>
     * @return
     */
    public static <T> List<T> ObjectConvertToList(T... params) {
        return new ArrayList<>(Arrays.asList(params));
    }
}
