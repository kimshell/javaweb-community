package io.javaweb.community.web.controller;

import io.javaweb.community.enums.PostType;
import io.javaweb.community.enums.ReplyControl;
import io.javaweb.community.enums.Role;
import io.javaweb.community.exception.ServiceException;
import io.javaweb.community.service.MessageService;
import io.javaweb.community.service.PostReplyService;
import io.javaweb.community.service.PostService;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.utils.JsoupUtils;
import io.javaweb.community.utils.MentionUtils;
import io.javaweb.community.utils.PageUtils;
import io.javaweb.community.utils.SimpleHttpUtils;
import io.javaweb.community.web.socket.endpoint.NowEndpoint;
import io.javaweb.community.web.socket.model.SocketMessage;
import io.javaweb.community.web.support.PageInfo;
import io.javaweb.community.web.support.SessionHolder;
import io.javaweb.community.web.support.UserSession;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;

import io.javaweb.community.annotation.IgnoreSession;
import io.javaweb.community.annotation.VerifyCode;
import io.javaweb.community.common.BaseController;
import io.javaweb.community.common.Message;
import io.javaweb.community.common.Message.Status;
import io.javaweb.community.common.Messages;
import io.javaweb.community.entity.MessageEntity;
import io.javaweb.community.entity.PostEntity;
import io.javaweb.community.entity.PostReplyEntity;
import io.javaweb.community.entity.dto.PostDTO;
import io.javaweb.community.web.support.Views;

/**
 * 
 * @author KevinBlandy
 *
 */
@Controller
@RequestMapping("/post")
public class PostController extends BaseController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);
	
	private static final Integer MAX_POST_CONTENT_LENGTH = 100000 * 5;		//50w
	
//	@Autowired
//	private Executor executor;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private PostReplyService postReplyService;
	
	@Value("${now.template.post}")
	private String nowTemplatePost;
	
	@Value("${now.template.reading}")
	private String nowTempateReding;
	
	@Value("${now.template.reply}")
	private String nowTemplateReply;
	
	@Autowired
	private ExecutorService executorService;
	
	@Autowired
	private MessageService messsageService;
	
	
	@Value("${baidu.link.submit.create}")
	private String baiduLinkSubmitCreate;
	
	@Value("${baidu.link.submit.update}")
	private String baiduLinkSubmitUpdate;
	
//	@Value("${baidu.link.submit.create}")
//	private String baiduLinkSubmitCreate;
	
	
	//检索主题
	@GetMapping("/{postId}")
	@IgnoreSession
	public ModelAndView postDetailPage(@PathVariable("postId")String postId,
									@RequestParam(value = "page",defaultValue = "1")Integer page,
									//@RequestParam(value = "rows",defaultValue = "20")Integer rows,
									//@RequestParam(value = "count",defaultValue = "true")Boolean count,
									@RequestParam(value = "broadCast",defaultValue = "true")Boolean broadCast,
									@RequestParam(value = "sort",defaultValue = "createDate") String[] sorts,
									@RequestParam(value = "order",defaultValue = "asc")String[] orders,
									@RequestParam(value = "messageId",required = false)String messageId)throws Exception{

        LOGGER.debug("查看文章详情:postId={},messageId={}",postId,messageId);

		PostDTO postDTO = new PostDTO();

        UserSession userSession = SessionHolder.USER_SESSION.get();
        if(userSession != null){
            if(!GeneralUtils.isEmpty(messageId)){
            	/**
            	 * 从通知消息过来,设置消息为已读 
            	 */
            	MessageEntity messageEntity = new MessageEntity();
            	messageEntity.setMessageId(messageId);
            	/**
            	 * 没有校验当前消息,是不是当前登录用户的 
            	 */
            	//messageEntity.setCreateUser(userSession.getUser().getUserId());		
            	messageEntity.setIsRead(true);
            	this.messsageService.updateByPrimaryKeySelective(messageEntity);
            }
            postDTO.setSessionUser(userSession.getUser());
        }

        postDTO.setPostId(postId);

        postDTO = this.postService.queryPostDetailByPostId(postDTO,PageUtils.getPageBounds(page, 20, PageUtils.getOrders(sorts, orders)));
        
	    ModelAndView modelAndView = new ModelAndView("/post/detail");
	    
	    //文章正文
	    modelAndView.addObject("post", postDTO);
	    //回复
	    modelAndView.addObject("replys", postDTO.getPostReplys());
	    //回复分页信息
	    modelAndView.addObject("pageInfo", new PageInfo(postDTO.getPostReplys().getPaginator()));
	    
	    //阅读广播 可以考虑异步 TODO
	    if(SessionHolder.USER_SESSION.get() != null && broadCast && SessionHolder.USER_SESSION.get().getUser().getBrowseRadio()) {
	    	SocketMessage message = new SocketMessage(SocketMessage.Type.READING, MessageFormat.format(this.nowTempateReding,
	    					SessionHolder.USER_SESSION.get().getUser().getUserId(),
							SessionHolder.USER_SESSION.get().getUser().getPortrait(),
							SessionHolder.USER_SESSION.get().getUser().getName(),
							postId,
							postDTO.getTitle()));
			NowEndpoint.broadCast(message);
	    }
	    
	    //刷新阅读量 可以考虑异步 TODO
	    if(postDTO.getBrowse() < 99999999) {
	    	this.postService.incrementPostBrowse(postId);
	    }
		return modelAndView;
	}
	
	@GetMapping("/create")
	public ModelAndView createPage() {
		return Views.ARTICLE_CREATE_VIEW;
	}
	
	//发布帖子
	@PostMapping("/create")
	@ResponseBody
	@VerifyCode(limit = 1000 * 30, max = 10,name = "post_create")		
	public Message<String> create(@RequestParam("title")String title,
                                  @RequestParam("postType")PostType postType,
                                  @RequestParam("replyControl")ReplyControl replyControl,
                                  @RequestParam("content")String content,
                                  @RequestParam(value = "anonymous",defaultValue = "false")Boolean anonymous)throws Exception{
		LOGGER.debug("新的文章:titlle			={}",title);
		LOGGER.debug("新的文章:postType		={}",postType);
		LOGGER.debug("新的文章:replyControl	={}",replyControl);
		LOGGER.debug("新的文章:content			={}",content);
		LOGGER.debug("新的文章:anonymous		={}",anonymous);
		LOGGER.debug("新的文章:user			={}",SessionHolder.USER_SESSION.get().getUser());
		
		//xss过滤
		title = JsoupUtils.cleanHTML(title);
		content = JsoupUtils.cleanXss(content);
		
		//标题校验
		if(GeneralUtils.isEmpty(title.trim())) {
			throw new ServiceException("标题不能为空", Status.BAD_PARAM);
		}
		if(title.length() > 50) {
			throw new ServiceException("标题最多50字符", Status.BAD_PARAM);
		}
		
		//内容校验
		if(GeneralUtils.isEmpty(content.trim())) {
			throw new ServiceException("正文不能为空", Status.BAD_PARAM);
		}
		if(content.length() > MAX_POST_CONTENT_LENGTH) {
			throw new ServiceException("正文长度超出限制", Status.BAD_PARAM);
		}
		
		PostEntity postEntity = new PostEntity();
		postEntity.setTitle(title);
		postEntity.setContent(content);
		postEntity.setAnonymous(anonymous);
		postEntity.setReplyControl(replyControl);
		postEntity.setStatus(io.javaweb.community.enums.Status.NORMAL);
		if(anonymous) {
			//匿名贴,必须允许评论
			postEntity.setReplyControl(ReplyControl.ALLOW);
		}
		if(postType.equals(PostType.NOTICE)){
			if(SessionHolder.USER_SESSION.get().getUser().getRole().equals(Role.USER)){
				throw new ServiceException("无权发布系统通知");
			}
			if(anonymous) {
				throw new ServiceException("不能匿名发布系统通知");
			}
		}
		postEntity.setType(postType);
		postEntity.setEssence(Boolean.FALSE);
		postEntity.setTop(Boolean.FALSE);
		postEntity.setCreateDate(new Date());
		postEntity.setBrowse(0);
		postEntity.setCreateUser(SessionHolder.USER_SESSION.get().getUser().getUserId());
		
		postEntity.setPostId(GeneralUtils.getUUID());
		
		this.postService.createPost(postEntity);
		
		//消息广播
		if(!anonymous) {
			NowEndpoint.broadCast(new SocketMessage(SocketMessage.Type.POST,MessageFormat.format(this.nowTemplatePost, 
					SessionHolder.USER_SESSION.get().getUser().getUserId(),
					SessionHolder.USER_SESSION.get().getUser().getPortrait(),
					SessionHolder.USER_SESSION.get().getUser().getName(),
					postEntity.getPostId(),
					postEntity.getTitle())));
		}
		
		//异步推送到百度连接
		this.executorService.submit(() -> {
			try {
				LOGGER.info("百度链接提交-create");
				String response = SimpleHttpUtils.post(this.baiduLinkSubmitCreate, "http://www.javaweb.io/post/" + postEntity.getPostId(), SimpleHttpUtils.TEXT_PLAIN);
				LOGGER.info("百度链接提交-create,结果:{}",response);
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.error("百度链接提交异常:{}",e);
			}
		});
		
		return super.getSuccessMessage(postEntity.getPostId());
	}
	
	//修改帖子内容
	@GetMapping("/update/{postId}")
	public ModelAndView updatePage(@PathVariable("postId")String postId) throws Exception{
		ModelAndView modelAndView = new ModelAndView("/post/update");
		
		PostEntity postEntity = new PostEntity();
		postEntity.setPostId(postId);
		postEntity.setStatus(io.javaweb.community.enums.Status.NORMAL);
		postEntity.setCreateUser(SessionHolder.USER_SESSION.get().getUser().getUserId());
		
		postEntity = this.postService.queryByParamSelectiveSole(postEntity);
		
		if(postEntity == null) {
			throw new ServiceException(Status.NOT_FOUND);
		}

		postEntity.setContent(MentionUtils.mentionRestore(postEntity.getContent()));
		
		modelAndView.addObject("post", postEntity);
		
		return modelAndView;
	}
	
	//更新帖子信息
	@PostMapping("/update")
	@ResponseBody
	@VerifyCode(limit = 1000 * 30, max = 10,name = "post_update")
	public Message<Void> update(@RequestParam("postId")String postId,
								@RequestParam("title")String title,
								@RequestParam("postType")PostType postType,
								@RequestParam("replyControl")ReplyControl replyControl,
								@RequestParam("content")String content)throws Exception{
		LOGGER.debug("修改文章:postId			={}",postId);
		LOGGER.debug("修改文章:titlle			={}",title);
		LOGGER.debug("修改文章:postType		={}",postType);
		LOGGER.debug("修改文章:replyControl	={}",replyControl);
		LOGGER.debug("修改文章:content			={}",content);
		LOGGER.debug("修改文章:user			={}",SessionHolder.USER_SESSION.get().getUser());
		
		//xss过滤
		title = JsoupUtils.cleanHTML(title);
		content = JsoupUtils.cleanXss(content);
		
		//标题校验
		if(GeneralUtils.isEmpty(title.trim())) {
			throw new ServiceException("标题不能为空", Status.BAD_PARAM);
		}
		if(title.length() > 50) {
			throw new ServiceException("标题最多50字符", Status.BAD_PARAM);
		}
		
		//内容校验
		if(GeneralUtils.isEmpty(content.trim())) {
			throw new ServiceException("正文不能为空", Status.BAD_PARAM);
		}
		if(content.length() > MAX_POST_CONTENT_LENGTH) {
			throw new ServiceException("正文长度超出限制", Status.BAD_PARAM);
		}
		
		PostEntity postEntity = new PostEntity();
		postEntity.setPostId(postId);
		postEntity.setTitle(title);
		postEntity.setContent(content);
		postEntity.setReplyControl(replyControl);
		postEntity.setType(postType);
		
		if(postType.equals(PostType.NOTICE)){
			if(SessionHolder.USER_SESSION.get().getUser().getRole().equals(Role.USER)){
				throw new ServiceException("无权发布系统通知");
			}
		}

		this.postService.updatePost(postEntity);
		
		//异步推送到百度连接
		this.executorService.submit(() -> {
			try {
				LOGGER.info("百度链接提交-update");
				String response = SimpleHttpUtils.post(this.baiduLinkSubmitUpdate, "http://www.javaweb.io/post/" + postEntity.getPostId(), SimpleHttpUtils.TEXT_PLAIN);
				LOGGER.info("百度链接提交-update,结果:{}",response);
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.error("百度链接提交异常:{}",e);
			}
		});
		
		return Messages.SUCCESS;
	}
	
	//删除文章
	@PostMapping("/delete")
	@ResponseBody
	public Message<Void> delete(@RequestParam("postId")String[] postIds)throws Exception{
		this.postService.batchLogicDeleteByPostId(postIds);
		return Messages.SUCCESS;
	}

	
	/***	帖子回复		***/
	
	//发表回复
	@PostMapping("/reply")
	@ResponseBody
	@VerifyCode(limit = 1000 * 10, max = 10,name = "post_reply")        //1h最多回复10次,两次回复间隔不超过10s免验证码
	public Message<JSONObject> reply(HttpServletRequest request,
							@RequestParam("postId")String postId,
							@RequestParam("content")String content,
							@RequestParam(value = "parentId",defaultValue = PostReplyEntity.DEFAULT_PARENT_ID)String parentId,
							@RequestParam(value = "anonymous",defaultValue = "false")Boolean anonymous)throws Exception{
		
		LOGGER.debug("新的回复:replyId={},parentId={},anonymous={},content={}",postId,parentId,anonymous,content);
		
		content = JsoupUtils.cleanXss(content);
		
		if(GeneralUtils.isEmpty(content)) {
			throw new ServiceException("回复信息不能为空");
		}
		if(content.length() > 10000 * 5) {
			throw new ServiceException("回复内容长度超出限制");
		}
		
		PostReplyEntity postReplyEntity = new PostReplyEntity();
		
		//帖子id
		postReplyEntity.setPostId(postId);
		//正文
		postReplyEntity.setContent(content);
		//是否匿名
		postReplyEntity.setAnonymous(anonymous);
		//父级评论id
		postReplyEntity.setParentId(GeneralUtils.isEmpty(parentId) ? PostReplyEntity.DEFAULT_PARENT_ID : parentId);
		
		//回复id
		postReplyEntity.setReplyId(GeneralUtils.getUUID());
		//发布时间
		postReplyEntity.setCreateDate(new Date());
		//发布人
		postReplyEntity.setCreateUser(SessionHolder.USER_SESSION.get().getUser().getUserId());
		//非一级回复
		postReplyEntity.setIsParent(Boolean.FALSE);
		postReplyEntity.setStatus(io.javaweb.community.enums.Status.NORMAL);
		
		Map<String,Object> map = this.postReplyService.createReply(postReplyEntity);
		
		Integer page = (Integer) map.get("page");
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("replyId", postReplyEntity.getReplyId());
		jsonObject.put("postId", postReplyEntity.getPostId());
		jsonObject.put("page", page);
		
		if(!anonymous && SessionHolder.USER_SESSION.get().getUser().getReplyRadio()) {
			//消息广播
			NowEndpoint.broadCast(new SocketMessage(SocketMessage.Type.REPLY,MessageFormat.format(this.nowTemplateReply, 
						SessionHolder.USER_SESSION.get().getUser().getUserId(),
						SessionHolder.USER_SESSION.get().getUser().getPortrait(),
						SessionHolder.USER_SESSION.get().getUser().getName(),
						
						postReplyEntity.getPostId(),
						page + "",
						postReplyEntity.getReplyId(),
						((PostEntity)map.get("post")).getTitle())));
		}
		
		return super.getSuccessMessage(jsonObject);
	}
	
	//读取回复
	@GetMapping("/reply")
	@ResponseBody
	public Message<Void> getReply(@RequestParam("postId")String postId,
								@RequestParam(value = "parentId",defaultValue = PostReplyEntity.DEFAULT_PARENT_ID)String parentId,
								@RequestParam(value = "page",defaultValue = "1")Integer page,
								@RequestParam(value = "rows",defaultValue = "20")Integer rows,
								@RequestParam(value = "count",defaultValue = "true")Boolean count,
								@RequestParam(value = "sort",defaultValue = "createDate") String[] sorts,
					            @RequestParam(value = "order",defaultValue = "asc")String[] orders)throws Exception {
		
		return Messages.SUCCESS;
	}
	
	//删除回复
	@PostMapping("/reply/delete")
	@ResponseBody
	public Message<Void> deleteReply(@RequestParam("replyId")String[] replyId)throws Exception{
		return Messages.SUCCESS;
	}
}
