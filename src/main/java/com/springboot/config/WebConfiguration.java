package com.springboot.config;

import com.springboot.comm.filter.RegisterFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description 自定义请求过滤器
 * @Author gongxz
 * @Date 2019/2/14 15:27
 **/
@Configuration
public class WebConfiguration {

    @Bean
    public FilterRegistrationBean filterRegistration() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new RegisterFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("key","value");
        filterRegistrationBean.setName("RegisterFilter");
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }
}
