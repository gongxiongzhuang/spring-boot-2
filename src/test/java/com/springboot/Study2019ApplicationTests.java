package com.springboot;

import com.springboot.service.TestService;
import com.springboot.thread.AsyncTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@SpringBootTest
@MapperScan("com.springboot.dao")// mapper 接口类扫描包配置
@EnableCaching //开启缓存
public class Study2019ApplicationTests {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private AsyncTask asyncTask;

	@Autowired
	private TestService testService;

	@Test
	public void contextLoads() {
		testService.findByName("龚雄壮");
	}

	@Test
	public void AsyncTaskTest() {
		for (int i = 0; i < 10000; i++) {
			try {
				//自定义线程池
				/*asyncTask.doTask1(i);*/
				//spring异步线程池
				asyncTask.doTask2(i);

				String text = asyncTask.doTask3(i).get();//阻塞调用
				System.out.println(text);
				String context = asyncTask.doTask3(i).get(1, TimeUnit.SECONDS);//限时调用
				System.out.println(context);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				e.printStackTrace();
			}
		}
		logger.info("All tasks finished.");
	}
}

