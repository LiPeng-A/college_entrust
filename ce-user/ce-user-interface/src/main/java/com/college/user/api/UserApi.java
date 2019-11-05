package com.college.user.api;

import com.college.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserApi {

    /**
     * 登录验证
     *
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/login")
    User login(@RequestParam("username") String username,
               @RequestParam("password") String password
    );
}
