package com.springboot.comm.config;

import com.springboot.comm.Enum.CodeMessage;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import com.springboot.comm.utils.ListUtils;

import java.util.List;

/**
 * @Description swagger2
 * @Date 2019/2/21 18:31
 * @Created by gongxz
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${swagger.version}") private String version;
    @Value("${swagger.basePackage}") private String basePackage;

    @Bean
    public Docket createRestApi() {
        /*List<Parameter> operationParameters = new ArrayList<>();
        ParameterBuilder parameter = new ParameterBuilder();//参数
        parameter.name("name").defaultValue("龚雄壮").description("姓名").required(true).parameterType("header")
        .modelRef(new ModelRef("string"));//todo
        operationParameters.add(parameter.build());*/
        return new Docket(DocumentationType.SWAGGER_2)
                //.groupName("api项目")//todo
                .apiInfo(apiInfo())
                //.genericModelSubstitutes(DeferredResult.class)//todo
                .useDefaultResponseMessages(false)//不使用默认的响应信息描述
                .globalResponseMessage(RequestMethod.GET,customerResponseMessage())//自定义全局的响应信息描述 获取
                .globalResponseMessage(RequestMethod.POST,customerResponseMessage())//自定义全局的响应信息描述 在服务器新建一个资源
                .globalResponseMessage(RequestMethod.DELETE,customerResponseMessage())//自定义全局的响应信息描述 删除
                .globalResponseMessage(RequestMethod.PUT,customerResponseMessage())//自定义全局的响应信息描述 在服务器更新资源（客户端提供完整资源数据）。
                .globalResponseMessage(RequestMethod.PATCH,customerResponseMessage())//自定义全局的响应信息描述 在服务器更新资源（客户端提供需要修改的资源数据）
                //.globalOperationParameters(operationParameters)//全局的参数说明，作用在所有方法上。
                //.forCodeGeneration(true)//todo
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))//需要显示的接口路径
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))//这里采用包含注解的方式来确定要显示的接口
                .paths(PathSelectors.any())//todo
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("微信小程序&微信公众号api文档")//页面标题
                .description("微信小程序和微信公众号api文档")//描述
                .termsOfServiceUrl("http://laog.net")
                .version(version)//版本号
                .build();
    }

    /**
     * 自定义返回信息
     * @param
     * @return
     */
    private List<ResponseMessage> customerResponseMessage(){
        return ListUtils.ObjectConvertToList(
                new ResponseMessageBuilder()//500000
                        .code(Integer.valueOf(CodeMessage.Error.getCode()))
                        .message(CodeMessage.Error.getMsg())
                        //.responseModel(new ModelRef("Error"))
                        .build(),
                new ResponseMessageBuilder()//400001
                        .code(Integer.valueOf(CodeMessage.ParamError.getCode()))
                        .message(CodeMessage.ParamError.getMsg())
                        .build());
    }
}
