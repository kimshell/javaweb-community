package io.javaweb.community.utils;

import io.javaweb.community.common.SpringContext;
import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.service.UserService;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by KevinBlandy on 2018/1/22 17:23
 */
public class MentionUtils {
   
	private static final String MENTION_REG = "@(?<name>[0-9a-zA-Z_\\u4e00-\\u9fa5]{2,14})(\\s+|:|：|，|,|\\.|。|!|！|;|；|\\)|）|】|]|-|\\||&nbsp;|》|}|｝|$|</p>|</blockquote>|</h1>|</h2>|</h3>|</h4>|</h5>|</h6>|</b>|&lt;|&gt;|</u>|</span>|</li>|</div>|<img|<video|</i>|</br>|</font>|</strike>|</td>)";

	public static final Pattern MENTION_PATTERN = Pattern.compile(MENTION_REG);

    public static final String MENTION_TEMPLATE = "<a target=\"_blank\" href=\"http://www.javaweb.io/user/{0}\" id=\"at-{1}\">{2}</a>";

    public static final Pattern MENTION_RESTORE = Pattern.compile("<a target=\"_blank\" href=\"http://www.javaweb.io/user/[A-F0-9]{32}\" id=\"at-[A-F0-9]{32}\">(?<name>[0-9a-zA-Z_\\u4e00-\\u9fa5]{2,14})</a>");
    
    public static UserService userService;

    static class Mention{

    	//用户
        private UserEntity user;

        //是否存在
        private boolean exists;

        public Mention(){}

        public Mention(UserEntity userEntity,boolean exists){
            this.user = userEntity;
            this.exists = exists;
        }

        public UserEntity getUser() {
            return user;
        }

        public void setUser(UserEntity user) {
            this.user = user;
        }

        public boolean isExists() {
            return exists;
        }

        public void setExists(boolean exists) {
            this.exists = exists;
        }
    }

    //解析文章中的@
    public static String mentionParse(String content,Map<String,UserEntity> users)throws Exception{
    	
        StringBuilder stringBuilder = new StringBuilder();
        
        Matcher matcher = MENTION_PATTERN.matcher(content);
        
        int lastIndex = 0;
        
        Map<String,Mention> mentions = new HashMap<>();
        
        while (matcher.find()){
        	
            String name = matcher.group("name");
            
            Mention mention =  mentions.get(name);
            
            if(mention == null) {
            	
                //尝试从db检索用户
                UserEntity userEntity = getUserService().queryByName(name);
                
                mention = new Mention();
                
                if (userEntity == null) {
                    mention.setExists(false);
                } else {
                    mention.setExists(true);
                    mention.setUser(userEntity);
                }
                
                mentions.put(name, mention);
            }
            
            if (!mention.exists) {
                continue;
            }
            
            int start = matcher.start("name");
            
            int end = matcher.end("name");
            
            stringBuilder.append(content.substring(lastIndex,start));

            String anchorId = GeneralUtils.getUUID();

            stringBuilder.append(MessageFormat.format(MENTION_TEMPLATE,mention.getUser().getUserId(),anchorId,name));

            if(users != null) {
            	users.put(anchorId,mention.getUser());
            }
            lastIndex = end;
        }
        
        stringBuilder.append(content.substring(lastIndex));
        
        return stringBuilder.toString();
    }
    
    //反解析
    public static String mentionRestore(String content) {
    	Matcher matcher = MENTION_RESTORE.matcher(content);
    	StringBuilder stringBuilder = new StringBuilder();
    	int lastIndex = 0;
    	while(matcher.find()) {
    		stringBuilder.append(content.substring(lastIndex,matcher.start()));
    		stringBuilder.append(matcher.group("name"));
    		lastIndex = matcher.end();
    	}
    	stringBuilder.append(content.substring(lastIndex));
    	return stringBuilder.toString();
    }

    private static UserService getUserService() {
    	if(userService == null) {
    		//!synchronized 
    		userService = SpringContext.getBean(UserService.class);
    	}
    	return userService;
    }
}
