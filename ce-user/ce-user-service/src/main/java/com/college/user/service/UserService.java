package com.college.user.service;

import com.college.user.pojo.User;

import javax.validation.Valid;

public interface UserService {
    //校验数据
    Boolean checkData(String data, Integer type);

    //发送验证码
    void sendCode(String phone);

    //注册用户
    void register(User user, String code);

    //查询用户
    User login(String username, String password);

    //根据id查询用户
    User queryUserById(Long user_id);
}
