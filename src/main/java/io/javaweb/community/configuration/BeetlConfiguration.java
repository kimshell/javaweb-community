package io.javaweb.community.configuration;

import java.util.HashMap;
import java.util.Map;

import org.beetl.core.Function;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import io.javaweb.community.beetl.funcs.FriendLinkFunc;
import io.javaweb.community.beetl.funcs.LoginValidateFunc;
import io.javaweb.community.beetl.funcs.PostDateFunction;
import io.javaweb.community.beetl.funcs.RoleValidateFunc;
import io.javaweb.community.beetl.funcs.UserSessionFunc;

/**
 * Created by KevinBlandy on 2017/10/30 15:27
 */
@Configuration
public class BeetlConfiguration {

    /**
     * 模板根目录
     */
    @Value("${beetl.templatesPath}")
    private String templatesPath;

    @Bean(initMethod = "init", name = "beetlConfig")
    public BeetlGroupUtilConfiguration getBeetlGroupUtilConfiguration() {
        BeetlGroupUtilConfiguration beetlGroupUtilConfiguration = new BeetlGroupUtilConfiguration();
        //获取Spring Boot 的ClassLoader
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if(classLoader == null){
            classLoader = BeetlConfiguration.class.getClassLoader();
        }
        //beetlGroupUtilConfiguration.setConfigProperties(extProperties);
        ClasspathResourceLoader classpathResourceLoader = new ClasspathResourceLoader(classLoader, templatesPath);
        beetlGroupUtilConfiguration.setResourceLoader(classpathResourceLoader);
        
        Map<String,Function> funcMap = new HashMap<>();
        funcMap.put("isLogin", loginValidateFunc());
        funcMap.put("isRole", roleValidateFunc());
        funcMap.put("userSession", userSessionFunc());
        funcMap.put("postDate", postDateFunction());
        funcMap.put("friendLink", friendLinkFunc());
        
        beetlGroupUtilConfiguration.setFunctions(funcMap);
        beetlGroupUtilConfiguration.setConfigFileResource(new ClassPathResource("/beetl.properties"));
        beetlGroupUtilConfiguration.init();
        //如果使用了优化编译器，涉及到字节码操作，需要添加ClassLoader
        beetlGroupUtilConfiguration.getGroupTemplate().setClassLoader(classLoader);
        return beetlGroupUtilConfiguration;
    }

    @Bean(name = "beetlViewResolver")
    public BeetlSpringViewResolver getBeetlSpringViewResolver(@Qualifier("beetlConfig") BeetlGroupUtilConfiguration beetlGroupUtilConfiguration) {
        BeetlSpringViewResolver beetlSpringViewResolver = new BeetlSpringViewResolver();
        beetlSpringViewResolver.setContentType("text/html;charset=UTF-8");
        beetlSpringViewResolver.setOrder(0);
        //beetlSpringViewResolver.setPrefix("/");
        beetlSpringViewResolver.setSuffix(".html");
        beetlSpringViewResolver.setConfig(beetlGroupUtilConfiguration);
        return beetlSpringViewResolver;
    }
    
    @Bean
    public LoginValidateFunc loginValidateFunc() {
    	return new LoginValidateFunc();
    }
    
    @Bean
    public RoleValidateFunc roleValidateFunc() {
    	return new RoleValidateFunc();
    }
    
    @Bean
    public UserSessionFunc userSessionFunc() {
    	return new UserSessionFunc();
    }
    
    @Bean
    public PostDateFunction postDateFunction() {
    	return new PostDateFunction();
    }
    
    @Bean
    public FriendLinkFunc friendLinkFunc() {
    	return new FriendLinkFunc();
    }
}
