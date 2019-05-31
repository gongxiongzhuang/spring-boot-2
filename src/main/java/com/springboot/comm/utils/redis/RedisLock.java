package com.springboot.comm.utils.redis;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @Description TODO
 * @Date 2019/5/14 16:37
 * @Created by gongxz
 */
@Component
public class RedisLock {

    private final Logger logger = LoggerFactory.getLogger(RedisLock.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String redisKeyPrefix = "test_";

    private static final String LOCK_PREFIX = "lock:";
    //线程本地变量
    private ThreadLocal<String> localKeys = new ThreadLocal<>();//设置锁的key值
    private ThreadLocal<String> localRequestIds = new ThreadLocal<>();//设置锁的唯一value值
    private static final Long LOCK_SUCCESS = 1L;
    private static final String LOCK_LUA;
    private static final String UNLOCK_LUA;

    public static final String RUSH_TO_BUY_LUA = "local stock = tonumber(redis.call('get', KEYS[1])) " +
            "if stock < tonumber(ARGV[1]) then return 0 end " +
            "stock = stock - ARGV[1] " +
            "redis.call('set', KEYS[1], tonumber(stock)) " +
            "return 1";



    static {
        //加锁脚本，其中KEYS[]为外部传入参数
        //KEYS[1]表示key
        //KEYS[2]表示value
        //KEYS[3]表示过期时间
        //setnx (set if not exists) 如果不存在则设置值
        LOCK_LUA = " if redis.call('setnx', KEYS[1], KEYS[2]) == 1 " +
                "then " +
                "    if KEYS[3] == '-1' then return 1 else return redis.call('pexpire', KEYS[1], KEYS[3]) end "+
                "else " +
                "    return 0 " +
                "end ";

        //解锁脚本
        //KEYS[1]表示key
        //KEYS[2]表示value
        //return -1 表示未能获取到key或者key的值与传入的值不相等
        UNLOCK_LUA = " if redis.call('get',KEYS[1]) == KEYS[2] " +
                "then " +
                "    return redis.call('del',KEYS[1]) " +
                "else " +
                "    return -1 " +
                "end ";
    }

    /**
     * 获取锁，如果获取不到则一直等待，最长超时5分钟
     * @param key
     * @return
     */
    public boolean lock(String key) {
        return lock(key, 5*60*100, -1);
    }

    /**
     * 加锁
     * @param key Key
     * @param timeout 过期时间 单位毫秒
     * @param retryTimes 重试次数
     * @return
     */
    public boolean lock(String key, long timeout, int retryTimes) {
        try {
            DefaultRedisScript<Long> LOCK_LUA_SCRIPT = new DefaultRedisScript<>(LOCK_LUA, Long.class);
            final String redisKey = this.getRedisKey(key);
            final String requestId = this.getRequestId();
            logger.debug("lock :::: redisKey = " + redisKey + " requestid = " + requestId);
            //组装lua脚本参数
            List<String> keys = Arrays.asList(redisKey, requestId, String.valueOf(timeout));
            //执行脚本
            Long result = redisTemplate.execute(LOCK_LUA_SCRIPT, keys);
            //加锁成功则存储本地变量
            if (result != null && result.equals(LOCK_SUCCESS)) {
                localRequestIds.set(requestId);
                localKeys.set(redisKey);
                logger.info("success to acquire lock:" + Thread.currentThread().getName() + ", Status code reply:" + result);
                return true;
            } else if (retryTimes == 0) {
                //重试次数为0直接返回失败
                return false;
            } else {
                //重试获取锁
                logger.info("retry to acquire lock:" + Thread.currentThread().getName() + ", Status code reply:" + result);
                int count = 0;
                while (true) {
                    try {
                        //休眠一定时间后再获取锁，这里时间可以通过外部设置
                        Thread.sleep(100);
                        result = redisTemplate.execute(LOCK_LUA_SCRIPT, keys);
                        if (result != null && result.equals(LOCK_SUCCESS)) {
                            localRequestIds.set(requestId);
                            localKeys.set(redisKey);
                            logger.info("success to acquire lock:" + Thread.currentThread().getName() + ", Status code reply:" + result);
                            return true;
                        } else {
                            count++;
                            if (retryTimes == count) {
                                logger.info("fail to acquire lock for " + Thread.currentThread().getName() + ", Status code reply:" + result);
                                return false;
                            } else {
                                logger.warn(count + " times try to acquire lock for " + Thread.currentThread().getName() + ", Status code reply:" + result);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("acquire redis occured an exception:" + Thread.currentThread().getName(), e);
                        break;
                    }
                }
            }
        } catch (Exception e1) {
            logger.error("acquire redis occured an exception:" + Thread.currentThread().getName(), e1);
        }
        return false;
    }

    /**
     * 获取RedisKey
     * @param key 原始KEY，如果为空，自动生成随机KEY
     * @return
     */
    private String getRedisKey(String key) {
        //如果Key为空且线程已经保存，直接用，异常保护
        if (StringUtils.isEmpty(key) && !StringUtils.isEmpty(localKeys.get())) {
            return localKeys.get();
        }
        //如果都是空那就抛出异常
        if (StringUtils.isEmpty(key) && StringUtils.isEmpty(localKeys.get())) {
            throw new RuntimeException("key is null");
        }
        return LOCK_PREFIX + key;
    }

    /**
     * 获取随机请求ID
     * @return
     */
    private String getRequestId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 释放锁
     * @param key
     * @return
     */
    public boolean unlock(String key) {
        try {
            DefaultRedisScript<Long> UNLOCK_LUA_SCRIPT = new DefaultRedisScript<>(UNLOCK_LUA, Long.class);
            String localKey = localKeys.get();
            //如果本地线程没有KEY，说明还没加锁，不能释放
            if(StringUtils.isEmpty(localKey)) {
                logger.error("release lock occured an error: lock key not found");
                return false;
            }
            String redisKey = getRedisKey(key);
            //判断KEY是否正确，不能释放其他线程的KEY
            if(!StringUtils.isEmpty(localKey) && !localKey.equals(redisKey)) {
                logger.error("release lock occured an error: illegal key:" + key);
                return false;
            }
            //组装lua脚本参数
            List<String> keys = Arrays.asList(redisKey, localRequestIds.get());
            logger.debug("unlock :::: redisKey = " + redisKey + " requestid = " + localRequestIds.get());
            // 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
            Long result = redisTemplate.execute(UNLOCK_LUA_SCRIPT, keys);
            //如果这里抛异常，后续锁无法释放
            if (result !=null && result == 1L) {
                logger.info("release lock success:" + Thread.currentThread().getName() + ", Status code reply=" + result);
                return true;
            } else if (result!=null && result == -1L) {
                //返回-1说明获取到的KEY值与requestId不一致或者KEY不存在，可能已经过期或被其他线程加锁
                // 一般发生在key的过期时间短于业务处理时间，属于正常可接受情况
                logger.warn("release lock exception:" + Thread.currentThread().getName() + ", key has expired or released. Status code reply=" + result);
            } else {
                //其他情况，一般是删除KEY失败，返回0
                logger.error("release lock failed:" + Thread.currentThread().getName() + ", del key failed. Status code reply=" + result);
            }
        } catch (Exception e) {
            logger.error("release lock occured an exception", e);
        } finally {
            //清除本地变量
            this.clean();
        }
        return false;
    }

    /**
     * 清除本地线程变量，防止内存泄露
     */
    private void clean() {
        localRequestIds.remove();
        localKeys.remove();
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
            logger.error("设置redis锁发生异常", e);
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
            logger.error("获取锁得标识异常", e);
        }
        return "";
    }

    /**
     * 购买商品，判断商品库存是否充足
     * @param key 商品库存key
     * @param bugNum 购买商品数量
     * @return
     */
    public boolean bugGoods(String key, String bugNum) {
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
            logger.error("释放锁发生异常", e);
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
