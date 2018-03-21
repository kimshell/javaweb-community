package io.javaweb.community.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.javaweb.community.common.BaseController;
import io.javaweb.community.common.Message;
import io.javaweb.community.common.Messages;
import io.javaweb.community.service.MessageService;

@RestController
@RequestMapping("/message")
public class MessageController extends BaseController {

	@Autowired
	private MessageService messageService;
	
	//删除消息
	@PostMapping("/delete")
	public Message<Void> delete(@RequestParam("messageId")String[] messageId)throws Exception{
		this.messageService.batchDeleteMessage(messageId);
		return Messages.SUCCESS;
	}
	
	//消息已读
	@RequestMapping("/reding")
	public Message<Void> reding(@RequestParam("messageId")String[] messageId)throws Exception{
		this.messageService.batchRedingMessage(messageId);
		return Messages.SUCCESS;
	}
}
