package com.hg.syt;

import com.hg.feign.client.DictFeignClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;

/**
 * @author hougen
 * @program syt_parent
 * @description 医院启动类
 * @create 2023-01-08 19:14
 */
@SpringBootApplication
@MapperScan("com.hg.syt.mapper")
@ComponentScan(basePackages = "com.hg")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.hg.feign.client")
public class ServiceHospApplication {

    public static void main( String[] args ) {
        SpringApplication.run( ServiceHospApplication.class,args );
    }

}


