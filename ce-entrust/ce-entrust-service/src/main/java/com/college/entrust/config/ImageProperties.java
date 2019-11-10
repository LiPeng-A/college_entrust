package com.college.entrust.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ce.upload")
public class ImageProperties {
    private String url;
}
