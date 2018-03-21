package io.javaweb.community.web.controller;


import io.javaweb.community.annotation.IgnoreSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.javaweb.community.common.BaseController;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/test")
public class TestController extends BaseController {

    @GetMapping
    @IgnoreSession
	public ModelAndView test() throws Exception {
    	ModelAndView modelAndView =  new ModelAndView("/test/test");
    	return modelAndView;
	}
}
