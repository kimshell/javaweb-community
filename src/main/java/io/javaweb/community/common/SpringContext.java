package io.javaweb.community.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by KevinBlandy on 2018/1/18 16:50
 */
public class SpringContext implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringContext.class);

    /**
     * ApplicationContext
     */
    private static ApplicationContext applicationContext;

    /**
     * Spring调用注入
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)throws BeansException {
        LOGGER.info("IOC容器注入:{}",applicationContext);
        SpringContext.applicationContext = applicationContext;
    }

    /**
     * 获取ApplicationContext
     * @return
     */
    public static ApplicationContext getApplicationContext(){
        return SpringContext.applicationContext;
    }

    /**
     * 根据类类型获取实例
     * @param clazz
     * @return
     */
    public static <T> T getBean(Class<T> clazz){
        try{
            return getApplicationContext().getBean(clazz);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据bean的id和类类型获取实例
     * @param clazz
     * @param name
     * @return
     */
    public static <T> T getBean(Class<T> clazz,String name){
        try{
            return getApplicationContext().getBean(clazz, name);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据bean的id获取实例
     * @param name
     * @return
     */
    public static Object getBean(String name){
        try{
            return getApplicationContext().getBean(name);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
