package com.hg.syt;

import org.apache.http.conn.util.DomainType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import javax.annotation.Resource;

/**
 * @author hougen
 * @program syt_parent
 * @description 医院启动类
 * @create 2023-01-08 19:14
 */
//@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@SpringBootApplication
@MapperScan("com.hg.syt.mapper")
@ComponentScan(basePackages = "com.hg")
public class ServiceHospApplication {

//    @Resource
//    private MongoTemplate mongoTemplate;
//
//    @Resource
//    private MongoMappingContext mongoMappingContext;
    public static void main( String[] args ) {
        SpringApplication.run( ServiceHospApplication.class,args );
    }

//    @EventListener( ApplicationReadyEvent.class)
//    public void initIndicesAfterStartup() {
//        IndexOperations indexOps = mongoTemplate.indexOps(DomainType.class);
//        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
//        resolver.resolveIndexFor( DomainType.class).forEach(indexOps::ensureIndex);
//    }
}


