package com.college.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ce.sms")
public class SmsProperties {
     String accessKeyId;   //你自己的accessKeyId
     String accessKeySecret;  //你自己的accessKeySecret
     String signName;        // 签名名称
     String verifyCodeTemplate; //签名模版code
}
