package com.college.user.web;

import com.college.user.pojo.User;
import com.college.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 校验数据
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable("data")String data,
                                             @PathVariable("type")Integer type
                                             ){
        return ResponseEntity.ok(userService.checkData(data,type));
    }


    /**
     * 发送验证码
     * @param phone
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone")String phone){
        userService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 注册用户
     * @param user
     * @param code
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user,@RequestParam(value = "code")String code)
    {
        userService.register(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 登录验证
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/login")
    public ResponseEntity<User> login(@RequestParam("username")String username,
                                      @RequestParam("password")String password
                                      )
    {
        return ResponseEntity.ok(userService.login(username,password));

    }



}
