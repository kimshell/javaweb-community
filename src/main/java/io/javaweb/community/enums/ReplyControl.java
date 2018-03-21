package io.javaweb.community.enums;
/**
 * 
 * @author KevinBlandy
 *
 */
public enum ReplyControl {
	
	//允许任何正常回复
	ALLOW,
	
	//不允许任何回复(管理员设置)
	DISALLOW,
	
	//禁止作者以外的人回复
	DISALLOW_OTHER,
	
	//禁止匿名回复
	DISALLOW_ANONYMOUS
}
