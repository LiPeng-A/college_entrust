package com.college.entrust.interceptor;

import com.college.auth.pojo.UserInfo;
import com.college.auth.utils.JwtUtils;
import com.college.common.utils.CookieUtils;
import com.college.entrust.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties prop;

    @Autowired
    private static final ThreadLocal<UserInfo> tl=new ThreadLocal<>(); //创建传递用户信息的线程

    //通过构造方法注入jwtpropties
    public UserInterceptor(JwtProperties prop){
        this.prop=prop;
    }

    //前置拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //获取用户的登录信息
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());
        //解析token
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            //传递用户信息
            tl.set(userInfo);
            //放行
            return  true;
        } catch (Exception e) {
            String requestURI = request.getRequestURI();
            log.error("[委托服务] 拦截器，解析用户信息失败 {}",requestURI);


            return false;
        }
    }

    //最后渲染完视图进行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //用完数据，最后清空
        tl.remove();
    }

    //获取用户信息的方法
    public static UserInfo getUser() {

        return tl.get();
    }

}
