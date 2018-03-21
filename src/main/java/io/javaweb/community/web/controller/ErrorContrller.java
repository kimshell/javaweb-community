package io.javaweb.community.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import io.javaweb.community.annotation.IgnoreSession;
import io.javaweb.community.common.BaseController;
import io.javaweb.community.common.Messages;

@Controller
@RequestMapping("/error")
public class ErrorContrller extends BaseController implements org.springframework.boot.autoconfigure.web.ErrorController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorContrller.class);

	private static final String ERROR_PATH = "/error";
	
	
	@RequestMapping
	@IgnoreSession
	public ModelAndView errorPage(HttpServletRequest request,
			  						HttpServletResponse response) throws Exception{
		
		String path = (String) request.getAttribute("javax.servlet.error.request_uri");         //发生了异常的请求地址
		
		Class<?> exceptionType = (Class<?>) request.getAttribute("javax.servlet.error.exception_type");   //异常类型
		
		Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");    //异常类,只有在 500 异常的清空下,该值不为空
		
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");     //HTTP异常状态码
		
		//String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");     //异常的Servlet
		
		LOGGER.error("服务器异常, path={}, type={}, message={}, code={}",path,exceptionType,exception,statusCode);
		
		ModelAndView modelAndView = new ModelAndView("error/error");
		
		if(statusCode == 404) {
			modelAndView.addObject("message", Messages.NOT_FOUND);
		}else {
			modelAndView.addObject("message", Messages.SERVER_ERROR);
		}
		
		return modelAndView;
	}
	
	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}
	
}
