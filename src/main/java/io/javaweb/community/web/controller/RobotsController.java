package io.javaweb.community.web.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.javaweb.community.annotation.IgnoreSession;
import io.javaweb.community.common.BaseController;

@Controller
@RequestMapping("/robots.txt")
public class RobotsController extends BaseController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RobotsController.class);
	
	private static String robotsText = "";
	
	@GetMapping
	@IgnoreSession
	public void robots(HttpServletRequest request,
							HttpServletResponse response) throws Exception{
//		if(GeneralUtils.isEmpty(robotsText)) {
//			throw new ServiceException(Status.BAD_URL);
//		}
		response.setContentType(MediaType.TEXT_PLAIN_VALUE);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(robotsText);
		response.flushBuffer();
	}
	
	static {
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(RobotsController.class.getResourceAsStream("/robots.txt"),StandardCharsets.UTF_8))){
			LOGGER.info("加载robots.txt");
			String line = null;
			while((line = bufferedReader.readLine()) != null) {
				robotsText += line;
				robotsText += System.getProperty("line.separator");
			}
		}catch(Exception e) {
			e.printStackTrace();
			LOGGER.error("加载robots.txt文件异常");
		}
	}
}
