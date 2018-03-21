package io.javaweb.community.beetl.funcs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.beetl.core.Context;
import org.beetl.core.Function;

public class PostDateFunction implements Function {
	
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public Object call(Object[] paras, Context ctx) {
//		if(paras == null || paras[0] == null) {
//			return null;
//		}
		return this.getTimeInfo(((Date)paras[0]));
	}
	
	private String getTimeInfo(Date date) {
		long postTime = ((System.currentTimeMillis() - date.getTime()) / 1000L);
		if(postTime <  60) {
			return "刚刚";
		} else if(postTime >= 60 && postTime < 3600) {
			return postTime / 60 + "分钟前";
        } else if(postTime >= 3600 && postTime < 86400) {
        	return postTime / 3600 + "小时前";
        } else if(postTime >= 86400 && postTime < 864000) {	
        	return postTime / 86400 + "天前";
        } else{
        	return FORMAT.format(date);
        }
	}
}
