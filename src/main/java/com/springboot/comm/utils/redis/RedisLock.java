package com.springboot.comm.utils.redis;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Description TODO
 * @Date 2019/5/14 16:37
 * @Created by gongxz
 */
@Component
public class RedisLock {

    private final Logger LOGGER = LoggerFactory.getLogger(RedisLock.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String redisKeyPrefix = "test_";

    private static final String UNLOCK_LUA;

    public static final String RUSH_TO_BUY_LUA = "local stock = tonumber(redis.call('get', KEYS[1])) " +
            "if stock < tonumber(ARGV[1]) then return 0 end " +
            "stock = stock - ARGV[1] " +
            "redis.call('set', KEYS[1], tonumber(stock)) " +
            "return 1";

    static {
        UNLOCK_LUA = "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
                "then " +
                "    return redis.call(\"del\",KEYS[1]) " +
                "else " +
                "    return 0 " +
                "end ";
    }

    /**
     * 设置锁
     *
     * @param key        需要设置锁的key
     * @param retryTimes 次数
     * @return
     */
    public boolean setLock(String key, int retryTimes) {
        try {
            RedisCallback<String> callback = (connection) -> {
                JedisCommands commands = (JedisCommands) connection.getNativeConnection();
                String uuid = UUID.randomUUID().toString();
                return commands.set(appendKeyPrefix(key), uuid, "NX", "EX", 60);
            };
            String result = redisTemplate.execute(callback);
            if (StringUtils.isEmpty(result) && retryTimes > 0) {
                //retryTimes--;
                return setLock(key, retryTimes);
            } else {
                return !StringUtils.isEmpty(result);
            }
        } catch (Exception e) {
            LOGGER.error("设置redis锁发生异常", e);
        }
        return false;
    }

    /**
     * 获取uuid唯一标识
     *
     * @param key
     * @return
     */
    public String get(String key) {
        try {
            RedisCallback<String> callback = (connection) -> {
                JedisCommands commands = (JedisCommands) connection.getNativeConnection();
                return commands.get(appendKeyPrefix(key));
            };
            return redisTemplate.execute(callback);
        } catch (Exception e) {
            LOGGER.error("获取锁得标识异常", e);
        }
        return "";
    }

    /**
     * @param key
     * @param bugNum
     * @return
     */
    public boolean bugGoods(String key, String bugNum) {
        try {
            List<String> keys = new ArrayList<>();
            keys.add(appendKeyPrefix(key));
            List<String> args = new ArrayList<>();
            args.add(bugNum);

            // 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
            // spring自带的执行脚本方法中，集群模式直接抛出不支持执行脚本的异常，所以只能拿到原redis的connection来执行脚本
            RedisCallback<Long> callback = (connection) -> {
                Object nativeConnection = connection.getNativeConnection();
                // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                // 集群模式
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(RUSH_TO_BUY_LUA, keys, args);
                }

                // 单机模式
                else if (nativeConnection instanceof Jedis) {
                    return (Long) ((Jedis) nativeConnection).eval(RUSH_TO_BUY_LUA, keys, args);
                }
                return 0L;
            };
            Long result = redisTemplate.execute(callback);

            return result != null && result > 0;
        } catch (Exception e) {
            LOGGER.error("释放锁发生异常", e);
        } finally {
            // 清除掉ThreadLocal中的数据，避免内存溢出
            //lockFlag.remove();
        }
        return false;
    }

    /**
     * 释放锁
     *
     * @param key       key
     * @param requestId 标识
     * @return
     */
    public boolean releaseLock(String key, String requestId) {
        // 释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
        try {
            List<String> keys = new ArrayList<>();
            keys.add(appendKeyPrefix(key));
            List<String> args = new ArrayList<>();
            args.add(requestId);

            // 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
            // spring自带的执行脚本方法中，集群模式直接抛出不支持执行脚本的异常，所以只能拿到原redis的connection来执行脚本
            RedisCallback<Long> callback = (connection) -> {
                Object nativeConnection = connection.getNativeConnection();
                // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                // 集群模式
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, keys, args);
                }

                // 单机模式
                else if (nativeConnection instanceof Jedis) {
                    return (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, keys, args);
                }
                return 0L;
            };
            Long result = redisTemplate.execute(callback);

            return result != null && result > 0;
        } catch (Exception e) {
            LOGGER.error("释放锁发生异常", e);
        } finally {
            // 清除掉ThreadLocal中的数据，避免内存溢出
            //lockFlag.remove();
        }
        return false;
    }

    private String appendKeyPrefix(String key) {
        return redisKeyPrefix.concat(key);
    }
}
