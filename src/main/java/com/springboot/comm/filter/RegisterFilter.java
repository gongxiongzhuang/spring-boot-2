package com.springboot.comm.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description request请求过滤器
 * @Author gongxz
 * @Date 2019/2/14 15:31
 **/
public class RegisterFilter implements Filter {

    protected Logger logger =  LoggerFactory.getLogger(this.getClass());
    private static Set<String> GreenUrlSet = new HashSet<String>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        GreenUrlSet.add("/test");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        StringBuffer requestUrl = httpServletRequest.getRequestURL();
        /*if (containsKey(requestUrl.toString()) || containsSuffix(requestUrl.toString())) {
            logger.debug("跳过拦截的url, " + httpServletRequest.getRequestURI());
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            httpServletResponse.sendRedirect("/requestError");
        }*/
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean containsKey(String url) {
        return url.contains("/user/")
                || url.contains("/requestError");
    }

    private boolean containsSuffix(String url) {
        if (url.endsWith(".js")
                || url.endsWith(".css")
                || url.endsWith(".jpg")
                || url.endsWith(".gif")
                || url.endsWith(".png")
                || url.endsWith(".html")
                || url.endsWith(".eot")
                || url.endsWith(".svg")
                || url.endsWith(".ttf")
                || url.endsWith(".woff")
                || url.endsWith(".ico")
                || url.endsWith(".woff2")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void destroy() {

    }
}
