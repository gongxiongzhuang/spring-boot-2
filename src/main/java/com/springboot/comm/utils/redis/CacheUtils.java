package com.springboot.comm.utils.redis;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 通用缓存工具类
 */
@Component
public class CacheUtils {

    private final Logger LOGGER = LoggerFactory.getLogger(CacheUtils.class);

    /*	@Autowired
        private RedisService redisService;
        private RedisTemplate<String, Object> redisTemplate = redisService.getRedisTemplate();
        private StringRedisTemplate stringRedisTemplate = redisService.getStringRedisTemplate();*/
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final String redisKeyPrefix = "test_";

    /**
     * @param key
     * @del(删除缓存,根据key精确匹配删除)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午8:50:22
     */
    public void del(String... key) {
        LOGGER.warn("delete cache, keys in ({})", merge(key));
        for (String k : key) {
            redisTemplate.delete(appendKeyPrefix(k));
        }
    }

    /**
     * @param pattern
     * @batchDel(批量删除;（该操作会执行模糊查询，请尽量不要使用，以免影响性能或误删）)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午9:47:00
     */
    public void batchDel(String... pattern) {
        LOGGER.warn("batchDel cache, pattern in ({})", merge(pattern));
        for (String kp : pattern) {
            redisTemplate.delete(redisTemplate.keys(appendKeyPrefix(kp) + "*"));
        }
    }

    /**
     * @param key
     * @return
     * @getInt(取得缓存（int型）)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午9:47:44
     */
    public Integer getInt(String key) {
        String value = stringRedisTemplate.boundValueOps(appendKeyPrefix(key)).get();
        if (StringUtils.isNotBlank(value)) {
            return Integer.valueOf(value);
        }
        return 0;
    }

    /**
     * @param key
     * @return
     * @getLong(取得缓存（long型）)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午9:48:26
     */
    public Long getLong(String key) {
        String value = stringRedisTemplate.boundValueOps(appendKeyPrefix(key)).get();
        if (StringUtils.isNotBlank(value)) {
            return Long.valueOf(value);
        }
        return 0l;
    }

    /**
     * @param key
     * @return
     * @getStr(取得缓存（字符串类型）)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午9:48:40
     */
    public String getStr(String key) {
        return stringRedisTemplate.boundValueOps(appendKeyPrefix(key)).get();
    }

    /**
     * @param key
     * @param retain 是否保留
     * @return
     * @getStr(取得缓存（字符串类型）)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午9:55:02
     */
    public String getStr(String key, boolean retain) {
        String value = stringRedisTemplate.boundValueOps(appendKeyPrefix(key)).get();
        if (!retain) {
            stringRedisTemplate.delete(appendKeyPrefix(key));
        }
        return value;
    }

    /**
     * @param key
     * @return
     * @getObj(获取缓存:注：基本数据类型(Character除外)，请直接使用get(String key, Class<T> clazz)取值)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午9:55:52
     */
    public Object getObj(String key) {
        return redisTemplate.boundValueOps(appendKeyPrefix(key)).get();
    }

    /**
     * @param key
     * @param retain 是否保留
     * @return
     * @getObj(获取缓存:注：java 8种基本类型的数据请直接使用get(String key, Class < T > clazz)取值)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午9:58:10
     */
    public Object getObj(String key, boolean retain) {
        Object obj = redisTemplate.boundValueOps(appendKeyPrefix(key)).get();
        if (!retain && obj != null) {
            redisTemplate.delete(appendKeyPrefix(key));
        }
        return obj;
    }

    /**
     * @param key
     * @param clazz 类型
     * @return
     * @get(获取缓存：注：慎用java基本数据类型进行转换（可能会出现空值，转换报错）)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午9:59:50
     */
    public <T> T get(String key, Class<T> clazz) {
        key = appendKeyPrefix(key);
        if (clazz.equals(String.class)) {
            return (T) stringRedisTemplate.boundValueOps(key).get();
        } else if (clazz.equals(Integer.class) || clazz.equals(Long.class)) {
            return (T) stringRedisTemplate.boundValueOps(key).get();
        } else if (clazz.equals(Double.class) || clazz.equals(Float.class)) {
            return (T) stringRedisTemplate.boundValueOps(key).get();
        } else if (clazz.equals(Short.class) || clazz.equals(Boolean.class)) {
            return (T) stringRedisTemplate.boundValueOps(key).get();
        }
        return (T) redisTemplate.boundValueOps(key).get();
    }


    /**
     * @param key
     * @param value
     * @param seconds 失效时间(秒)
     * @set(将value对象写入缓存)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:02:01
     */
    public void set(String key, Object value, long seconds) {
        if (null == key || null == value) {
            throw new RuntimeException("key or value must not null");
        }
        key = appendKeyPrefix(key);
        if (value instanceof String) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value instanceof Integer || value instanceof Long) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value instanceof Double || value instanceof Float) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value instanceof Short || value instanceof Boolean) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
        if (seconds > 0) {
            redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        }
    }

    /**
     * @param key   缓存key
     * @param field 缓存对象field
     * @param value 缓存对象field值
     * @setJsonField(更新key对象field的值)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:03:34
     */
    public void setJsonField(String key, String field, String value) {
        JSONObject obj = JSONObject.parseObject(stringRedisTemplate.boundValueOps(appendKeyPrefix(key)).get());
        obj.put(field, value);
        stringRedisTemplate.opsForValue().set(appendKeyPrefix(key), obj.toString());
    }

    /**
     * @param key
     * @param by
     * @return
     * @decr(递减操作)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:04:21
     */
    public double decr(String key, double by) {
        return redisTemplate.opsForValue().increment(appendKeyPrefix(key), -by);
    }

    /**
     * @param key
     * @param by
     * @return
     * @incr(递增操作)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:04:36
     */
    public double incr(String key, double by) {
        return redisTemplate.opsForValue().increment(appendKeyPrefix(key), by);
    }

    /**
     * @param key
     * @param by
     * @return
     * @decr(递减操作)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:05:06
     */
    public long decr(String key, long by) {
        return redisTemplate.opsForValue().increment(appendKeyPrefix(key), -by);
    }

    /**
     * @param key
     * @param by
     * @return
     * @incr(递增操作)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:05:25
     */
    public long incr(String key, long by) {
        return redisTemplate.opsForValue().increment(appendKeyPrefix(key), by);
    }

    /**
     * @param key     缓存key
     * @param by      每次递减数
     * @param seconds 失效时间(秒)
     * @return
     * @decr(递减操作)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:05:06
     */
    public long decr(String key, long by, long seconds) {
        long increment = redisTemplate.opsForValue().increment(appendKeyPrefix(key), -by);
        if (seconds > 0) {
            redisTemplate.expire(appendKeyPrefix(key), seconds, TimeUnit.SECONDS);
        }
        return increment;
    }

    /**
     * @param key     缓存key
     * @param by      每次递增数
     * @param seconds 失效时间(秒)
     * @return
     * @incr(递增操作)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:05:25
     */
    public long incr(String key, long by, long seconds) {
        long increment = redisTemplate.opsForValue().increment(appendKeyPrefix(key), by);
        if (seconds > 0) {
            redisTemplate.expire(appendKeyPrefix(key), seconds, TimeUnit.SECONDS);
        }
        return increment;
    }

    /**
     * @param key
     * @return
     * @getDouble(获取double类型值)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:05:42
     */
    public double getDouble(String key) {
        String value = stringRedisTemplate.boundValueOps(appendKeyPrefix(key)).get();
        if (StringUtils.isNotBlank(value)) {
            return Double.valueOf(value);
        }
        return 0d;
    }

    /**
     * @param key
     * @param value
     * @param seconds 失效时间(秒)
     * @setDouble(设置double类型值)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:06:00
     */
    public void setDouble(String key, double value, long seconds) {
        stringRedisTemplate.opsForValue().set(appendKeyPrefix(key), String.valueOf(value));
        if (seconds > 0) {
            stringRedisTemplate.expire(appendKeyPrefix(key), seconds, TimeUnit.SECONDS);
        }
    }

    /**
     * @param key
     * @param map
     * @setMap(将map写入缓存)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:07:03
     */
    public <T> void setMap(String key, Map<String, T> map) {
        redisTemplate.opsForHash().putAll(appendKeyPrefix(key), map);
    }

    /**
     * @param key
     * @param map
     * @addMap(向key对应的map中添加缓存对象)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:07:22
     */
    public <T> void addMap(String key, Map<String, T> map) {
        redisTemplate.opsForHash().putAll(appendKeyPrefix(key), map);
    }

    /**
     * @param key   cache对象key
     * @param field map对应的key
     * @param value 值
     * @addMap(向key对应的map中添加缓存对象)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:08:10
     */
    public void addMap(String key, String field, String value) {
        redisTemplate.opsForHash().put(appendKeyPrefix(key), field, value);
    }

    /**
     * @param key   cache对象key
     * @param field map对应的key
     * @param obj   对象
     * @addMap(向key对应的map中添加缓存对象)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:08:57
     */
    public <T> void addMap(String key, String field, T obj) {
        redisTemplate.opsForHash().put(appendKeyPrefix(key), field, obj);
    }

    /**
     * @param key
     * @param clazz
     * @return
     * @mget(获取map缓存)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:09:38
     */
    public <T> Map<String, T> mget(String key, Class<T> clazz) {
        BoundHashOperations<String, String, T> boundHashOperations = redisTemplate.boundHashOps(appendKeyPrefix(key));
        return boundHashOperations.entries();
    }

    /**
     * @param key
     * @param field
     * @param clazz
     * @return
     * @getMapField(获取map缓存中的某个对象)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:10:02
     */
    public <T> T getMapField(String key, String field, Class<T> clazz) {
        return (T) redisTemplate.boundHashOps(appendKeyPrefix(key)).get(field);
    }

    /**
     * @param key   map对应的key
     * @param field map中该对象的key
     * @return
     * @delMapField(删除map中的某个对象)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:14:46
     */
    public Long delMapField(String key, Object... field) {
        return redisTemplate.opsForHash().delete(appendKeyPrefix(key), field);
    }

    /**
     * @param key   键
     * @param field map域
     * @param value 增量值
     * @return
     * @hincr(为哈希表key中的域field的值)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:15:33
     */
    public long hincr(String key, String field, long value) {
        return redisTemplate.opsForHash().increment(appendKeyPrefix(key), field, value);
    }

    /**
     * @param key     hash表名
     * @param hashKey hashKey 键
     * @param value   值
     * @hset(向hash表中添加数据)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:17:37
     */
    public void hset(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(appendKeyPrefix(key), hashKey, value);
    }

    /**
     * @param key   hash表名
     * @param field hashKey 键
     * @return
     * @hget(查找hash表中数据)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午10:20:09
     */
    public Object hget(String key, String field) {
        return redisTemplate.boundHashOps(appendKeyPrefix(key)).get(field);
    }

    /**
     * @param key
     * @hdel(删除hash表中)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午9:37:45
     */
    public void hdel(String key, Object... hashKeys) {
        if (hashKeys == null || hashKeys.length == 0) {
            redisTemplate.delete(appendKeyPrefix(key));
        } else {
            redisTemplate.opsForHash().delete(appendKeyPrefix(key), hashKeys);
        }
    }

    /**
     * @param key 为hash表名
     * @return
     * @hlen(获取hash表中所有数据大小)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午9:25:03
     */
    public Long hlen(String key) {
        return redisTemplate.boundHashOps(appendKeyPrefix(key)).size();
    }

    /**
     * @param key 为hash表名
     * @return
     * @hkeys(获取hash表中所有的keys值)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午9:23:49
     */
    public <T> Set<T> hkeys(String key) {
        return (Set<T>) redisTemplate.boundHashOps(appendKeyPrefix(key)).keys();
    }

    /**
     * @param key 为hash表名
     * @return
     * @hvals(获取hash表中所有的数据)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午9:10:32
     */
    public <T> List<T> hvals(String key) {
        return (List<T>) redisTemplate.boundHashOps(appendKeyPrefix(key)).values();
    }

    /**
     * 使用： template.opsForHash().put("redisHash","name","tom");
     * template.opsForHash().put("redisHash","age",26);
     * template.opsForHash().put("redisHash","class","6");
     * System.out.println(template.opsForHash().entries("redisHash"));
     * 结果：{age=26, class=6, name=tom}
     *
     * @param key
     * @return
     */
    public <K, V> Map<K, V> hmap(String key) {
        return (Map<K, V>) redisTemplate.opsForHash().entries(appendKeyPrefix(key));
    }

    /**
     * @param key     缓存KEY
     * @param value   值
     * @param seconds seconds 失效时间(秒)
     * @return
     * @setnx(当key不存在时，为key赋值,同时设置过期时间)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午9:03:45
     */
    public boolean setnx(String key, String value, long seconds) {
        boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(appendKeyPrefix(key), value);
        if (seconds > 0) {
            redisTemplate.expire(appendKeyPrefix(key), seconds, TimeUnit.SECONDS);
        }
        return flag;
    }

    /**
     * @param key     缓存KEY
     * @param seconds 失效时间(秒)
     * @expire(指定缓存的失效时间)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午8:59:12
     */
    public void expire(String key, long seconds) {
        redisTemplate.expire(appendKeyPrefix(key), seconds, TimeUnit.SECONDS);
    }

    /**
     * @param key     缓存KEY
     * @param seconds 失效时间(秒)
     * @expire(指定缓存的失效时间)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午8:58:34
     */
    public void expire(String key, int seconds) {
        redisTemplate.expire(appendKeyPrefix(key), seconds, TimeUnit.SECONDS);
    }

    /**
     * @param key
     * @param value
     * @sadd(添加set)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午8:57:36
     */
    public void sadd(String key, Object... value) {
        redisTemplate.boundSetOps(appendKeyPrefix(key)).add(value);
    }

    /**
     * @param key   set
     * @param value
     * @srem(删除set集合中的对象)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午8:52:50
     */
    public void srem(String key, Object... value) {
        redisTemplate.boundSetOps(appendKeyPrefix(key)).remove(value);
    }


    /**
     * @param key
     * @return
     * @exists(判断key对应的缓存是否存在)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午8:52:15
     */
    public boolean exists(String key) {
        return redisTemplate.hasKey(appendKeyPrefix(key));
    }

    /**
     * @param pattern
     * @return
     * @keys(模糊查询keys)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午8:51:20
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(appendKeyPrefix(pattern));
    }

    /**
     * @param strings
     * @return
     * @merge(合并数组为字符串)
     * @作者:tiger
     * @创建时间:2017年8月30日 下午8:51:03
     */
    private String merge(String... strings) {
        if (strings == null || strings.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        int len = strings.length;
        for (int i = 0; i < len; i++) {
            sb.append(strings[i]);
            if (len != i + 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private String appendKeyPrefix(String key) {
        return redisKeyPrefix.concat(key);
    }

}
