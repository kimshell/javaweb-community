package io.javaweb.community.web.controller;

import io.javaweb.community.annotation.IgnoreEmailVerifi;
import io.javaweb.community.common.BaseController;
import io.javaweb.community.common.Message;
import io.javaweb.community.service.MessageService;
import io.javaweb.community.web.support.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by KevinBlandy on 2018/1/29 12:45
 */
@RestController
@RequestMapping("/notify")
public class NotifyController extends BaseController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    @IgnoreEmailVerifi
    public Message<Integer> getNotify()throws Exception{
        return getSuccessMessage(this.messageService.queryUnreadMessageCountByUserId(SessionHolder.USER_SESSION.get().getUser().getUserId()));
    }
}
