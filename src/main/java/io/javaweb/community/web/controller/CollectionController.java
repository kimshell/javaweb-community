package io.javaweb.community.web.controller;

import io.javaweb.community.common.BaseController;
import io.javaweb.community.common.Message;
import io.javaweb.community.common.Messages;
import io.javaweb.community.entity.CollectionEntity;
import io.javaweb.community.service.CollectionService;
import io.javaweb.community.web.support.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by KevinBlandy on 2018/1/31 14:50
 */
@RequestMapping("/collection")
@RestController
public class CollectionController extends BaseController{

    @Autowired
    private CollectionService collectionService;

    //添加
    @PostMapping("/create")
    public Message<Void> create(@RequestParam("postId")String postId)throws Exception{

        CollectionEntity collectionEntity = new CollectionEntity();

        collectionEntity.setCreateUser(SessionHolder.USER_SESSION.get().getUser().getUserId());
        collectionEntity.setPostId(postId);

        this.collectionService.createCollection(collectionEntity);
        return Messages.SUCCESS;
    }
    
    //批量删除
    @PostMapping("/remove")
    public Message<Void> remove(@RequestParam("postId")String[] postId)throws Exception{
        this.collectionService.batchDeleteCollectionByPostIds(postId);
        return Messages.SUCCESS;
    }
}
