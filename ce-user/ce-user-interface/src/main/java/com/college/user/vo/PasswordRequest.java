package com.college.user.vo;

import lombok.Data;

@Data
public class PasswordRequest {
    private Long id;
    private String password;
    private String newPassword;
    private String confirmPassword;
}
