package io.javaweb.community.configuration;

import io.javaweb.community.common.SpringContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by KevinBlandy on 2018/1/18 16:51
 */
@Configuration
public class SpringContextConfiguration {

    @Bean
    public SpringContext springContext(){
        return new SpringContext();
    }
}
