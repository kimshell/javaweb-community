package io.javaweb.community.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import io.javaweb.community.common.BaseMapper;
import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.entity.dto.UserDTO;
/**
 * 
 * @author KevinBlandy
 *
 */
@Repository
public interface UserMapper extends BaseMapper<UserEntity>{
	
	/**
	 * 根据账户检索一个用户记录
	 * @param account
	 * @return
	 * @throws Exception
	 */
	UserEntity queryByAccount(String account)throws Exception;

	/**
	 * 根据%name%,检索出记录
	 * @param name
	 * @return
	 * @throws Exception
	 */
	List<UserDTO> queryByLikeName(@Param("name")String name)throws Exception;

	/**
	 * 检索用户详细信息
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	UserDTO queryUserInfoByUserId(String userId)throws Exception;
}
