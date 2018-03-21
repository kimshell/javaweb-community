package io.javaweb.community.generate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Set;

/**
 * 
 * @author KevinBlandy
 *
 */
public class MapperWork {
	
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	public static final String XML_IF = "<if test=\"{0}\">{1}</if>";
	
	public static void create(Set<EntityInfo> entityInfos,String targetDir) {
		entityInfos.stream().forEach(item -> {
			try {
				create(item,targetDir);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	public static void create(EntityInfo entityInfo,String targetDir) throws Exception {
		Path baseDir = Paths.get(targetDir);
		
		if(!Files.exists(baseDir)) {
			Files.createDirectories(baseDir);
		}

		StringBuilder stringBuilder = new StringBuilder();
		
		try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(MapperWork.class.getResourceAsStream("/template.xml"),StandardCharsets.UTF_8))){
			String line;
			
			while((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(LINE_SEPARATOR);
			}
			
			String result = render(stringBuilder.toString(),entityInfo);

			Path targetFile = baseDir.resolve(entityInfo.getEntityClass().substring(entityInfo.getEntityClass().lastIndexOf(".") + 1).replace("Entity", "") + "-mapper.xml");
			
			if(Files.exists(targetFile)) {
				Files.delete(targetFile);
			}
			
			Files.write(targetFile, result.getBytes(StandardCharsets.UTF_8));
		}
	}
	
	public static String render (String template,EntityInfo entityInfo)throws Exception {
		return template.
				replace("AUTO_CREATE_MAPPER", entityInfo.getMapperClass()).
				replace("AUTO_CREATE_BASE_FIELD", baseField(entityInfo)).
				replace("AUTO_CREATE_ENTITY",entityInfo.getEntityClass()).
				replace("AUTO_CREATE_BASE_SELECT", baseSelect(entityInfo)).
				replace("AUTO_CREATE_QUERYBYPRIMARYKEY", queryByPrimaryKey(entityInfo)).
				replace("AUTO_CREATE_CREATE", create(entityInfo)).
				replace("AUTO_CREATE_UPDATEBYPRIMARYKEYSELECTIVE", updateByPrimaryKeySelective(entityInfo)).
				replace("AUTO_CREATE_UPDATEBYPRIMARYKEY", updateByPrimaryKey(entityInfo)).
				replace("AUTO_CREATE_DELETEBYPRIMARYKEY", deleteByPrimaryKey(entityInfo)).
				replace("AUTO_CREATE_DELETEBYPARAMSELECTIVE", deleteByParamSelective(entityInfo)).
				replace("AUTO_CREATE_TB_NAME", entityInfo.getTableName());
	}
	
	
	public static String baseField(EntityInfo entityInfo) throws Exception{
		StringBuilder stringBuilder = new StringBuilder();
		Set<String> fields = entityInfo.getAllFields();
		int size = 0;
		for(String property : fields) {
			String template = "`${alias}`.`" + formatStyle(property) + "` AS `"+property+"`";
			if(size > 0) {
				template = "		" + template;
			}
			if(size != fields.size() - 1) {
				template += ',';
				template += LINE_SEPARATOR;
			}
			stringBuilder.append(template);
			size ++;
		}
		return stringBuilder.toString();
	}
	
	public static String baseSelect(EntityInfo entityInfo)throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		Set<String> fields = entityInfo.getAllFields();
		int size = 0;
		for(String property : fields) {
			if(size > 0) {
				stringBuilder.append("			");
			}
			stringBuilder.append(MessageFormat.format(XML_IF,property+" != null","AND t.`"+formatStyle(property) +"` = #{"+property+"}"));
			if(size != fields.size() - 1) {
				stringBuilder.append(LINE_SEPARATOR);
			}
			size ++;
		}
		return stringBuilder.toString();
	}
	
	public static String queryByPrimaryKey(EntityInfo entityInfo)throws Exception {
		StringBuilder sb = new StringBuilder();
		Set<String> ids = entityInfo.getIds();
		int size = 0;
		for(String id : ids) {
			if(size != 0) {
				sb.append(LINE_SEPARATOR);
				sb.append("		AND");
				sb.append(LINE_SEPARATOR);
				sb.append("			t.`"+formatStyle(id) + "` = #{"+id+"}");
			}else {
				sb.append("	t.`"+formatStyle(id) + "` = #{"+id+"}");
			}
			size ++;
		}
		return sb.toString();
	}
	
	public static String queryByParamSelective(EntityInfo entityInfo)throws Exception {
		return "";
	}
	
	public static String queryByParamSelectiveSole(EntityInfo entityInfo)throws Exception {
		return "";
	}
	
	public static String create(EntityInfo entityInfo)throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
		stringBuilder.append(LINE_SEPARATOR);
		for(String property : entityInfo.getAllFields()) {
			stringBuilder.append("			");
			stringBuilder.append(MessageFormat.format(XML_IF,property+" != null","`"+formatStyle(property)+"`,"));
			stringBuilder.append(LINE_SEPARATOR);
		}
		stringBuilder.append("		</trim>");
		stringBuilder.append(LINE_SEPARATOR);
		stringBuilder.append("		<trim prefix=\" VALUES(\" suffix=\")\" suffixOverrides=\",\">");
		stringBuilder.append(LINE_SEPARATOR);
		for(String property : entityInfo.getAllFields()) {
			stringBuilder.append("			");
			stringBuilder.append(MessageFormat.format(XML_IF,property+" != null","#{"+property+"},"));
			stringBuilder.append(LINE_SEPARATOR);
		}
		stringBuilder.append("		</trim>");
		return stringBuilder.toString();
	}
	
	public static String updateByPrimaryKeySelective(EntityInfo entityInfo)throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<set>"+LINE_SEPARATOR);
		for(String property : entityInfo.getFields()) {
			stringBuilder.append("			");
			stringBuilder.append(MessageFormat.format(XML_IF,property+" != null","`"+formatStyle(property)+"` = #{"+property+"},"));
			stringBuilder.append(LINE_SEPARATOR);
		}
		stringBuilder.append("		</set>" + LINE_SEPARATOR);
		
		Set<String> ids = entityInfo.getIds();
		int size = 0;
		for(String id : ids) {
			if(size != 0) {
				stringBuilder.append(LINE_SEPARATOR);
				stringBuilder.append("		AND");
				stringBuilder.append(LINE_SEPARATOR);
				stringBuilder.append("			`"+formatStyle(id) + "` = #{"+id+"}");
			}else {
				stringBuilder.append("		WHERE"+ LINE_SEPARATOR +"			`" + formatStyle(id) + "` = #{" + id + "}");
			}
			size ++;
		}
		return stringBuilder.toString();
	}
	
	public static String updateByPrimaryKey(EntityInfo entityInfo)throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		
		Set<String> fields = entityInfo.getFields();
		
		int x = 0;
		
		for(String property : fields) {
			property = "`"+formatStyle(property)+"` = #{"+property+"}";
			if(x < (fields.size() - 1)) {
				property += ',';
			}
			if(x > 0) {
				stringBuilder.append("			");
			}
			stringBuilder.append(property);
			stringBuilder.append(LINE_SEPARATOR);		
			x ++;
		}
		
		Set<String> ids = entityInfo.getIds();
		int size = 0;
		for(String id : ids) {
			if(size != 0) {
				stringBuilder.append(LINE_SEPARATOR);
				stringBuilder.append("		AND");
				stringBuilder.append(LINE_SEPARATOR);
				stringBuilder.append("			`"+formatStyle(id) + "` = #{"+id+"}");
			}else {
				stringBuilder.append("		WHERE"+ LINE_SEPARATOR + "			`"+formatStyle(id) +"` = #{"+id+"}");
			}
			size ++;
		}
		
		return stringBuilder.toString();
	}
	
	public static String deleteByPrimaryKey(EntityInfo entityInfo)throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("DELETE FROM `" + entityInfo.getTableName() + "`");
		Set<String> ids = entityInfo.getIds();
		int size = 0;
		for(String id : ids) {
			if(size != 0) {
				stringBuilder.append(" AND `"+formatStyle(id) + "` = #{"+id+"}");
			}else {
				stringBuilder.append(" WHERE `"+formatStyle(id)+"` = #{"+id+"}");
			}
			size ++;
		}
		return stringBuilder.toString();
	}
	
	public static String deleteByParamSelective(EntityInfo entityInfo)throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		Set<String> fields = entityInfo.getAllFields();
		int x = 0;
		for(String property : fields) {
			if(x > 0) {
				stringBuilder.append("			");
			}
			stringBuilder.append(MessageFormat.format(XML_IF,property+" != null","AND `"+formatStyle(property) +"` = #{"+property+"}"));
			if(x != fields.size() - 1) {
				stringBuilder.append(LINE_SEPARATOR);
			}
			x++;
		}
		return stringBuilder.toString();
	}
	
	/**
	 * 格式化文字样式
	 * @param string
	 * @return
	 */
	public static String formatStyle(String string) {
		StringBuilder sb = new StringBuilder();
		char[] chars = string.toCharArray();
		for(int x = 0;x < chars.length; x++) {
			if(Character.isUpperCase(chars[x])) {
				sb.append("_");
			}
			sb.append(chars[x]);
		}
		return sb.toString().toLowerCase();
	}
}





























