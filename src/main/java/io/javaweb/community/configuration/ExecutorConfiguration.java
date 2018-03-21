package io.javaweb.community.configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * 
 * 异步线程池
 * @author KevinBlandy
 *
 */
@Configuration
public class ExecutorConfiguration {
	
	@Bean("executor")
    public ExecutorService executor() {
		return Executors.newFixedThreadPool(10);
    }
}
