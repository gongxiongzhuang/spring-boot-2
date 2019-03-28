package com.springboot.comm.utils.redis;

import com.alibaba.fastjson.JSONObject;
import com.springboot.comm.utils.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 通用缓存工具类
 */
@SuppressWarnings("unchecked")
public class CacheUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheUtils.class);

	private static RedisService redisService = (RedisService) SpringContextUtil.getBean("redisService");
	private static RedisTemplate<String, Object> redisTemplate = redisService.getRedisTemplate();
	private static StringRedisTemplate stringRedisTemplate = redisService.getStringRedisTemplate();
	private static String redisKeyPrefix = "test_";

	/**
	 *
	 * @del(删除缓存,根据key精确匹配删除)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午8:50:22
	 * @param key
	 */
	public static void del(String... key) {
		LOGGER.warn("delete cache, keys in ({})", merge(key));
		for (String k : key) {
			redisTemplate.delete(appendKeyPrefix(k));
		}
	}

	/**
	 *
	 * @batchDel(批量删除;（该操作会执行模糊查询，请尽量不要使用，以免影响性能或误删）)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午9:47:00
	 * @param pattern
	 */
	public static void batchDel(String... pattern) {
		LOGGER.warn("batchDel cache, pattern in ({})", merge(pattern));
		for (String kp : pattern) {
			redisTemplate.delete(redisTemplate.keys(appendKeyPrefix(kp) + "*"));
		}
	}

	/**
	 *
	 * @getInt(取得缓存（int型）)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午9:47:44
	 * @param key
	 * @return
	 */
	public static Integer getInt(String key) {
		String value = stringRedisTemplate.boundValueOps(appendKeyPrefix(key)).get();
		if (StringUtils.isNotBlank(value)) {
			return Integer.valueOf(value);
		}
		return 0;
	}

	/**
	 *
	 * @getLong(取得缓存（long型）)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午9:48:26
	 * @param key
	 * @return
	 */
	public static Long getLong(String key) {
		String value = stringRedisTemplate.boundValueOps(appendKeyPrefix(key)).get();
		if (StringUtils.isNotBlank(value)) {
			return Long.valueOf(value);
		}
		return 0l;
	}

	/**
	 *
	 * @getStr(取得缓存（字符串类型）)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午9:48:40
	 * @param key
	 * @return
	 */
	public static String getStr(String key) {
		return stringRedisTemplate.boundValueOps(appendKeyPrefix(key)).get();
	}

	/**
	 *
	 * @getStr(取得缓存（字符串类型）)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午9:55:02
	 * @param key
	 * @param retain 是否保留
	 * @return
	 */
	public static String getStr(String key, boolean retain) {
		String value = stringRedisTemplate.boundValueOps(appendKeyPrefix(key)).get();
		if (!retain) {
			stringRedisTemplate.delete(appendKeyPrefix(key));
		}
		return value;
	}

	/**
	 *
	 * @getObj(获取缓存:注：基本数据类型(Character除外)，请直接使用get(String key, Class<T> clazz)取值)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午9:55:52
	 * @param key
	 * @return
	 */
	public static Object getObj(String key) {
		return redisTemplate.boundValueOps(appendKeyPrefix(key)).get();
	}

	/**
	 *
	 * @getObj(获取缓存:注：java 8种基本类型的数据请直接使用get(String key, Class<T> clazz)取值)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午9:58:10
	 * @param key
	 * @param retain 是否保留
	 * @return
	 */
	public static Object getObj(String key, boolean retain) {
		Object obj = redisTemplate.boundValueOps(appendKeyPrefix(key)).get();
		if (!retain && obj != null) {
			redisTemplate.delete(appendKeyPrefix(key));
		}
		return obj;
	}

	/**
	 *
	 * @get(获取缓存：注：慎用java基本数据类型进行转换（可能会出现空值，转换报错）)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午9:59:50
	 * @param key
	 * @param clazz 类型
	 * @return
	 */
	public static <T> T get(String key, Class<T> clazz) {
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
	 *
	 * @set(将value对象写入缓存)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:02:01
	 * @param key
	 * @param value
	 * @param seconds  失效时间(秒)
	 */
	public static void set(String key, Object value, long seconds) {
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
	 *
	 * @setJsonField(更新key对象field的值)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:03:34
	 * @param key 缓存key
	 * @param field 缓存对象field
	 * @param value 缓存对象field值
	 */
	public static void setJsonField(String key, String field, String value) {
		JSONObject obj = JSONObject.parseObject(stringRedisTemplate.boundValueOps(appendKeyPrefix(key)).get());
		obj.put(field, value);
		stringRedisTemplate.opsForValue().set(appendKeyPrefix(key), obj.toString());
	}

	/**
	 *
	 * @decr(递减操作)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:04:21
	 * @param key
	 * @param by
	 * @return
	 */
	public static double decr(String key, double by) {
		return redisTemplate.opsForValue().increment(appendKeyPrefix(key), -by);
	}

	/**
	 *
	 * @incr(递增操作)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:04:36
	 * @param key
	 * @param by
	 * @return
	 */
	public static double incr(String key, double by) {
		return redisTemplate.opsForValue().increment(appendKeyPrefix(key), by);
	}

	/**
	 *
	 * @decr(递减操作)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:05:06
	 * @param key
	 * @param by
	 * @return
	 */
	public static long decr(String key, long by) {
		return redisTemplate.opsForValue().increment(appendKeyPrefix(key), -by);
	}

	/**
	 *
	 * @incr(递增操作)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:05:25
	 * @param key
	 * @param by
	 * @return
	 */
	public static long incr(String key, long by) {
		return redisTemplate.opsForValue().increment(appendKeyPrefix(key), by);
	}

	/**
	 *
	 * @decr(递减操作)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:05:06
	 * @param key 缓存key
	 * @param by  每次递减数
	 * @param seconds 失效时间(秒)
	 * @return
	 */
	public static long decr(String key, long by,long seconds) {
		long increment=redisTemplate.opsForValue().increment(appendKeyPrefix(key), -by);
		if (seconds > 0) {
			redisTemplate.expire(appendKeyPrefix(key), seconds, TimeUnit.SECONDS);
		}
		return increment;
	}

	/**
	 *
	 * @incr(递增操作)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:05:25
	 * @param key  缓存key
	 * @param by   每次递增数
	 * @param seconds 失效时间(秒)
	 * @return
	 */
	public static long incr(String key, long by, long seconds) {
		long increment=redisTemplate.opsForValue().increment(appendKeyPrefix(key), by);
		if (seconds > 0) {
			redisTemplate.expire(appendKeyPrefix(key), seconds, TimeUnit.SECONDS);
		}
		return increment;
	}

	/**
	 *
	 * @getDouble(获取double类型值)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:05:42
	 * @param key
	 * @return
	 */
	public static double getDouble(String key) {
		String value = stringRedisTemplate.boundValueOps(appendKeyPrefix(key)).get();
		if (StringUtils.isNotBlank(value)) {
			return Double.valueOf(value);
		}
		return 0d;
	}

	/**
	 *
	 * @setDouble(设置double类型值)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:06:00
	 * @param key
	 * @param value
	 * @param seconds 失效时间(秒)
	 */
	public static void setDouble(String key, double value, long seconds) {
		stringRedisTemplate.opsForValue().set(appendKeyPrefix(key), String.valueOf(value));
		if (seconds > 0) {
			stringRedisTemplate.expire(appendKeyPrefix(key), seconds, TimeUnit.SECONDS);
		}
	}

	/**
	 *
	 * @setMap(将map写入缓存)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:07:03
	 * @param key
	 * @param map
	 */
	public static <T> void setMap(String key, Map<String, T> map) {
		redisTemplate.opsForHash().putAll(appendKeyPrefix(key), map);
	}

	/**
	 *
	 * @addMap(向key对应的map中添加缓存对象)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:07:22
	 * @param key
	 * @param map
	 */
	public static <T> void addMap(String key, Map<String, T> map) {
		redisTemplate.opsForHash().putAll(appendKeyPrefix(key), map);
	}

	/**
	 *
	 * @addMap(向key对应的map中添加缓存对象)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:08:10
	 * @param key cache对象key
	 * @param field map对应的key
	 * @param value 值
	 */
	public static void addMap(String key, String field, String value) {
		redisTemplate.opsForHash().put(appendKeyPrefix(key), field, value);
	}

	/**
	 *
	 * @addMap(向key对应的map中添加缓存对象)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:08:57
	 * @param key cache对象key
	 * @param field map对应的key
	 * @param obj 对象
	 */
	public static <T> void addMap(String key, String field, T obj) {
		redisTemplate.opsForHash().put(appendKeyPrefix(key), field, obj);
	}

	/**
	 *
	 * @mget(获取map缓存)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:09:38
	 * @param key
	 * @param clazz
	 * @return
	 */
	public static <T> Map<String, T> mget(String key, Class<T> clazz) {
		BoundHashOperations<String, String, T> boundHashOperations = redisTemplate.boundHashOps(appendKeyPrefix(key));
		return boundHashOperations.entries();
	}

	/**
	 *
	 * @getMapField(获取map缓存中的某个对象)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:10:02
	 * @param key
	 * @param field
	 * @param clazz
	 * @return
	 */
	public static <T> T getMapField(String key, String field, Class<T> clazz) {
		return (T) redisTemplate.boundHashOps(appendKeyPrefix(key)).get(field);
	}

	/**
	 *
	 * @delMapField(删除map中的某个对象)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:14:46
	 * @param key  map对应的key
	 * @param field map中该对象的key
	 * @return
	 */
	public static Long delMapField(String key, Object... field) {
		return 	redisTemplate.opsForHash().delete(appendKeyPrefix(key), field);
	}

	/**
	 *
	 * @hincr(为哈希表key中的域field的值)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:15:33
	 * @param key 键
	 * @param field map域
	 * @param value 增量值
	 * @return
	 */
	public static long hincr(String key, String field, long value) {
		return redisTemplate.opsForHash().increment(appendKeyPrefix(key), field, value);
	}

	/**
	 *
	 * @hset(向hash表中添加数据)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:17:37
	 * @param key hash表名
	 * @param hashKey  hashKey 键
	 * @param value  值
	 */
	public static void hset(String key, String hashKey, Object value){
		redisTemplate.opsForHash().put(appendKeyPrefix(key), hashKey, value);
	}

	/**
	 *
	 * @hget(查找hash表中数据)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午10:20:09
	 * @param key  hash表名
	 * @param field hashKey 键
	 * @return
	 */
	public static Object hget(String key, String field){
		return redisTemplate.boundHashOps(appendKeyPrefix(key)).get(field);
	}

	/**
	 *
	 * @hdel(删除hash表中)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午9:37:45
	 * @param key
	 */
	public static void hdel(String key, Object...hashKeys){
		if (hashKeys == null || hashKeys.length == 0) {
			redisTemplate.delete(appendKeyPrefix(key));
		}else{
			redisTemplate.opsForHash().delete(appendKeyPrefix(key),hashKeys);
		}
	}

	/**
	 *
	 * @hlen(获取hash表中所有数据大小)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午9:25:03
	 * @param key 为hash表名
	 * @return
	 */
	public static Long hlen(String key){
		return redisTemplate.boundHashOps(appendKeyPrefix(key)).size();
	}

	/**
	 *
	 * @hkeys(获取hash表中所有的keys值)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午9:23:49
	 * @param key 为hash表名
	 * @return
	 */
	public static <T> Set<T> hkeys(String key){
		return (Set<T>)redisTemplate.boundHashOps(appendKeyPrefix(key)).keys();
	}

	/**
	 *
	 * @hvals(获取hash表中所有的数据)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午9:10:32
	 * @param key 为hash表名
	 * @return
	 */
	public static <T> List<T> hvals(String key){
		return (List<T>)redisTemplate.boundHashOps(appendKeyPrefix(key)).values();
	}
	/**
	 * 使用： template.opsForHash().put("redisHash","name","tom");
	 *       template.opsForHash().put("redisHash","age",26);
	 *       template.opsForHash().put("redisHash","class","6");
	 *	     System.out.println(template.opsForHash().entries("redisHash"));
	 *	         结果：{age=26, class=6, name=tom}
	 * @param key
	 * @return
	 */
	public static <K, V>  Map<K, V> hmap(String key){
		return (Map<K, V>)redisTemplate.opsForHash().entries(appendKeyPrefix(key));
	}

	/**
	 *
	 * @setnx(当key不存在时，为key赋值,同时设置过期时间)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午9:03:45
	 * @param key 缓存KEY
	 * @param value 值
	 * @param seconds  seconds 失效时间(秒)
	 * @return
	 */
	public static boolean setnx(String key, String value, long seconds) {
		boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(appendKeyPrefix(key), value);
		if (seconds > 0) {
			redisTemplate.expire(appendKeyPrefix(key), seconds, TimeUnit.SECONDS);
		}
		return flag;
	}

	/**
	 *
	 * @expire(指定缓存的失效时间)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午8:59:12
	 * @param key 缓存KEY
	 * @param seconds 失效时间(秒)
	 */
	public static void expire(String key, long seconds) {
		redisTemplate.expire(appendKeyPrefix(key), seconds, TimeUnit.SECONDS);
	}

	/**
	 *
	 * @expire(指定缓存的失效时间)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午8:58:34
	 * @param key 缓存KEY
	 * @param seconds 失效时间(秒)
	 */
	public static void expire(String key, int seconds) {
		redisTemplate.expire(appendKeyPrefix(key), seconds, TimeUnit.SECONDS);
	}

	/**
	 *
	 * @sadd(添加set)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午8:57:36
	 * @param key
	 * @param value
	 */
	public static void sadd(String key, Object... value) {
		redisTemplate.boundSetOps(appendKeyPrefix(key)).add(value);
	}

	/**
	 *
	 * @srem(删除set集合中的对象)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午8:52:50
	 * @param key set
	 * @param value
	 */
	public static void srem(String key, Object... value) {
		redisTemplate.boundSetOps(appendKeyPrefix(key)).remove(value);
	}


	/**
	 *
	 * @exists(判断key对应的缓存是否存在)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午8:52:15
	 * @param key
	 * @return
	 */
	public static boolean exists(String key) {
		return redisTemplate.hasKey(appendKeyPrefix(key));
	}

	/**
	 *
	 * @keys(模糊查询keys)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午8:51:20
	 * @param pattern
	 * @return
	 */
	public static Set<String> keys(String pattern){
		return redisTemplate.keys(appendKeyPrefix(pattern));
	}

	/**
	 *
	 * @merge(合并数组为字符串)
	 * @作者:tiger
	 * @创建时间:2017年8月30日 下午8:51:03
	 * @param strings
	 * @return
	 */
	private static String merge(String...strings){
		if (strings == null || strings.length == 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		int len = strings.length;
		for (int i = 0; i < len; i++) {
			sb.append(strings[i]);
			if(len != i+1){
				sb.append(",");
			}
		}
		return sb.toString();
	}

	private static String appendKeyPrefix(String key){
		return redisKeyPrefix.concat(key);
	}

}
