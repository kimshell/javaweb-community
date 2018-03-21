package io.javaweb.community.service;

import io.javaweb.community.common.Message;
import io.javaweb.community.common.Message.Status;
import io.javaweb.community.exception.ServiceException;
import io.javaweb.community.mapper.UserMapper;

import java.util.List;

import org.springframework.stereotype.Service;

import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.entity.dto.UserDTO;
import io.javaweb.community.enums.Role;
import io.javaweb.community.service.general.GeneralService;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.web.support.SessionHolder;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;

/**
 * 
 * @author KevinBlandy
 *
 */
@Service
public class UserService extends GeneralService<UserEntity>{

    public UserMapper getMapper(){
         return (UserMapper) super.getBaseMapper();
    }

    /**
     * 根据账户检索记录
     * @param account
     * @return
     * @throws Exception
     */
	@Transactional(readOnly = true,rollbackFor = Exception.class)
    public UserEntity queryByAccount(String account)throws Exception{
	    return this.getMapper().queryByAccount(account);
    }

    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public UserEntity queryByEmail(String email)throws Exception{
	    UserEntity userEntity = new UserEntity();
	    userEntity.setEmail(email);
	    return super.queryByParamSelectiveSole(userEntity);
    }

    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public UserEntity queryByName(String name)throws Exception{
        UserEntity userEntity = new UserEntity();
        userEntity.setName(name);
        return super.queryByParamSelectiveSole(userEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void register(UserEntity userEntity) throws Exception{
        if(this.queryByEmail(userEntity.getEmail()) != null){
            throw new ServiceException("邮箱已经存在", Message.Status.ALREADY_EXIST);
        }
        if(this.queryByName(userEntity.getName()) != null){
            throw new ServiceException("用户名已经存在", Message.Status.ALREADY_EXIST);
        }
        super.create(userEntity);
    }
    
    @Transactional(readOnly = true,rollbackFor = Exception.class)
	public List<UserDTO> queryByLikeName(String name) throws Exception{
		return this.getMapper().queryByLikeName(name);
	}

    //检索用户详细信息
    @Transactional(readOnly = true,rollbackFor = Exception.class)
	public UserDTO queryUserInfoByUserId(String userId) throws Exception{
		UserDTO userDTO =  this.getMapper().queryUserInfoByUserId(userId);
		if(userDTO == null) {
			throw new ServiceException(Status.BAD_URL);
		}
		return userDTO;
	}

    //更新用户信息
    @Transactional(rollbackFor = Exception.class)
	public void upateUserInfo(UserEntity userEntity) throws Exception{
    	//判断邮件地址是否重复
    	UserEntity user = this.queryByEmail(userEntity.getEmail());
    	if(user != null && !user.getUserId().equals(userEntity.getUserId())) {
    		throw new ServiceException("邮箱已经存在", Message.Status.ALREADY_EXIST);
    	}
    	if(user == null) {
    		user = super.queryByPrimaryKey(userEntity.getUserId());
    	}
    	
		if(!user.getEmail().equals(userEntity.getEmail())) {
			//修改了邮件地址,则需要重新校验邮箱
			userEntity.setEmailVerifi(Boolean.FALSE);
		}
		super.updateByPrimaryKeySelective(userEntity);
	}
    
    //批量修改用户状态
    @Transactional(rollbackFor = Exception.class)
	public void batchUpdateUserStatus(JSONArray userIds, String type) throws Exception {
		UserEntity userEntity = new UserEntity();
		userEntity.setStatus(io.javaweb.community.enums.Status.valueOf(type.toUpperCase()));
		for(int x = 0 ;x < userIds.size() ; x++) {
			String userId = userIds.getString(x);
			UserEntity user = super.queryByPrimaryKey(userId);
			if(user == null) {
				continue;
			}
			if(user.getRole().equals(Role.ADMIN) && !SessionHolder.MANAGER_SESSION.get().getUser().getRole().equals(Role.ADMIN)) {
				throw new ServiceException("无权修改站长账户状态",Status.NO_PERMISSION);
			}
			if(user.getRole().equals(Role.MANAGER) && !SessionHolder.MANAGER_SESSION.get().getUser().getRole().equals(Role.ADMIN)) {
				throw new ServiceException("无权修改管理员账户状态",Status.NO_PERMISSION);
			}
			userEntity.setUserId(userId);
			super.updateByPrimaryKeySelective(userEntity);
		}
	}

    //批量修改用户角色
    @Transactional(rollbackFor = Exception.class)
	public void batchUpdateUserRole(String[] userIds, Role role) throws Exception {
		if(!GeneralUtils.isEmpty(userIds)) {
			UserEntity userEntity = new UserEntity();
			userEntity.setRole(role);
			for(String userId : userIds) {
				UserEntity user = super.queryByPrimaryKey(userId);
				if(user == null) {
					continue;
				}
				if(user.getRole().equals(Role.ADMIN)) {
					throw new ServiceException("无权修改站长角色",Status.NO_PERMISSION);
				}
				userEntity.setUserId(userId);
				super.updateByPrimaryKeySelective(userEntity);
			}
		}		
	}
}
