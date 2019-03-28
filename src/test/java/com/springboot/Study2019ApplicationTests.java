package com.springboot;

import com.springboot.comm.utils.redis.CacheUtils;
import com.springboot.domain.User;
import com.springboot.service.TestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
@MapperScan("com.springboot.dao")// mapper 接口类扫描包配置
@EnableCaching //开启缓存
public class Study2019ApplicationTests {

	@Autowired
	private TestService testService;

	@Test
	public void contextLoads() {
		testService.findByName("龚雄壮");
	}

}

