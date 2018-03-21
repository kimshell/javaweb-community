package io.javaweb.community.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.javaweb.community.common.BaseController;
import io.javaweb.community.common.Message;
import io.javaweb.community.entity.AttitudeEntity;
import io.javaweb.community.enums.AttitudeTarget;
import io.javaweb.community.enums.AttitudeType;
import io.javaweb.community.service.AttitudeService;
import io.javaweb.community.web.support.SessionHolder;

@RequestMapping("/attitude")
@RestController
public class AttitudeController extends BaseController {
	
	@Autowired
	private AttitudeService attitudeService;
	
	//发表态度
	@PostMapping
	public Message<Integer> attitude(@RequestParam("targetId")String targetId,
							@RequestParam("target")AttitudeTarget target,
							@RequestParam("type")AttitudeType type,
							@RequestParam("operation")String operation)throws Exception{
		
		AttitudeEntity attitudeEntity = new AttitudeEntity();
		
		attitudeEntity.setTargetId(targetId);
		attitudeEntity.setTarget(target);
		attitudeEntity.setType(type);
		
		attitudeEntity.setCreateUser(SessionHolder.USER_SESSION.get().getUser().getUserId());
		
		if(operation.equals("sure")) {
			//创建
			this.attitudeService.createAttitude(attitudeEntity);
		}else {
			//删除
			this.attitudeService.deleteAttiude(attitudeEntity);
		}
		
		int count = this.attitudeService.queryCount(targetId,target,type);
		
		return super.getSuccessMessage(count);
	}
}
