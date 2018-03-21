package io.javaweb.community.web.controller.manager;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import io.javaweb.community.annotation.Admin;
import io.javaweb.community.common.BaseController;
import io.javaweb.community.common.Message;
import io.javaweb.community.common.Messages;
import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.enums.Role;
import io.javaweb.community.service.UserService;
import io.javaweb.community.utils.PageUtils;
import io.javaweb.community.utils.RegUtils;
import io.javaweb.community.web.model.Datagrid;

@RestController("managerUserController")
@RequestMapping("/manager/user")
public class UserController extends BaseController {
	
	@Autowired
	private UserService userService;
	
	//检索所有的用户
	@GetMapping
	public Message<Datagrid<UserEntity>> users(HttpServletRequest request,
									UserEntity userEntity,
									@RequestParam(value = "totalCount",defaultValue = "true") Boolean totalCount,
						            @RequestParam(value="page",defaultValue="1")Integer page,
						            @RequestParam(value="rows",defaultValue="20")Integer rows,
						            @RequestParam(value="sort",defaultValue="createDate") String[] sorts,
						            @RequestParam(value="order",defaultValue="desc")String[] orders)throws Exception{
		return super.getSuccessMessage(new Datagrid<>(this.userService.queryByParamSelective(userEntity, PageUtils.getPageBounds(page, rows,totalCount, PageUtils.getOrders(sorts, orders)))));
	}
	
	//批量修改用户状态
	@PostMapping("/status")
	public Message<Void> message(@RequestBody JSONObject requestBody)throws Exception{
		this.userService.batchUpdateUserStatus(requestBody.getJSONArray("userIds"),requestBody.getString("type"));
		return Messages.SUCCESS;
	}
	
	//修改用户的登录密码
	@PostMapping("/updatePass")
	@Admin
	public Message<Void> password(@RequestParam("userId")String userId,
									@RequestParam("password")String passWord)throws Exception{
		RegUtils.match(RegUtils.Regex.of("密码", passWord, RegUtils.REG_PASS));
		UserEntity userEntity = new UserEntity();
		userEntity.setUserId(userId);
		userEntity.setPass(DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(passWord.getBytes()).getBytes()));
		this.userService.updateByPrimaryKeySelective(userEntity);
		return Messages.SUCCESS;
	}
	
	//设置/取消管理员
	@PostMapping("/setManager")
	@Admin
	public Message<Void> setManager(@RequestParam("type")String type,
									@RequestParam("userIds")String[] userIds)throws Exception{
		
		//
		this.userService.batchUpdateUserRole(userIds,type.equalsIgnoreCase("seting") ? Role.MANAGER : Role.USER);
//		if(!GeneralUtils.isEmpty(userIds)) {
//			UserEntity userEntity = new UserEntity();
//			userEntity.setRole(type.equalsIgnoreCase("seting") ? Role.MANAGER : Role.USER);
//			for(String userId : userIds) {
//				userEntity.setUserId(userId);
//				this.userService.updateByPrimaryKeySelective(userEntity);
//			}
//		}
		return Messages.SUCCESS;
	}
}






