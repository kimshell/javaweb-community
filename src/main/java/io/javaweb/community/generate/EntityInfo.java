package io.javaweb.community.generate;

import java.util.HashSet;
import java.util.Set;
/**
 * 
 * @author KevinBlandy
 *
 */
public class EntityInfo {
	
	//表名称
	private String tableName;
	
	//id集合
	private Set<String> ids = new HashSet<>();
	
	//DB字段集合
	private Set<String> fields = new HashSet<>();
	
	//mapper路径
	private String mapperClass;
	
	//实体类路径
	private String entityClass;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Set<String> getIds() {
		return ids;
	}

	public void setIds(Set<String> ids) {
		this.ids = ids;
	}

	public Set<String> getFields() {
		return fields;
	}

	public void setFields(Set<String> fields) {
		this.fields = fields;
	}

	public String getMapperClass() {
		return mapperClass;
	}

	public void setMapperClass(String mapperClass) {
		this.mapperClass = mapperClass;
	}

	public String getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(String entityClass) {
		this.entityClass = entityClass;
	}
	
	public Set<String> getAllFields(){
		Set<String> fields = new HashSet<>();
		fields.addAll(ids);
		fields.addAll(this.fields);
		return fields;
	}
}
