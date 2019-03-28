package com.springboot.controller;

import com.springboot.comm.base.BaseController;
import com.springboot.domain.User;
import com.springboot.service.TestService;
import com.springboot.comm.page.PageList;
import com.springboot.comm.result.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public User findByName(@RequestParam("name") String name) {
        return testService.findByName(name);
    }

    @ApiOperation(value = "获得所有用户信息")
    @RequestMapping(value = "/users",method = RequestMethod.GET)
    public ResponseResult<PageList<User>> findAll() {
        return new ResponseResult<>(testService.findAll());
    }

    @ApiOperation(value = "获得所有用户信息")
    @RequestMapping(value = "/userList",method = RequestMethod.GET)
    public ResponseResult<List<User>> userList() {
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
