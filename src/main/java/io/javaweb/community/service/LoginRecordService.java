package io.javaweb.community.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.javaweb.community.entity.LoginRecordEntity;
import io.javaweb.community.service.general.GeneralService;
import io.javaweb.community.utils.IpUtils;

/**
 * 
 * @author Kevin
 *
 */
@Service
public class LoginRecordService extends GeneralService<LoginRecordEntity> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginRecordService.class);
	
	@Transactional(rollbackFor = Exception.class)
	public void createRecord(LoginRecordEntity loginRecordEntity) throws Exception{
		try {
			IpUtils.setIpInfo(loginRecordEntity);
		}catch (IOException  e) {
			e.printStackTrace();
			LOGGER.error("检索ip信息异常:{}",e);
		}
		super.create(loginRecordEntity);
	}
}
