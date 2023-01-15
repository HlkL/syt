package com.hg.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author hougen
 * @program syt_parent
 * @description 用户管理
 * @create 2023-01-16 01:33
 */
@SpringBootApplication
@MapperScan("com.hg.user.mapper")
@ComponentScan(basePackages = "com.hg")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.hg.feign.client")
public class ServiceUserApplication {
    public static void main( String[] args ) {
        SpringApplication.run( ServiceUserApplication.class,args );
    }
}


