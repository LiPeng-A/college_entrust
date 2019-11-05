package com.college.auth.service.impl;

import com.college.auth.client.UserClient;
import com.college.auth.config.JwtProperties;
import com.college.auth.pojo.UserInfo;
import com.college.auth.service.AuthService;
import com.college.auth.utils.JwtUtils;
import com.college.common.Exception.EntrustException;
import com.college.common.enums.ExceptionEnum;
import com.college.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties prop;

    @Override
    public String login(String username, String password) {

        try {
            //校验用户名和密码
            User user = userClient.login(username, password);
            if(user==null)
            {
                throw new EntrustException(ExceptionEnum.INVALID_USERNAME_AND_PASSWORD);
            }
            //生成token
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername()),
                    prop.getPrivateKey(), prop.getExpire());
            return token;
        } catch (Exception e) {
            log.error("[授权服务]，授权失败，原因：用户名或密码错误 用户名：{}",username,e);
           throw new EntrustException(ExceptionEnum.INVALID_USERNAME_AND_PASSWORD);
        }
    }
}
