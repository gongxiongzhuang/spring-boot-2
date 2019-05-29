package com.springboot.comm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @Description redis session
 * @Date 2019/3/28 20:35
 * @Created by gongxz
 */
@Configuration
////maxInactiveIntervalInSeconds session超时时间,单位秒
@EnableRedisHttpSession
public class RedisSessionConfig {
}
