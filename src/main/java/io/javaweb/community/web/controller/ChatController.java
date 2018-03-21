package io.javaweb.community.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import io.javaweb.community.annotation.IgnoreSession;
import io.javaweb.community.common.BaseController;
import io.javaweb.community.constants.CookieKeys;
import io.javaweb.community.utils.GeneralUtils;

@RequestMapping("/chat")
@Controller
public class ChatController extends BaseController {
	
	@GetMapping
	@IgnoreSession
	public ModelAndView chatPage(@CookieValue(value = CookieKeys.USER_SESSION,required = false) String token) throws Exception{
		ModelAndView modelAndView = new ModelAndView("/chat/chat");
		//modelAndView.addObject("channels", ChatEndpoint.SESSIONS);
		if(!GeneralUtils.isEmpty(token)) {
			modelAndView.addObject("token", token);
		}
		return modelAndView;
	}
}
