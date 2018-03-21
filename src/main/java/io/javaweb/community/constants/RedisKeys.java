package io.javaweb.community.constants;

public class RedisKeys {
	
	//验证码
	public static final String VERIFY_CODE = "verify_code_";
	
	//用户会话
	public static final String SESSION_USER = "session_user_";
	
	//请求限制控制
	public static final String REQUEST_LIMIT = "request_limit_";

	//动态列表
	public static final String DYNAMIC_LIST = "dynamic_list";

	//置顶帖子
    public static final String TOP_POST_LIST = "top_post";

	//周热门帖子
    public static final String HOT_POST_LIST = "hot_post";

    //邮件激活
    public static final String EMAIL_ACTIVATION = "email_activation_";
    
    //重置密码邮件
    public static final String FORGET_PASS = "forget_pass_";
    
    //管理员会话
    public static final String SESSION_MANAGER = "session_manager_";
    
    //友情连接缓存
    public static final String FRIEND_LINK = "friend_link";
}
