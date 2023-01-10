package com.hg.common;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-10 23:49
 */
@SpringBootApplication
@MapperScan("com.hg.common.mapper")
@ComponentScan(basePackages = "com.hg")
public class CommonApplication {

    public static void main( String[] args ) {
        SpringApplication.run( CommonApplication.class,args );
    }
}


