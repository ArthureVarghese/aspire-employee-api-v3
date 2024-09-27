package com.aspire.employee_api_v3.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {

    @Bean
    public Cache<String,Object> customCache(){
        return new Cache<>();
    }
}
