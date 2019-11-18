package com.college.user.web;

import com.college.common.utils.CookieUtils;
import com.college.user.config.JwtProperties;
import com.college.user.pojo.User;
import com.college.user.service.UserService;
import com.college.user.vo.PasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties prop;

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

    /**
     * 根据id查询用户的信息
     * @param user_id
     * @return
     */
    @GetMapping("userDetail/{user_id}")
    public ResponseEntity<User> queryUserById(@PathVariable("user_id")Long user_id)
    {
        return ResponseEntity.ok(userService.queryUserById(user_id));
    }

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateUser(@RequestBody  User user)
    {
        userService.updateUser(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("password")
    public ResponseEntity<Void> alertPassword(@RequestBody PasswordRequest request){
        userService.alertPassword(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 人脸识别注册
     * @param str
     * @param fid
     * @return
     */
    @PostMapping("/faceReg")
    public ResponseEntity<Map<String,Object>> faceRegister(@RequestParam("url")String str,
                                                           @RequestParam("fid")String fid){
        return ResponseEntity.ok(userService.faceRegister(str,fid));
    }

    /**
     * 删除人脸信息
     * @param id
     * @return
     */
    @DeleteMapping("/delFace/{id}")
    public ResponseEntity<Void> delFace(@PathVariable(value = "id")Long id){
        userService.delFace(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 更新人脸信息
     */
    @PostMapping("/updateFace")
    public ResponseEntity<Map<String,Object>> updateFace(@RequestParam(value = "url")String str,
                                                         @RequestParam("id")Long id
                                                         ){
        return ResponseEntity.ok(userService.updateFace(str,id));
    }

    /**
     * 人脸登录
     * @param str
     * @return
     */
    @PostMapping("/faceVerify")
    public ResponseEntity<Void> faceLogin(@RequestParam("url")String str,
                                          HttpServletResponse response,
                                          HttpServletRequest request
                                          ){
        String  token = userService.faceLogin(str);
        //将token 存入到token中
        CookieUtils.newBuilder(response).httpOnly().request(request)
                .build(prop.getCookieName(),token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
