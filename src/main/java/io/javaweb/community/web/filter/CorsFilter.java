package io.javaweb.community.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
/**
 * 
 * cors
 * @author KevinBlandy
 *
 */
@WebFilter(urlPatterns = {"/verifyCode","/manager/*"},filterName = "corsFilter")
public class CorsFilter implements Filter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CorsFilter.class);
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)throws IOException, ServletException {
		
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		
		String origin = httpServletRequest.getHeader(HttpHeaders.ORIGIN);
		if(origin != null) {
			LOGGER.debug("跨域头设置...");
			httpServletResponse.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
			httpServletResponse.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS");
			httpServletResponse.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,"Origin, Accept-Charset, Accept, Accept-Encoding, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With");
			httpServletResponse.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
			httpServletResponse.addHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "1800");
		}
		chain.doFilter(httpServletRequest, httpServletResponse);
	}

	@Override
	public void destroy() {
		
	}
}
