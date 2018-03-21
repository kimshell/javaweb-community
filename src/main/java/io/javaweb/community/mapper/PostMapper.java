package io.javaweb.community.mapper;

import io.javaweb.community.common.BaseMapper;
import io.javaweb.community.entity.PostEntity;
import io.javaweb.community.entity.dto.PostDTO;
import io.javaweb.community.mybatis.domain.PageBounds;
import io.javaweb.community.mybatis.domain.PageList;


import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by Kevin on 2018/1/17 22:21.
 */
@Repository
public interface PostMapper extends BaseMapper<PostEntity>{

	//根据帖子id检索帖子详情
	PostDTO queryPostDetailByPostId(PostDTO postDTO)throws Exception;

	//检索主页帖子
	PageList<PostDTO> queryIndexPosts(PostDTO postDTO, PageBounds pageBounds)throws Exception;
	
	//帖子阅读量自增1
	int incrementPostBrowse(@Param("postId")String postId)throws Exception;

//	//检索置顶帖
//	List<PostDTO> queryTopPosts()throws Exception;
	
	//根据非空参数修改status
	int updateStatusByParamSelective(PostEntity postEntity)throws Exception;
}
