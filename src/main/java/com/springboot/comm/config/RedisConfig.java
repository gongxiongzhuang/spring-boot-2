package com.springboot.comm.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.comm.cache.CacheHashCode;
import com.springboot.comm.cache.CacheKeyConversionService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @Description redis 缓存配置
 * @Date 2019/3/13 14:55
 * @Created by gongxz
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    @Bean(name = "cacheKeyGenerator")
    @Primary//@Primary：自动装配时当出现多个Bean候选者时，被注解为@Primary的Bean将作为首选者，否则将抛出异常
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> CacheHashCode.of(params);
    }

    /**
     * spring boot 缓存默认配置
     * @param factory
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60))//定义默认的cache time-to-live.(缓存存储有效时间)
                .disableCachingNullValues()//静止缓存为空
                //此处定义了cache key的前缀, 避免公司不同项目之间的key名称冲突.
                .computePrefixWith(cacheName -> "api".concat(":").concat(cacheName).concat(":"))
                //定义key和value的序列化协议, 同时的hash key和hash value也被定义.
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(createJackson2JsonRedisSerializer()))
                //自定义key的生成策略, 将方法参数转换为hashcode, 作为redis key. 需要做两个事情, 一个是添加一个自定义的ConversionService, 另一个是需要自定义一个KeyGenerator.
                .withConversionService(new CacheKeyConversionService());

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .transactionAware()
                .build();
    }

    /**
     * 创建redisTemplate工具
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        template.setKeySerializer(new StringRedisSerializer());//设置key序列化类，否则key前面会多了一些乱码
        template.setValueSerializer(createJackson2JsonRedisSerializer());//设置value序列化
        template.setHashKeySerializer(createJackson2JsonRedisSerializer());//设置 hash key 序列化
        template.setHashValueSerializer(createJackson2JsonRedisSerializer());//设置 hash value 序列化
        template.setEnableTransactionSupport(true);//设置redis支持数据库的事务
        template.afterPropertiesSet();//初始化设置并且生效
        return template;
    }

    /**
     * 创建redis序列化
     * @return
     */
    private RedisSerializer<Object> createJackson2JsonRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        return jackson2JsonRedisSerializer;
    }

    /*@Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    @Bean
    public ValueOperations<String, String> valueOperations(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    @Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    @Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }*/

}
