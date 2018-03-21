package io.javaweb.community.web.controller.manager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.javaweb.community.common.BaseController;
import io.javaweb.community.common.Message;
import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.web.support.SessionHolder;

@RestController
@RequestMapping("/manager/init")
public class InitController extends BaseController {
	
	@GetMapping
	public Message<JSONObject> init(){
		UserEntity userEntity = SessionHolder.MANAGER_SESSION.get().getUser();
		return super.getSuccessMessage(JSON.parseObject(JSON.toJSONString(userEntity)));
	}
}
