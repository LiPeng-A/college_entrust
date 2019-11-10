package com.college.entrust.config;

import com.college.common.utils.IdWorker;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IdWorkerProperties.class)
public class IdWorkerConfig {
    @Bean
    public IdWorker idWorker(IdWorkerProperties prop)
    {
        return new IdWorker(prop.getWorkerId(),prop.getDataCenterId());
    }
}
