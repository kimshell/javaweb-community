package io.javaweb.community.datasource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * Created by KevinBlandy on 2017/10/30 14:12
 */
@Component
@Aspect
@Order(-9999)       //最优先执行
public class DynamicDataSourceAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    /**
     * 在进入Service方法之前执行
     * @param point
     * @throws NoSuchMethodException
     */
    @Before("execution(public * io.javaweb.community.service..*.*(..))")
    public void before(JoinPoint point) throws NoSuchMethodException {

        Object target = point.getTarget();

        String methodName = point.getSignature().getName();

        Object[] args = point.getArgs();

        Class<?>[] parameterTypes = ((MethodSignature)point.getSignature()).getMethod().getParameterTypes();

        Method method = null;

        method = target.getClass().getMethod(methodName, parameterTypes);

        //桥接方法
        if(method.isBridge()){
            for(int i = 0; i < args.length; i++){
                Class<?> genClazz = this.getSuperClassGenricType(target.getClass(),0);
                if(args[i].getClass().isAssignableFrom(genClazz)){
                    parameterTypes[i] = genClazz;
                }
            }
            method = target.getClass().getMethod(methodName, parameterTypes);
        }

        LOGGER.info("当前事务方法  " + methodName);

        Transactional transactional = method.getAnnotation(Transactional.class);
        if(transactional != null && transactional.readOnly()){
            LOGGER.info("动态数据源 - 读库");
            DynamicDataSourceHolder.markSlave();
        }else {
            LOGGER.info("动态数据源 - 写库");
            DynamicDataSourceHolder.markMaster();
        }
    }

    public Class<?> getSuperClassGenricType(Class<?> clazz, int index) {
        Type genType = clazz.getGenericSuperclass();                // 得到泛型父类
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class<?>) params[index];
    }
}
