package io.javaweb.community.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
/**
 * 
 * @author KevinBlandy
 *
 */
public class BeanUtils {
	/**
	 * 把Java对象的属性,转换为Map集合
	 * @param bean
	 * @return
	 */
	public static <T> Map<String,Object> getBeanProperties(T bean){
		Map<String,Object> hashMap = new HashMap<>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			if(!GeneralUtils.isEmpty(propertyDescriptors)) {
				for(PropertyDescriptor propertyDescriptor : propertyDescriptors) {
					String propertyName = propertyDescriptor.getName();
					if(!propertyName.equals("class")) {
						//剔除class
						hashMap.put(propertyName, propertyDescriptor.getReadMethod().invoke(bean));
					}
				}
			}
		} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return hashMap;
	}
}
