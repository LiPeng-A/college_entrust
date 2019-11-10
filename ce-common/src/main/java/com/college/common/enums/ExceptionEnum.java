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
    USER_NOT_FOUND(404,"用户信息未找到"),
    INVALID_USER_INFO(404,"无效的用户信息"),
    ACCEPT_ENTRUST_ERROR(500,"接受委托信息失败"),
    ACCEPT_ENTRUST_NOT_FOUND(404,"接受的委托信息查询失败"),
    DELETE_ACCEPT_ENTRUST_ERROR(500,"放弃委托失败"),
    ENTRUST_STATUS_UPDATE_FAILED(500,"委托状态更新失败了"),
    INVALID_FILE_TYPE(500,"无效的文件类型"),
    UPLOAD_ERROR(500,"上传文件失败"),
    ENTRUST_INSERT_ERROR(500,"委托添加失败"),
    UPDATE_ENTRUST_ERROR(500,"委托修改失败"),
    DELETE_ENTRUST_ERROR(500,"委托删除失败"),

    ;
    private Integer status;
    private String msg;
}
