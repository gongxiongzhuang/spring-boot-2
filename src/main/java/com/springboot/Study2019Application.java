package com.springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

/**
 * spring boot 启动类
 */
@SpringBootApplication
@MapperScan("com.springboot.dao")// mapper 接口类扫描包配置
@EnableCaching //开启缓存
public class Study2019Application extends SpringBootServletInitializer {

    /**
     * 部署war包需要继承SpringApplicationBuilder，重写configure
     * @param builder
     * @return
     */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(Study2019Application.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(Study2019Application.class, args);
	}

}

