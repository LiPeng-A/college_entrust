package com.college;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.college.user.mapper")
public class CeUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(CeUserApplication.class);
    }
}
