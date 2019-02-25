package com.springboot.comm.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by DingYS on 2017/2/20.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger =  LoggerFactory.getLogger(this.getClass());

    private static final String DEFAULT_ERROR_VIEW = "error";

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(Exception e, HttpServletRequest request) {
        logger.info("请求地址：" + request.getRequestURL());
        ModelAndView mav = new ModelAndView();
        logger.error("异常信息：",e);
        mav.setViewName(DEFAULT_ERROR_VIEW);
        return mav;
    }
}
