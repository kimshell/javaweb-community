package io.javaweb.community.web.support;

import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author KevinBlandy
 *
 */
public class Views {
	
	public static final ModelAndView LOGIN_VIEW = new ModelAndView("/login/login");
	
	public static final ModelAndView ARTICLE_CREATE_VIEW = new ModelAndView("/post/create");
}
