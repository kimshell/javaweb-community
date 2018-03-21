package io.javaweb.community.generate;

import java.util.Set;

/**
 * main
 * @author KevinBlandy
 *
 */
public class Main {
	
	//扫描包根路径
	public static final String basePackage = "io.javaweb.community.entity";
	
	//生成的文件路径
	public static final String targetDir = "E:\\python-workspace\\javaweb-community\\src\\main\\resources\\mapper";
	
	public static void main(String[] args) throws Exception {
		
		//读取所有的entity类
		Set<Class<?>> classes = ClassUtils.getClasses(basePackage);
		
		//获取所有类的描述信息
		Set<EntityInfo> entityInfos = EntityWorker.getEntityInfo(classes,item -> {
			//条件过滤  --> 仅仅解析 Table 注解标识的类 
			return item.isAnnotationPresent(Entity.class);
		});
		
		//生成类
		MapperWork.create(entityInfos,targetDir);
	}
}
