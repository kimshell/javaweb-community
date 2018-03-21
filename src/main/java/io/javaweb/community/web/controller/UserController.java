package io.javaweb.community.web.controller;

import io.javaweb.community.annotation.IgnoreAccountStatus;
import io.javaweb.community.annotation.IgnoreEmailVerifi;
import io.javaweb.community.annotation.IgnoreSession;
import io.javaweb.community.annotation.VerifyCode;
import io.javaweb.community.common.BaseController;
import io.javaweb.community.common.Message;
import io.javaweb.community.common.Messages;
import io.javaweb.community.entity.PostEntity;
import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.entity.dto.CollectionDTO;
import io.javaweb.community.entity.dto.MessageDTO;
import io.javaweb.community.entity.dto.UserDTO;
import io.javaweb.community.enums.Status;
import io.javaweb.community.mybatis.domain.PageList;
import io.javaweb.community.service.CollectionService;
import io.javaweb.community.service.MessageService;
import io.javaweb.community.service.PostService;
import io.javaweb.community.service.UserService;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.utils.JsoupUtils;
import io.javaweb.community.utils.PageUtils;
import io.javaweb.community.utils.RegUtils;
import io.javaweb.community.web.support.PageInfo;
import io.javaweb.community.web.support.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by KevinBlandy on 2018/1/19 14:50
 */

@Controller
@RequestMapping("/user")
public class UserController extends BaseController{
	
	@Autowired
	private UserService userService;

	@Autowired
    private CollectionService collectionService;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private MessageService messageService;
	
//	@RequestMapping("/query")
//	@ResponseBody
//	@IgnoreSession
//	public Message<List<UserDTO>> queryEntity(@RequestParam("name")String name)throws Exception{
//		return super.getSuccessMessage(this.userService.queryByLikeName(name));
//	}

	//用户主页
	@GetMapping("/{userId}")
	@IgnoreSession
	public ModelAndView mainPage(@PathVariable(value = "userId",required=  true)String userId)throws Exception {
		ModelAndView modelAndView = new ModelAndView("/user/main");
		UserDTO userDTO = this.userService.queryUserInfoByUserId(userId);
		modelAndView.addObject("user", userDTO);
		return modelAndView;
	}

	//设置页面
    @GetMapping("/settings")
    @IgnoreAccountStatus
    @IgnoreEmailVerifi
    public ModelAndView settingsPage(){
        ModelAndView modelAndView = new ModelAndView("/user/settings");
        modelAndView.addObject("user", SessionHolder.USER_SESSION.get().getUser());
        return modelAndView;
    }

    //我的收藏页面
    @GetMapping("/collections")
    @IgnoreAccountStatus
    @IgnoreEmailVerifi
    public ModelAndView collectionsPage(HttpServletRequest request,
                                        CollectionDTO collectionDTO,
                                        @RequestParam(value = "page",defaultValue = "1")Integer page,
                                        @RequestParam(value = "rows",defaultValue = "20")Integer rows,
                                        @RequestParam(value = "sort",defaultValue = "createDate") String[] sorts,
                                        @RequestParam(value = "order",defaultValue = "desc")String[] orders) throws Exception {

        collectionDTO.setCreateUser(SessionHolder.USER_SESSION.get().getUser().getUserId());

        PageList<CollectionDTO> collections = this.collectionService.queryCollectionsByCreateUser(collectionDTO, PageUtils.getPageBounds(page,rows,PageUtils.getOrders(sorts,orders)));

        ModelAndView modelAndView = new ModelAndView("/user/collections");
        modelAndView.addObject("collections",collections);
        modelAndView.addObject("pageInfo",new PageInfo(collections.getPaginator()));

        return modelAndView;
    }
    
    //我的帖子页面
    @GetMapping("/post")
    @IgnoreAccountStatus
    @IgnoreEmailVerifi
    public ModelAndView postPage(HttpServletRequest request,
    								PostEntity postEntity,
    								@RequestParam(value = "page",defaultValue = "1")Integer page,
                                    @RequestParam(value = "rows",defaultValue = "20")Integer rows,
                                    @RequestParam(value = "sort",defaultValue = "createDate") String[] sorts,
                                    @RequestParam(value = "order",defaultValue = "desc")String[] orders)throws Exception{
        ModelAndView modelAndView = new ModelAndView("/user/post");
        
        postEntity.setStatus(Status.NORMAL);
        postEntity.setCreateUser(SessionHolder.USER_SESSION.get().getUser().getUserId());
        
        PageList<PostEntity> posts = this.postService.queryByParamSelective(postEntity, PageUtils.getPageBounds(page, rows, PageUtils.getOrders(sorts, orders)));
        
        modelAndView.addObject("posts", posts);
        modelAndView.addObject("pageInfo", new PageInfo(posts.getPaginator()));
        
        return modelAndView;
    }
    
    //消息通知页面
    @GetMapping("/message")
    @IgnoreAccountStatus
    @IgnoreEmailVerifi
    public ModelAndView messagePage(MessageDTO messageDTO,
					    		@RequestParam(value = "page",defaultValue = "1")Integer page,
					            @RequestParam(value = "rows",defaultValue = "20")Integer rows,
					            @RequestParam(value = "sort",defaultValue = "isRead,createDate") String[] sorts,
					            @RequestParam(value = "order",defaultValue = "asc,desc")String[] orders)throws Exception{
    	messageDTO.setUserId(SessionHolder.USER_SESSION.get().getUser().getUserId());
    	PageList<MessageDTO> messages = this.messageService.queryMessagesByUserId(messageDTO, PageUtils.getPageBounds(page, rows, PageUtils.getOrders(sorts, orders)));
        ModelAndView modelAndView = new ModelAndView("/user/message");
        modelAndView.addObject("messages", messages);
        modelAndView.addObject("pageInfo", new PageInfo(messages.getPaginator()));
        return modelAndView;
    }
    
    
    /************************/
    
    //修改登录密码
    @PostMapping("/updatePass")
    @ResponseBody
    @IgnoreAccountStatus
    @IgnoreEmailVerifi
    @VerifyCode(limit = 1000 * 30, max = 2,name = "pass_update")
    public Message<Void> updatePass(@RequestParam("oldPass")String oldPass,
    								@RequestParam("newPass")String newPass)throws Exception{
    	UserEntity userEntity = this.userService.queryByPrimaryKey(SessionHolder.USER_SESSION.get().getUser().getUserId());
    	if(!userEntity.getPass().equals(DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(oldPass.getBytes()).getBytes()))) {
    		return super.getErrorMessage("旧密码不正确");
    	}
    	//密码正则校验
    	RegUtils.match(RegUtils.Regex.of("密码", newPass, RegUtils.REG_PASS));

    	//修改原始密码
    	UserEntity updateUser = new UserEntity();
    	updateUser.setUserId(userEntity.getUserId());
    	updateUser.setPass(DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(newPass.getBytes()).getBytes()));
    	this.userService.updateByPrimaryKeySelective(updateUser);
    	return Messages.SUCCESS;
    }
    
    //修改基本信息
    @PostMapping("/updateInfo")
    @ResponseBody
    @IgnoreAccountStatus
    @IgnoreEmailVerifi
    public Message<Void> updateInfo(@RequestParam("email")String email,														//邮件地址
						    		@RequestParam(value = "personality",required = false)String personality,				//个性签名
						    		@RequestParam(value = "site",required = false)String site,								//个人站点
						    		@RequestParam(value = "github",required = false)String github,
						    		@RequestParam(value = "loginRadio",defaultValue = "false")Boolean loginRadio,
						    		@RequestParam(value = "browseRadio",defaultValue = "false")Boolean browseRadio,
						    		@RequestParam(value = "replyRadio",defaultValue = "false")Boolean replyRadio)throws Exception{			//github主页
    
    	
    	
    	//校验邮箱
    	RegUtils.match(RegUtils.Regex.of("邮件地址", email, RegUtils.REG_EMAIL));
    	
    	UserEntity userEntity = new UserEntity();
    	
    	if(!GeneralUtils.isEmpty(personality)) {
    		personality = JsoupUtils.cleanHTML(personality);
    		if(personality.length() > 255) {
    			return super.getErrorMessage("自我介绍,最多255个字符");
    		}
    	}
    	
    	if(!GeneralUtils.isEmpty(site)) {
    		site = JsoupUtils.cleanHTML(site);
    		if(site.length() > 50) {
    			return super.getErrorMessage("个人站点,最多50个字符");
    		}
    	}
    	
    	if(!GeneralUtils.isEmpty(github)) {
    		github = JsoupUtils.cleanHTML(github);
    		if(github.length() > 50) {
    			return super.getErrorMessage("github主页,最多50个字符");
    		}
    	}
    	
    	userEntity.setUserId(SessionHolder.USER_SESSION.get().getUser().getUserId());
    	userEntity.setEmail(email);
    	userEntity.setPersonality(personality);
    	userEntity.setSite(site);
    	userEntity.setGithub(github);
    	userEntity.setLoginRadio(loginRadio);
    	userEntity.setBrowseRadio(browseRadio);
    	userEntity.setReplyRadio(replyRadio);
    	
    	this.userService.upateUserInfo(userEntity);
    	return Messages.SUCCESS;
    }
    
    //修改头像
    @PostMapping("/updatePortrait")
    @ResponseBody
    public Message<Void> updatePortrait(@RequestParam("portrait")String portrait)throws Exception{
    	UserEntity userEntity = new UserEntity();
    	userEntity.setPortrait(portrait);
    	userEntity.setUserId(SessionHolder.USER_SESSION.get().getUser().getUserId());
    	this.userService.updateByPrimaryKeySelective(userEntity);
    	//TODO 广播聊天室,修改用户头像标识
    	return Messages.SUCCESS;
    }
}


















