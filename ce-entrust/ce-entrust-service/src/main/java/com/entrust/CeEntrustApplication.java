package com.entrust;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.college.entrust.client")
public class CeEntrustApplication {
    public static void main(String[] args) {
        SpringApplication.run(CeEntrustApplication.class);
    }
}
