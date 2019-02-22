package com.springboot.controller;

import com.springboot.domain.User;
import com.springboot.service.TestService;
import com.springboot.vo.base.PageList;
import com.springboot.vo.base.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description 测试类
 * @Author gongxz
 * @Date 2019/2/14 15:04
 **/
@RestController
@Api(tags = "用户信息")
@RequestMapping(value = "/test")
public class TestController {

    @Autowired
    private TestService testService;

    //,,httpMethod = "path"
    @ApiOperation(value = "根据姓名和uuid获取用户信息", notes = "这是notes")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path",name = "name",value = "姓名",dataType = "String",required = true,defaultValue = "龚雄壮"),
            @ApiImplicitParam(paramType = "query",name = "uuid",value = "uuid",dataType = "String",required = true,defaultValue = "30f54356304811e9a2370235d2b38928")
    })
    @RequestMapping(value = "/user/name/{name}",method = RequestMethod.GET)
    public User findByNameAndUuid(@PathVariable("name") String name, HttpServletRequest request) {
        String uuid = request.getParameter("uuid");
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
        PageList<User> pageListData = new PageList<>(testService.findAll());
        return new ResponseResult<>(pageListData);
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
