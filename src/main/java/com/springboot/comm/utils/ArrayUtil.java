package com.springboot.comm.utils;

import java.lang.reflect.Array;

/**
 * @Description TODO
 * @Date 2019/3/28 16:41
 * @Created by gongxz
 */
public class ArrayUtil {

    /**
     * 对象是否为数组对象
     *
     * @param obj 对象
     * @return 是否为数组对象，如果为{@code null} 返回false
     */
    public static boolean isArray(Object obj) {
        if (null == obj) {
//            throw new NullPointerException("Object check for isArray is null");
            return false;
        }
//        反射 获得类型
        return obj.getClass().isArray();
    }

    public static Object[] toObjectArray(Object object) {
        if (object.getClass().isArray()) {
            int length = Array.getLength(object);
            Object[] os = new Object[length];
            for (int i = 0; i < os.length; i++) {
                os[i] = Array.get(object, i);
            }
            return os;
        }
        return null;
    }
}
