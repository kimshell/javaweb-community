package io.javaweb.community.generate;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
/**
 * 
 * @author KevinBlandy
 *
 */
public class EntityWorker {
	
	/**
	 * 获取类描述实体
	 * @param classes
	 * @return
	 */
	public static Set<EntityInfo> getEntityInfo(Set<Class<?>> classes,Predicate<Class<?>> filter){
		Set<EntityInfo> entityInfos = new HashSet<>();
		classes.stream().filter(filter).forEach(clazz -> {
			EntityInfo entityInfo = new EntityInfo();
			//实例类路径
			entityInfo.setEntityClass(clazz.getName());
			//mapper类路径
			entityInfo.setMapperClass(clazz.getAnnotation(Entity.class).mapper());
			//表名称
			entityInfo.setTableName(clazz.getAnnotation(Entity.class).table());
			getFields(clazz).stream().forEach(field -> {
				if(!field.isAnnotationPresent(Ignore.class)) {
					if(!field.getName().equals("serialVersionUID") && !Character.isUpperCase(field.getName().toCharArray()[0])) {
						if(field.isAnnotationPresent(Id.class)) {
							entityInfo.getIds().add(field.getName());
						}else {
							entityInfo.getFields().add(field.getName());
						}
					}
				}
			});
			entityInfos.add(entityInfo);
		});
		return entityInfos;
	}


	/**
	 * 获取所有字段信息
	 * @param clazz
	 * @return
	 */
	public static Set<Field> getFields(Class<?> clazz){
		Set<Field> fields = new HashSet<>();
		Class<?> currentClazz = clazz;
		while(currentClazz != null) {
			fields.addAll(Arrays.asList(currentClazz.getDeclaredFields()));
			currentClazz = currentClazz.getSuperclass();
		}
		return fields;
	}
}
