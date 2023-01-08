package com.hg.syt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author hougen
 * @program syt_parent
 * @description 医院启动类
 * @create 2023-01-08 19:14
 */
@SpringBootApplication
@MapperScan("com.hg.syt.mapper")
@ComponentScan(basePackages = "com.hg")
public class ServiceHospApplication {
    public static void main( String[] args ) {
        SpringApplication.run( ServiceHospApplication.class,args );
    }
}


