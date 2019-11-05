package com.college.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "ce.filter")
public class FilterProperties {
    List<String> allowPaths=new ArrayList<>();
}
