package io.javaweb.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.stereotype.Repository;

/**
 * Created by KevinBlandy on 2017/10/30 11:33
 */
@SpringBootApplication
@MapperScan(value = "io.javaweb.community.mapper",annotationClass = Repository.class)
@ServletComponentScan(basePackages = {"io.javaweb.community.web"})
public class Application extends SpringBootServletInitializer {

    /**
     * main
     * @param args
     * @throws Exception
     */
    public static void main(String[] args)throws Exception{
        SpringApplication.run(Application.class,args);
    }

    /**
     * war support
     * @param application
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
        return application.sources(Application.class);
    }
}