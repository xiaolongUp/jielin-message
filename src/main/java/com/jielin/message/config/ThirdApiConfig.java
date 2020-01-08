package com.jielin.message.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "third")
@Getter
@Setter
public class ThirdApiConfig {

    private String jlWebApiUrl;

}
