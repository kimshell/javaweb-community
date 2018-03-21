package io.javaweb.community.web.advice;


import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import io.javaweb.community.common.BaseController;
import io.javaweb.community.common.Message;
import io.javaweb.community.common.Messages;
import io.javaweb.community.exception.ServiceException;
import io.javaweb.community.utils.WebUtils;



/**
 * 所有异常响应 ResponseCode 均为200(@ResponseStatus(HttpStatus.OK)),防止部分路由器(小米路由器)或者浏览器的劫持
 * @author KevinBlandy
 */
@ControllerAdvice
@ResponseStatus(HttpStatus.OK)
//@ResponseBody
public class ExceptionAdvice extends BaseController {
	
	public static final String ERROR_VIEW = "/error/error";
	
	/**
	 * 异常处理
	 * @param request
	 * @param response
	 * @param message
	 * @return
	 * @throws IOException
	 */
	public ModelAndView errorHandle(HttpServletRequest request,HttpServletResponse response,Message<Void> message) throws IOException {
		if(WebUtils.isAjaxRequest(request)) {
			//ajax请求响应json,不渲染页面
			WebUtils.responseToJson(message, response);
			return null;
		}
		ModelAndView modelAndView = new ModelAndView(ERROR_VIEW);
		modelAndView.addObject("message", message);
		return modelAndView;
	}
	
	/**
	 * 缺少必须参数
	 * @param request
	 * @param response
	 * @param e
	 * @return
	 * @throws IOException 
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)  
	public ModelAndView missingServletRequestParameterException(HttpServletRequest request,HttpServletResponse response,MissingServletRequestParameterException e) throws IOException {
		return errorHandle(request,response,Messages.MISSING_REQUEST_PARAM);
	}  
	
	/**
	 * 参数解析失败
	 * @param request
	 * @param response
	 * @param e
	 * @return
	 * @throws IOException 
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)  
	public ModelAndView  httpMessageNotReadableException(HttpServletRequest request,HttpServletResponse response,HttpMessageNotReadableException e) throws IOException {
		return errorHandle(request,response,Messages.BAD_REQUEST_BODY);
	}  
	
	/**
	 * 参数校验失败
	 * @param request
	 * @param response
	 * @param e
	 * @return
	 * @throws IOException
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ModelAndView  illegalArgumentException(HttpServletRequest request,HttpServletResponse response,IllegalArgumentException e) throws IOException {
		e.printStackTrace();
		return errorHandle(request,response,Messages.BAD_PARAM);
	} 
	
	
	/**
	 * 参数校验失败
	 * @param request
	 * @param response
	 * @param e
	 * @return
	 * @throws IOException 
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)  
	public ModelAndView  methodArgumentTypeMismatchException(HttpServletRequest request,HttpServletResponse response,MethodArgumentTypeMismatchException e) throws IOException {
		return errorHandle(request,response,Messages.BAD_PARAM);
	}  
	
	/**
	 * 参数校验失败
	 * @param request
	 * @param response
	 * @param e
	 * @return
	 * @throws IOException 
	 */
	@ExceptionHandler(BindException.class)  
	public ModelAndView  bindException(HttpServletRequest request,HttpServletResponse response,BindException e) throws IOException {
		return errorHandle(request,response,Messages.BAD_PARAM);
	}  
	
	
	
	/**
	 * 请求方式不支持
	 * @param request
	 * @param response
	 * @param e
	 * @return
	 * @throws IOException 
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)  
	public ModelAndView httpRequestMethodNotSupportedException(HttpServletRequest request,HttpServletResponse response,HttpRequestMethodNotSupportedException e) throws IOException {
		return errorHandle(request,response,Messages.REQUEST_METHOD_NOT_SUPPORT);
	}  

	/**
	 * 不支持的媒体类型
	 * @param request
	 * @param response
	 * @param e
	 * @return
	 * @throws IOException 
	 */
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ModelAndView httpMediaTypeNotSupportedException(HttpServletRequest request,HttpServletResponse response,HttpMediaTypeNotSupportedException e) throws IOException {
		return errorHandle(request,response,Messages.MEDIA_TYPE_NOT_SUPPORTED);
	}  
	

	/**
	 * 路径未找到
	 * @param request
	 * @param response
	 * @param e
	 * @return
	 * @throws IOException
	 */
	@ExceptionHandler(NoHandlerFoundException.class)  
	public ModelAndView noHandlerFoundException(HttpServletRequest request,HttpServletResponse response,NoHandlerFoundException e) throws IOException {
		return errorHandle(request,response,Messages.BAD_URL);
	}  
	
	/**
	 * 业务异常
	 * @param request
	 * @param response
	 * @param e
	 * @return
	 * @throws IOException 
	 */
	@ExceptionHandler(ServiceException.class)  
	public ModelAndView serviceException(HttpServletRequest request,HttpServletResponse response,ServiceException e) throws IOException {
		return errorHandle(request,response,e.getErrorMessage());
	}  
	
  
	/**
	 * 服务器异常
	 * @param request
	 * @param response
	 * @param e
	 * @return
	 * @throws IOException 
	 */
	@ExceptionHandler(Exception.class)  
	public ModelAndView  handleException(HttpServletRequest request,HttpServletResponse response,Exception e) throws IOException {
		e.printStackTrace();
		if(e instanceof ServiceException) {
			return errorHandle(request,response,((ServiceException)e).getErrorMessage());
		}
		return errorHandle(request,response,Messages.SERVER_ERROR);
	}  
}
