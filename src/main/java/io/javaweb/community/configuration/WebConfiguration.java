package io.javaweb.community.configuration;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

import io.javaweb.community.utils.DateUtils;
import io.javaweb.community.web.converter.DateConverter;
import io.javaweb.community.web.converter.EnumConverter;
import io.javaweb.community.web.interceptor.BlackListInterceptor;
import io.javaweb.community.web.interceptor.ManagerSessionInterceptor;
import io.javaweb.community.web.interceptor.ResourceCleanInterceptor;
import io.javaweb.community.web.interceptor.UserSessionInterceptor;
import io.javaweb.community.web.interceptor.UserSessionLoadInterceptor;
import io.javaweb.community.web.interceptor.VerifyCodeInterceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Created by KevinBlandy on 2017/10/30 11:43
 */
@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter {

    /**
     * fastjson
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    	super.configureMessageConverters(converters);
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        fastJsonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat(DateUtils.DEFAULF_DATETIME_PATTERN);
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        //fastJsonConfig.setSerializerFeatures();
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        converters.add(fastJsonHttpMessageConverter);
    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        super.addResourceHandlers(registry);
//        registry.addResourceHandler("/robots.txt}").addResourceLocations("classpath:/robots.txt");
//    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        super.addFormatters(registry);
//        registry.addConverterFactory(new ConverterFactory<String, Enum<?>>() {
//			@Override
//			public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
//				return null;
//			}
//		});
        registry.addConverter(new EnumConverter());
        registry.addConverter(new DateConverter());
    }

    //    /**
//     * cors
//     */
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//		super.addCorsMappings(registry);
//		registry.addMapping("/**")  
//		.allowedOrigins("*")  
//		.allowCredentials(true)  
//		.allowedMethods("GET", "POST", "DELETE", "PUT","OPTIONS")
//		.maxAge(1728000);  		//20天
//    }
    
    /**
     * interceptors
     */
    @Override
	public void addInterceptors(InterceptorRegistry registry) {
    	super.addInterceptors(registry);
    	
    	/**********		全局拦截器		************/
    	//黑名单
    	registry.addInterceptor(blackListInterceptor())
    				.addPathPatterns("/**");
    	
    	//资源清理
    	registry.addInterceptor(resourceCleanInterceptor())
    				.addPathPatterns("/**")
                    .excludePathPatterns("/upload")
                    .excludePathPatterns("/login")
    				.excludePathPatterns("/register")
    				.excludePathPatterns("/manager/login");
    				
    
    	//验证码
    	registry.addInterceptor(verifyCodeInterceptor())
    				.addPathPatterns("/login")				//登录
    				.addPathPatterns("/register")			//注册
    				.addPathPatterns("/post/create")		//发帖
    				.addPathPatterns("/post/reply")			//回帖
    				.addPathPatterns("/post/update")		//修改帖子
    				.addPathPatterns("/upload")				//上传图片
    				.addPathPatterns("/user/updatePass")	//修改密码
    				.addPathPatterns("/manager/login");		//管理员登录
    	
    	/**********		用户拦截器		************/
    	//会话校验
    	registry.addInterceptor(userSessionInterceptor())
					.addPathPatterns("/**")
                    .excludePathPatterns("/index","/login","/register","/error")
                    .excludePathPatterns("/oss/**")
                    .excludePathPatterns("/manager/**");
    	

    	
    	//用户状态
    	
    	//不需要登录,却需要加载当前用户信息到request(用于视图层渲染)的接口
    	registry.addInterceptor(userSessionLoadInterceptor())
                    .addPathPatterns("/**")

                    //排查及,非必须登录接口
                    .excludePathPatterns("/user/query")
                    .excludePathPatterns("/verifyCode")
                    .excludePathPatterns("/register")
                    .excludePathPatterns("/login")
                    .excludePathPatterns("/register")
                    .excludePathPatterns("/oss/**")
                    
                    //排除,必须登录接口
                    .excludePathPatterns("/upload")
                    .excludePathPatterns("/post/create")
                    .excludePathPatterns("/post/update")
                    .excludePathPatterns("/post/reply")
                    .excludePathPatterns("/activation/send")
                    .excludePathPatterns("/activation/validate")
                    
                    //排除,manager系统接口
    				.excludePathPatterns("/manager/**");
    				
    	
    	/**********		manager拦截器		************/
    	registry.addInterceptor(managerSessionInterceptor())
    				.addPathPatterns("/manager/**")
    				.excludePathPatterns("/manager/login");
	}
    
    /**
     * 跨域
     */
//	@Override 
//	public void addCorsMappings(CorsRegistry registry) { 
//		registry.addMapping("/manager/**")
//				.allowedOrigins("*")
//				.allowCredentials(true)
//				.allowedMethods("GET, POST, PUT, DELETE, OPTIONS")
//				.allowedHeaders("Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With")
//				.maxAge(1800);
//		
//		registry.addMapping("/verifyCode")
//				.allowedOrigins("*")
//				.allowCredentials(true)
//				.allowedMethods("GET, POST, PUT, DELETE, OPTIONS")
//				.allowedHeaders("Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With")
//				.maxAge(1800);
//	} 
    
    
    @Bean
    public ResourceCleanInterceptor resourceCleanInterceptor() {
    	return new ResourceCleanInterceptor();
    }
    
    @Bean
    public BlackListInterceptor blackListInterceptor() {
    	return new BlackListInterceptor();
    }
    
    @Bean
    public UserSessionInterceptor userSessionInterceptor() {
    	return new UserSessionInterceptor();
    }
    
    @Bean
    public VerifyCodeInterceptor verifyCodeInterceptor() {
    	return new VerifyCodeInterceptor();
    }
    
    @Bean
    public UserSessionLoadInterceptor userSessionLoadInterceptor() {
    	return new UserSessionLoadInterceptor();
    }
    
    @Bean
    public ManagerSessionInterceptor managerSessionInterceptor() {
    	return new ManagerSessionInterceptor();
    }
}
