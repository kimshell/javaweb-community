package io.javaweb.community.test;


import java.io.IOException;
import java.util.regex.Pattern;

import io.javaweb.community.utils.GeneralUtils;

public class Test {

    public static final Pattern MENTION_RESTORE = Pattern.compile("<a target=\"_blank\" href=\"http://www.javaweb.io/user/[A-F0-9]{32}\" id=\"at-[A-F0-9]{32}\">[0-9a-zA-Z_\\u4e00-\\u9fa5]{1,14}</a>");
	

	public static void main(String[] args) throws IOException {
		System.out.println(GeneralUtils.getUUID());
		System.out.println(GeneralUtils.getUUID());
		System.out.println(GeneralUtils.getUUID());
	}
	public static void test(String content){
		
    }
}
