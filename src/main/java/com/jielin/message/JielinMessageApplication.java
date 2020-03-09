package com.jielin.message;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.jielin.message.dao.mysql")
@EnableRetry
@EnableApolloConfig
@EnableScheduling
public class JielinMessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(JielinMessageApplication.class, args);
    }

}
