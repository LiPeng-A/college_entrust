package com.college.user.utils;
 
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CodecUtils {

    public static String passwordBcryptEncode(String salt,String password){ //给密码中加盐
 
        return new BCryptPasswordEncoder().encode(salt + password);
    }
 
    public static Boolean passwordBcryptDecode(String rawPassword,String encodePassword){
        return new BCryptPasswordEncoder().matches(rawPassword,encodePassword);
    }
}