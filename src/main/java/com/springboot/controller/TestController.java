package com.springboot.controller;

import com.springboot.comm.base.BaseController;
import com.springboot.comm.constant.ConstantProperties;
import com.springboot.comm.page.PageList;
import com.springboot.comm.result.ResponseResult;
import com.springboot.comm.utils.redis.CacheUtils;
import com.springboot.comm.utils.redis.RedisLock;
import com.springboot.domain.User;
import com.springboot.domain.UserExample;
import com.springboot.service.TestService;
import com.springboot.thread.ThreadPoolManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


/**
 * @Description 测试类
 * @Author gongxz
 * @Date 2019/2/14 15:04
 **/

@RestController
@Api(tags = "用户信息")
@RequestMapping(value = "/test")
public class TestController extends BaseController {

    @Autowired
    private TestService testService;

    @Autowired
    private CacheUtils cacheUtils;

    @Autowired
    private RedisLock redisLock;

    //@Autowired
    private ConstantProperties constantProperties = new ConstantProperties();

    //,,httpMethod = "path"
    @ApiOperation(value = "根据姓名和uuid获取用户信息", notes = "这是notes" ,httpMethod = "POST")
    @ApiImplicitParams({
            //@ApiImplicitParam(paramType = "path",name = "name",value = "姓名",dataType = "String",required = true,defaultValue = "龚雄壮"),
            //@ApiImplicitParam(paramType = "body",name = "user.uuid",value = "uuid",dataType = "User",required = true)//defaultValue = "30f54356304811e9a2370235d2b38928"
    })
    @RequestMapping(value = "/user/name/uuid",method = RequestMethod.POST)
    public User findByNameAndUuid(@RequestBody User user) {
        String uuid = user.getUuid();
        String name = user.getName();
        //String uuid = request.getParameter("uuid");
        return testService.findByNameAndUuid(name,uuid);
    }

    @ApiOperation(value = "根据用户姓名获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "name",value = "姓名",dataType = "String",required = true,defaultValue = "龚雄壮")
    })
    @RequestMapping(value = "/user/name",method = RequestMethod.GET)
    public User findByName(@RequestParam("name") String name, HttpServletRequest request) {
        request.getSession().setAttribute("user",testService.findByName(name));
        User user =(User) request.getSession().getAttribute("user");
        return user;
    }

    @ApiOperation(value = "获得所有用户信息")
    @RequestMapping(value = "/users",method = RequestMethod.GET)
    public ResponseResult<PageList<User>> findAll() {
/*        if (!redisLock.setLock("lock", 5 * 60)) {//上锁失败，证明没有释放
            redisLock.releaseLock("lock", redisLock.get("lock"));
        }
        cacheUtils.set("lock", "lock",5*60);*/
        //logger.info(new Test().getTest() +"="+constantProperties.getAppKey());
        cacheUtils.set("test",10,-1);
        if (redisLock.bugGoods("test", "100")) {
            logger.info("购买成功！");
        } else {
            logger.info("库存不足，购买失败！");
        }
        return new ResponseResult<>(testService.findAll());
    }

    @ApiOperation(value = "修改年龄")
    @RequestMapping(value = "/updateUsers",method = RequestMethod.GET)
    public ResponseResult<PageList<User>> updateUsers() {

/*        if (!redisLock.setLock("lock", 5 * 60)) {//上锁失败，证明没有释放
            redisLock.releaseLock("lock", redisLock.get("lock"));
        }
        cacheUtils.set("lock", "lock",5*60);*/
        //logger.info(new Test().getTest() +"="+constantProperties.getAppKey());
        return new ResponseResult<>(testService.updateUsers());
    }

    @ApiOperation(value = "测试增量")
    @GetMapping("/incr")
    public ResponseResult<PageList<User>> incr() {
        ThreadPoolManager threadPoolManager = ThreadPoolManager.newInstance();
        threadPoolManager.perpare();
        for (int i = 0; i < 110; i++) {//testService.incr()
            threadPoolManager.addExecuteTask(()-> {
                if (redisLock.bugGoods("test", "1")) {
                    logger.info("购买成功！");
                } else {
                    System.out.println("库存不足购买失败");
                }
            });
        }
        return new ResponseResult<>();
    }



    @ApiOperation(value = "更新缓存信息")
    @RequestMapping(value = "/putFindAll",method = RequestMethod.GET)
    public ResponseResult<PageList<User>> putFindAll() {

/*        if (!redisLock.setLock("lock", 5 * 60)) {//上锁失败，证明没有释放
            redisLock.releaseLock("lock", redisLock.get("lock"));
        }
        cacheUtils.set("lock", "lock",5*60);*/
        //logger.info(new Test().getTest() +"="+constantProperties.getAppKey());
        return new ResponseResult<>(testService.putFindAll());
    }

    @ApiOperation(value = "获得所有用户信息")
    @RequestMapping(value = "/userList",method = RequestMethod.GET)
    public ResponseResult<List<User>> userList() {
        /*if (redisLock.get("lock")) {

        }*/
        String lock = cacheUtils.getStr("lock");
        logger.info("获取锁key："+lock);
        return new ResponseResult<>(testService.findList());
    }

    @RequestMapping("/user/id/{id}")
    public User findById(@PathVariable("id") Long id) {
        return testService.findById(id);
    }

    @RequestMapping("/requestError")
    public String requestError() {
        return "requestError";
    }
}
