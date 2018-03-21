package io.javaweb.community.web.servlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import io.javaweb.community.utils.JsoupUtils;

/**
 * 
 * xss过滤器
 * @author KevinBlandy
 *
 */
public class XssHttpServletRequest extends HttpServletRequestWrapper {

	public XssHttpServletRequest(HttpServletRequest request) {
		super(request);
	}
	
	@Override
	public String getParameter(String name) {
		return JsoupUtils.cleanXss(super.getParameter(name));
	}
	
	@Override
	public String[] getParameterValues(String name) {
		return this.clearXss(super.getParameterValues(name));
	}
	
	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> parameterMap = super.getParameterMap();
		for(String key : parameterMap.keySet()) {
			parameterMap.put(key, this.clearXss(parameterMap.get(key)));
		}
		return parameterMap;
	}
	
	private String[] clearXss(String[] source) {
		if(source != null) {
			for(int x = 0;x < source.length ; x++) {
				source[x] = JsoupUtils.cleanXss(source[x]);
			}
		}
		return source;
	}
}
