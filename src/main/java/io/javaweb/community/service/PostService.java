package io.javaweb.community.service;

import io.javaweb.community.common.Message.Status;
import io.javaweb.community.constants.ConditionBeans;
import io.javaweb.community.entity.MessageEntity;
import io.javaweb.community.entity.PostEntity;
import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.entity.dto.PostDTO;
import io.javaweb.community.entity.dto.PostReplyDTO;
import io.javaweb.community.enums.MessageType;
import io.javaweb.community.enums.ReplyControl;
import io.javaweb.community.exception.ServiceException;
import io.javaweb.community.mapper.PostMapper;
import io.javaweb.community.mybatis.domain.PageBounds;
import io.javaweb.community.mybatis.domain.PageList;
import io.javaweb.community.service.general.GeneralService;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.utils.MentionUtils;
import io.javaweb.community.web.support.PageInfo;
import io.javaweb.community.web.support.SessionHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by KevinBlandy on 2018/1/24 17:47
 */
@Service
public class PostService extends GeneralService<PostEntity> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);
	
	@Autowired
	private PostReplyService postReplyService;

	@Autowired
    private MessageService messageService;

	private PostMapper getMapper() {
		return (PostMapper) super.getBaseMapper();
	}

    //创建
    @Transactional(rollbackFor = Exception.class)
	public void createPost(PostEntity postEntity) throws Exception{
    	
    	//提取@用户,渲染正文
    	Map<String,UserEntity> userMap = new HashMap<>();
    	
    	postEntity.setContent(MentionUtils.mentionParse(postEntity.getContent(), userMap));
    	
    	if(!userMap.isEmpty()) {

    		String sessionUserId = SessionHolder.USER_SESSION.get().getUser().getUserId();

            MessageEntity atMessage = new MessageEntity();

            atMessage.setPostId(postEntity.getPostId());
            atMessage.setIsRead(false);
            atMessage.setCreateDate(new Date());
            atMessage.setCreateUser(sessionUserId);
            atMessage.setType(MessageType.POST_AT);

    		for(Map.Entry<String, UserEntity> entry : userMap.entrySet()) {
    			UserEntity user = entry.getValue();
    			if(!user.getUserId().equals(sessionUserId)) {
    				LOGGER.debug("@到用户{}",user.getName());
                    atMessage.setUserId(user.getUserId());
                    atMessage.setMessageId(GeneralUtils.getUUID());
                    atMessage.setAtAnchor(entry.getKey());
                    this.messageService.create(atMessage);
    			}
    		}
    	}
		super.create(postEntity);
	}
    
    //检索帖子详情
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    //@Transactional(rollbackFor = Exception.class)
	public PostDTO queryPostDetailByPostId(PostDTO postDTO,PageBounds pageBounds) throws Exception{
    	
		PostDTO resultPost = this.getMapper().queryPostDetailByPostId(postDTO);
		
		if(resultPost == null || resultPost.getStatus().equals(io.javaweb.community.enums.Status.DELETED)) {
        	//记录不存在,或者被逻辑删除
        	throw new ServiceException(Status.BAD_URL);
        }
        if(resultPost.getStatus().equals(io.javaweb.community.enums.Status.DISABLE)) {
        	//帖子被封,不能查阅
        	throw new ServiceException(Status.POST_IS_DISABLED);
        }
        
//        if(resultPost.getBrowse() <= 99999999){
//        	//刷新阅读量
//        	this.getMapper().incrementPostBrowse(postDTO.getPostId());
//        }
        
        if(pageBounds != null) {
        	//根据分页信息检索帖子的回帖信息
        	PostReplyDTO postReplyDTO = new PostReplyDTO();
        	postReplyDTO.setPostId(postDTO.getPostId());
        	postReplyDTO.setSessionUser(postDTO.getSessionUser());
        	PageList<PostReplyDTO> replys = this.postReplyService.queryReplysByPostId(postReplyDTO, pageBounds);
        	resultPost.setPostReplys(replys);
        }
		return resultPost;
	}

    //检索主页帖子
    @Transactional(readOnly = true,rollbackFor = Exception.class)
	public Map<String,Object>queryIndexPosts(PostDTO postDTO, PageBounds pageBounds) throws Exception {
    	Map<String,Object> hashMap = new HashMap<>();
    	PageList<PostDTO> posts = this.getMapper().queryIndexPosts(postDTO,pageBounds);
    	PageList<PostDTO> topPosts = this.getMapper().queryIndexPosts(ConditionBeans.TOP_POST, ConditionBeans.TOP_PAGE_BOUNDS);
    	hashMap.put("posts", posts);
    	hashMap.put("topPosts", topPosts);
    	hashMap.put("pageInfo", new PageInfo(posts.getPaginator()));
    	return hashMap;
	}
    

    //刷新帖子阅读量
    @Transactional(rollbackFor = Exception.class)
    public void incrementPostBrowse(String postId) throws Exception{
    	this.getMapper().incrementPostBrowse(postId);
    }
    
    //根据非空参数修改status
    @Transactional(rollbackFor = Exception.class)
    public int updateStatusByParamSelective(PostEntity postEntity) throws Exception{
    	return this.getMapper().updateStatusByParamSelective(postEntity); 
    }

    //批量的逻辑删除帖子
    @Transactional(rollbackFor = Exception.class)
	public void batchLogicDeleteByPostId(String[] postIds) throws Exception{
    	
    	PostEntity postEntity = new PostEntity();
    	postEntity.setStatus(io.javaweb.community.enums.Status.DELETED);
    	postEntity.setCreateUser(SessionHolder.USER_SESSION.get().getUser().getUserId());
    	
    	for(String postId : postIds) {
    		postEntity.setPostId(postId);
    		this.updateStatusByParamSelective(postEntity);
    	}
	}

    //修改帖子
    @Transactional(rollbackFor = Exception.class)
	public void updatePost(PostEntity postEntity)throws Exception {
		
		PostEntity post = new PostEntity();
		
		post.setPostId(postEntity.getPostId());
		post.setStatus(io.javaweb.community.enums.Status.NORMAL);
		post.setCreateUser(SessionHolder.USER_SESSION.get().getUser().getUserId());
		
		post = super.queryByParamSelectiveSole(post);
		if(post == null) {
			throw new ServiceException(Status.NOT_FOUND);
		}
		
		if(post.getReplyControl().equals(ReplyControl.DISALLOW)) {
			//帖子被管理员设置为禁止回复,则置空该字段,不允许修改
			postEntity.setReplyControl(null);
		}
		
		/**
    	 * 删除文章的旧@信息
    	 */
    	MessageEntity messageEntity = new MessageEntity();
    	messageEntity.setType(MessageType.POST_AT);
    	messageEntity.setPostId(postEntity.getPostId());
    	this.messageService.deleteByParamSelective(messageEntity);
		
    	/**
    	 * 提取本次@
    	 */
		Map<String,UserEntity> userMap = new HashMap<>();
    	postEntity.setContent(MentionUtils.mentionParse(postEntity.getContent(), userMap));
    	
    	if(!userMap.isEmpty()) {

    		String sessionUserId = SessionHolder.USER_SESSION.get().getUser().getUserId();

            MessageEntity atMessage = new MessageEntity();

            atMessage.setPostId(postEntity.getPostId());
            atMessage.setIsRead(false);
            atMessage.setCreateDate(new Date());
            atMessage.setCreateUser(sessionUserId);
            atMessage.setType(MessageType.POST_AT);

    		for(Map.Entry<String, UserEntity> entry : userMap.entrySet()) {
    			UserEntity user = entry.getValue();
    			if(!user.getUserId().equals(sessionUserId)) {
    				LOGGER.debug("@到用户{}",user.getName());
                    atMessage.setUserId(user.getUserId());
                    atMessage.setMessageId(GeneralUtils.getUUID());
                    atMessage.setAtAnchor(entry.getKey());
                    this.messageService.create(atMessage);
    			}
    		}
    	}
		
		super.updateByPrimaryKeySelective(postEntity);
	}
}


