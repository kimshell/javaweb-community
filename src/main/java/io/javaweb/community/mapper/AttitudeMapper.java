package io.javaweb.community.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import io.javaweb.community.common.BaseMapper;
import io.javaweb.community.entity.AttitudeEntity;
import io.javaweb.community.enums.AttitudeTarget;
import io.javaweb.community.enums.AttitudeType;

@Repository
public interface AttitudeMapper extends BaseMapper<AttitudeEntity>{

	
	int queryCount(@Param("targetId")String targetId,@Param("target") AttitudeTarget target, @Param("type")AttitudeType type)throws Exception;
}
