package com.college.auth.web;

import com.college.auth.config.JwtProperties;
import com.college.auth.pojo.UserInfo;
import com.college.auth.service.AuthService;
import com.college.auth.utils.JwtUtils;
import com.college.common.Exception.EntrustException;
import com.college.common.enums.ExceptionEnum;
import com.college.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProperties prop;

    @PostMapping("login")
    public ResponseEntity<Void> login(@RequestParam("username")String username,
                                      @RequestParam("password")String password,
                                      HttpServletResponse response,
                                      HttpServletRequest request
                                      ){
        String token = authService.login(username, password);
        //将生成的token写入cookie中
        CookieUtils.newBuilder(response).httpOnly().request(request)
                .build(prop.getCookieName(),token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 校验用户登录状态
     * @param token
     * @param request
     * @param response
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("CE-TOKEN")String token,
                                           HttpServletRequest request,
                                           HttpServletResponse response
                                           ){
        try {
            //获取当前用户信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            //为确保用户每次操作完成后都能刷新cookie中token的时间
            //生成新的token
            String newToken = JwtUtils.generateToken(userInfo, prop.getPrivateKey(), prop.getExpire());
            //再次写入到cookie中
            CookieUtils.newBuilder(response).httpOnly().request(request).setPath("/").
                    build(prop.getCookieName(),newToken);
            //将用户的信息返回到页面
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            throw new EntrustException(ExceptionEnum.UN_AUTHORIZED);
        }
    }

    /**
     * 注销登录
     * @param request
     * @param response
     * @return
     */
    @PutMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response
                                       ){
        //创建空的token
        String newToken=null;
        //将新的token'值重新写入cookie中
        CookieUtils.newBuilder(response).httpOnly().request(request).setPath("/").build(prop.getCookieName(),newToken);
        //返回结果
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
