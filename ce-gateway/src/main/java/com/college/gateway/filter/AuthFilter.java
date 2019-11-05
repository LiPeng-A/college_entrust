package com.college.gateway.filter;

import com.college.gateway.config.FilterProperties;
import com.college.gateway.config.JwtProperties;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
//网关授权
@Slf4j
@Component
@EnableConfigurationProperties({FilterProperties.class,JwtProperties.class})
public class AuthFilter extends ZuulFilter {

    @Autowired
    private FilterProperties filterProp;

    @Autowired
    private JwtProperties jwtProp;

    //过滤器类型
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;// 过滤器类型 前置过滤器
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER-1; //过滤器执行顺序
    }


    //是否过滤，true为过滤，false为放行
    @Override
    public boolean shouldFilter() {
        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = ctx.getRequest();
        //获取访问路径
        String path = request.getRequestURI();
        //判断是否放行，放行返回false

        return  !isAllowPath(path);  //是否过滤;
    }

    /**
     * 判断是否是白名单中的路径
     * @param path
     * @return
     */
    private boolean isAllowPath(String path)
    {
        //遍历白名单
        for (String allowPath : filterProp.getAllowPaths()) {
            //判断是否在白名单中
            if(path.startsWith(allowPath))
            {
                log.info("[网关] 在白名单中 path：{} ",path);
                return true;

            }
            log.info("[网关] 不在白名单中 path：{} ",path);
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = ctx.getRequest();
        //获取cookie中的token
        String token = CookieUtils.getCookieValue(request, jwtProp.getCookieName());
        //解析token
        try {
            UserInfo userInf = JwtUtils.getInfoFromToken(token, jwtProp.getPublicKey());
        } catch (Exception e) {
            //解析失败，未登录，拦截下来
            ctx.setSendZuulResponse(false);
            //返回状态码
            ctx.setResponseStatusCode(403);
        }

        return null;
    }
}
