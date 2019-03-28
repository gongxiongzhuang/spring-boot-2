package com.springboot.comm.cache;

import com.springboot.comm.utils.ArrayUtil;

import java.util.Arrays;

/**
 * @Description 缓存hashcode工具
 * @Date 2019/3/28 16:35
 * @Created by gongxz
 */
public class CacheHashCode {
    private Object[] params;
    private int code;

    private CacheHashCode(Object[] params) {
        this.params = params;
        this.code = Arrays.deepHashCode(params);
    }

    public static CacheHashCode of(Object object) {
        return new CacheHashCode(ArrayUtil.isArray(object) ? ArrayUtil.toObjectArray(object) : new Object[]{object});
    }

    @Override
    public int hashCode() {
        return code;
    }

    public String hashString() {
        return code + "";
    }

    @Override
    public String toString() {
        return "CacheHashCode{" +
                "params=" + Arrays.toString(params) +
                ", code=" + code +
                '}';
    }
}
