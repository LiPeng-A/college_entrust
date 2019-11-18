package com.college.face.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ce.face")
public class FaceProperties {
    private String app_id;
    private String api_key;
    private String secret_key;
    private String group;
}
