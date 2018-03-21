package io.javaweb.community.beetl.funcs;

import java.util.ArrayList;
import java.util.List;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.fastjson.JSON;

import io.javaweb.community.constants.ConditionBeans;
import io.javaweb.community.constants.RedisKeys;
import io.javaweb.community.entity.FriendLinkEntity;
import io.javaweb.community.service.FriendLinkService;
/**
 * 
 * 友情连接
 * @author Kevin
 *
 */
public class FriendLinkFunc implements Function{
	
	@Autowired
	private FriendLinkService friendLinkService;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Override
	public Object call(Object[] paras, Context ctx) {
		try {
			List<FriendLinkEntity> friendLinks = null;
			if(this.stringRedisTemplate.hasKey(RedisKeys.FRIEND_LINK)) {
				friendLinks = new ArrayList<>();
				//命中缓存
				List<String> records = stringRedisTemplate.opsForList().range(RedisKeys.FRIEND_LINK, 0, this.stringRedisTemplate.opsForList().size(RedisKeys.FRIEND_LINK));
				for(String record : records) {
					friendLinks.add(JSON.parseObject(record, FriendLinkEntity.class));
				}
			}else {
				//从db检索数据
				friendLinks = friendLinkService.queryByParamSelective(ConditionBeans.FRIEND_LINK_ENTITY, ConditionBeans.FRIEND_LINK_BOUNDS);
				//刷入缓存
				for(FriendLinkEntity friendLinkEntity : friendLinks) {
					this.stringRedisTemplate.opsForList().leftPush(RedisKeys.FRIEND_LINK, JSON.toJSONString(friendLinkEntity));
				}
			}
			return friendLinks;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
