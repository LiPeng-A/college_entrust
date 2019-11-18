package com.college.user.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("ce")
public class SaltProperties {

    private  String salt;
}
