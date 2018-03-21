package io.javaweb.community.entity;

import io.javaweb.community.common.BaseEntity;
import io.javaweb.community.enums.AttitudeTarget;
import io.javaweb.community.enums.AttitudeType;
import io.javaweb.community.generate.Entity;
import io.javaweb.community.generate.Id;

@Entity(table = "jw_attitude",mapper = "io.javaweb.community.mapper.AttitudeMapper")
public class AttitudeEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//pk
    @Id
	private String attitudeId;
	
	//目标id
	private String targetId;
	
	//目标类型
	private AttitudeTarget target;
	
	//类型
	private AttitudeType type;

	public String getAttitudeId() {
		return attitudeId;
	}

	public void setAttitudeId(String attitudeId) {
		this.attitudeId = attitudeId;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public AttitudeTarget getTarget() {
		return target;
	}

	public void setTarget(AttitudeTarget target) {
		this.target = target;
	}

	public AttitudeType getType() {
		return type;
	}

	public void setType(AttitudeType type) {
		this.type = type;
	}
}
