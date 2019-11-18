package com.college.user.service;

import com.college.user.pojo.User;
import com.college.user.vo.PasswordRequest;

import javax.validation.Valid;
import java.util.Map;

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

    //修改用户信息
    void updateUser(User user);

    //修改密码
    void alertPassword(PasswordRequest request);

    //人脸识别注册
    Map<String ,Object> faceRegister(String str, String fid);

    void delFace(Long id);

    Map<String,Object> updateFace(String str, Long id);

    String faceLogin(String str);
}
