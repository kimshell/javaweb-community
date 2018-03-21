package io.javaweb.community.service;

import io.javaweb.community.entity.CollectionEntity;
import io.javaweb.community.entity.PostEntity;
import io.javaweb.community.entity.dto.CollectionDTO;
import io.javaweb.community.enums.Status;
import io.javaweb.community.exception.ServiceException;
import io.javaweb.community.mapper.CollectionMapper;
import io.javaweb.community.mybatis.domain.PageBounds;
import io.javaweb.community.mybatis.domain.PageList;
import io.javaweb.community.service.general.GeneralService;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.web.support.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by KevinBlandy on 2018/1/31 14:50
 */
@Service
public class CollectionService extends GeneralService<CollectionEntity> {

    @Autowired
    private PostService postService;

    private CollectionMapper getMapper(){
        return (CollectionMapper) super.getBaseMapper();
    }

    //创建
    @Transactional(rollbackFor = Exception.class)
    public void createCollection(CollectionEntity collectionEntity) throws Exception{

        PostEntity postEntity = this.postService.queryByPrimaryKey(collectionEntity.getPostId());

        if(postEntity == null || !Status.NORMAL.equals(postEntity.getStatus())){
            throw new ServiceException("帖子信息不存在");
        }

        if(super.queryByParamSelectiveSole(collectionEntity) != null){
            throw new ServiceException("重复收藏");
        }

        collectionEntity.setCollectionId(GeneralUtils.getUUID());
        collectionEntity.setCreateDate(new Date());

        super.create(collectionEntity);
    }

    //删除
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteCollectionByPostIds(String[] postId) throws Exception{
        if(GeneralUtils.isEmpty(postId)){
           return;
        }
        CollectionEntity collectionEntity = new CollectionEntity();
        collectionEntity.setCreateUser(SessionHolder.USER_SESSION.get().getUser().getUserId());
        for(String id : postId){
            collectionEntity.setPostId(id);
            super.deleteByParamSelective(collectionEntity);
        }
    }

    //检索
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public PageList<CollectionDTO> queryCollectionsByCreateUser(CollectionDTO collectionDTO, PageBounds pageBounds) throws Exception{
        return this.getMapper().queryCollectionsByCreateUser(collectionDTO,pageBounds);
    }
}
