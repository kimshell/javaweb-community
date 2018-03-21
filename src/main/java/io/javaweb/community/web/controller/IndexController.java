package io.javaweb.community.web.controller;

import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.javaweb.community.annotation.IgnoreSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import io.javaweb.community.common.BaseController;
import io.javaweb.community.entity.dto.PostDTO;
import io.javaweb.community.enums.PostType;
import io.javaweb.community.enums.Status;
import io.javaweb.community.service.PostService;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.utils.PageUtils;
/**
 * 
 * @author Kevin
 *
 */
@Controller
@RequestMapping
public class IndexController extends BaseController{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

	private static final ModelAndView FORWARD_MODEL_AND_VIEW = new ModelAndView("forward:index");
	
	@Autowired
	private PostService postService;
	
	/**
	 * index
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/index")
	public ModelAndView index(HttpServletRequest request,
								PostDTO postDTO,
								@RequestParam(value = "page",defaultValue = "1")Integer page,
								@RequestParam(value = "rows",defaultValue = "20")Integer rows,
								//@RequestParam(value = "count",defaultValue = "true")Boolean count,
								@RequestParam(value = "sort",defaultValue = "createDate") String[] sorts,
					            @RequestParam(value = "order",defaultValue = "desc")String[] orders) throws Exception {
		
		postDTO.setStatus(Status.NORMAL);
		
		Object postTypeParam = request.getAttribute("postType");
		
		if(postTypeParam != null) {
			postDTO.setType(PostType.valueOf(((String)postTypeParam).toUpperCase()));
		}
		
		if(!GeneralUtils.isEmpty(postDTO.getCreateUser())) {
			//检索指定用户的记录时,不检索匿名主题
			postDTO.setAnonymous(Boolean.FALSE);
		}
		
		LOGGER.debug("加载帖子信息:{},page={},rows={},sort={},order={}",postDTO.getType(),page,rows,Arrays.asList(sorts),Arrays.asList(orders));
		
		ModelAndView modelAndView = new ModelAndView("/index/index");
		
		Map<String,Object> results = this.postService.queryIndexPosts(postDTO,PageUtils.getPageBounds(page, rows,PageUtils.getOrders(sorts, orders)));
		
		modelAndView.addAllObjects(results);
		
		modelAndView.addObject("postType",postTypeParam == null ? "index" : postTypeParam);
		
		modelAndView.addObject("param", postDTO);
		
		return modelAndView;
	}
	
	
	//根据帖子类型加载帖子信息
	@GetMapping("/{postType}")
	@IgnoreSession
	public ModelAndView post(HttpServletRequest request,
					@PathVariable("postType")String postType)throws Exception {
		try {
			PostType.valueOf(postType.toUpperCase());
		}catch (Exception e) {
			//帖子类型不存在,抛出404
			throw new NoHandlerFoundException(request.getMethod(),request.getRequestURL().toString(),null);
		}
		request.setAttribute("postType", postType);
		return FORWARD_MODEL_AND_VIEW; 
	}
	
	/**
	 * 默认转发页面
	 * @param request
	 * @return
	 */
	@GetMapping("/")
	@IgnoreSession
	public ModelAndView defaultPage(HttpServletRequest request) {
		return FORWARD_MODEL_AND_VIEW;
	}
}
