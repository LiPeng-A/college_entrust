package com.college.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum  ExceptionEnum {

    INVALID_USERNAME_TYPE(500,"无效的用户名信息"),
    INVALID_CODE(500,"无效的验证码"),
    REGISTER_ERROR(500,"注册服务器失败"),
    INVALID_USERNAME_AND_PASSWORD(500,"无效的用户名或密码"),
    UN_AUTHORIZED(500,"未授权"),
    ENTRUST_NOT_FOUND(404,"委托信息没找到"),

    ;
    private Integer status;
    private String msg;
}
