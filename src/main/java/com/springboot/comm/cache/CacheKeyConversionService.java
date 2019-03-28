package com.springboot.comm.cache;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

/**
 * @Description TODO
 * @Date 2019/3/28 16:32
 * @Created by gongxz
 */
public class CacheKeyConversionService implements ConversionService {
    @Override
    public boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType) {
        return true;
    }

    @Override
    public boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        return true;
    }

    @Nullable
    @Override
    public <T> T convert(@Nullable Object source, Class<T> targetType) {
        return (T) convert(source);
    }

    @Nullable
    @Override
    public Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        return convert(source);
    }

    private Object convert(Object source) {
        if (source instanceof CacheHashCode) {
            return ((CacheHashCode) source).hashString();
        }
        return CacheHashCode.of(source).hashString();
    }
}
