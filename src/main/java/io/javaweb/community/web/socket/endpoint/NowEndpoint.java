package io.javaweb.community.web.socket.endpoint;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.fastjson.JSON;
import io.javaweb.community.common.SpringContext;
import io.javaweb.community.constants.RedisKeys;
import io.javaweb.community.web.socket.model.SocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import io.javaweb.community.web.socket.config.ServerEndpointConfigurator;
import io.javaweb.community.web.socket.encoder.MessageEncode;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author KevinBlandy
 *
 */
@Component
@ServerEndpoint(value = "/channel/now", encoders = {MessageEncode.class},configurator = ServerEndpointConfigurator.class)
public class NowEndpoint  {

    private static StringRedisTemplate stringRedisTemplate = null;

	private static final Logger LOGGER = LoggerFactory.getLogger(NowEndpoint.class);

	private Session session;

	public static final Set<Session> SESSIONS = new HashSet<>();

 	@OnMessage(maxMessageSize = 10)
    public void onMessage(byte[] message){
        //skip
    }

    @OnOpen
    public void onOpen(Session session,EndpointConfig endpointConfig){
        LOGGER.info("此时此刻 新的连接,id={}",session.getId());
        session.setMaxIdleTimeout(-1);
        SESSIONS.add(session);
        this.session = session;
    }

    @OnClose
    public void onClose(CloseReason closeReason){
        LOGGER.info("此时此刻 连接断开,id={} reason={}",this.session.getId(),closeReason);
        SESSIONS.remove(this.session);
    }

    @OnError
    public void onError(Throwable throwable) throws IOException {
        LOGGER.info("此时此刻 连接异常,id={},throwable={}",this.session.getId(),throwable);
        this.session.close();
        SESSIONS.remove(this.session);
        throwable.printStackTrace();
    }

    /**
     *
     * 消息广播
     * @param message
     * @throws Exception
     *
     */
    public static void broadCast(SocketMessage message)throws Exception{
 	    for(Session session : SESSIONS){
 	        if(session.isOpen()){
 	            session.getAsyncRemote().sendObject(message);
            }
        }
        //消息入栈
        getStringRedisTemplate().opsForList().leftPush(RedisKeys.DYNAMIC_LIST, JSON.toJSONString(message));
        if(getStringRedisTemplate().opsForList().size(RedisKeys.DYNAMIC_LIST) > 100){
            //仅仅记录100长度
            getStringRedisTemplate().opsForList().rightPop(RedisKeys.DYNAMIC_LIST);
        }
    }

    public static StringRedisTemplate getStringRedisTemplate(){
        //!synchronized
        if(stringRedisTemplate == null){
            stringRedisTemplate = SpringContext.getBean(StringRedisTemplate.class);
        }
        return stringRedisTemplate;
    }
}
