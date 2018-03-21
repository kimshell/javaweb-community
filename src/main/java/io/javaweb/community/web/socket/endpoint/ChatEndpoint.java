package io.javaweb.community.web.socket.endpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import io.javaweb.community.common.SpringContext;
import io.javaweb.community.constants.RedisKeys;
import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.utils.JsoupUtils;
import io.javaweb.community.web.socket.config.ServerEndpointConfigurator;
import io.javaweb.community.web.socket.encoder.MessageEncode;
import io.javaweb.community.web.socket.exception.SocketException;
import io.javaweb.community.web.socket.model.ChatMessage;
import io.javaweb.community.web.support.UserSession;

/**
 * 
 * @author KevinBlandy
 * 
 */
@Component
@ServerEndpoint(value = "/channel/chat/{sessionId}", 
		encoders = {MessageEncode.class},
		//decoders = {MessageDecoder.class},
		configurator = ServerEndpointConfigurator.class)
public class ChatEndpoint {
	
	private static final long MESSAGE_MAX_FREQUENCY = 1000;		// +1s (*^__^*) 
	
	private static final String TOURISTS = "tourists";
	
	private static final UserEntity TOURISTS_USER = new UserEntity();
	
	static {
		TOURISTS_USER.setName("游客");
		TOURISTS_USER.setUserId(TOURISTS);
		TOURISTS_USER.setPortrait("http://www.javaweb.io/static/image/anonymous.png");
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatEndpoint.class);
	
	public static final Set<ChatEndpoint> SESSIONS = Collections.synchronizedSet(new HashSet<>());
	
	private static StringRedisTemplate stringRedisTemplate;
	
	private UserEntity user;
	
	private Session session;
	
	private long lastMessageSend;
	
	/**
	 * 新的消息
	 * @param content
	 * @throws EncodeException 
	 * @throws IOException 
	 */
	@OnMessage(maxMessageSize = 1024)		//消息最大1MB消息
    public void onMessage(String content) throws IOException, EncodeException{
		LOGGER.debug("新的消息:content={}",content);
		if(content == null) {
			return;
		}
		if(this.user == TOURISTS_USER) {
			this.session.getAsyncRemote().sendObject(new ChatMessage(ChatMessage.Type.NOTIFY, "游客身份,不能发送消息", null));
			return;
		}else{
			//TODO 判断账户邮箱是否认证
			//TODO 判断账户状态是否被禁用
			//TODO 判断当前连接是否被禁用
			
			//消息发送频率是否超时
			long now = System.currentTimeMillis();
			if((now - this.lastMessageSend) < MESSAGE_MAX_FREQUENCY ) {
				//两次消息小于1s
				this.session.getAsyncRemote().sendObject(new ChatMessage(ChatMessage.Type.NOTIFY, "消息发送太快,慢点", null));
				return;
			}
			this.lastMessageSend = now;
		}
		content = JsoupUtils.cleanXss(content);
		//TODO 消息持久化
		broadCast(new ChatMessage(ChatMessage.Type.MESSAGE, content, this.user), chatEndpoint -> {
			return true;
		});
    }
	
	/**
	 * 新的连接
	 * @param session
	 * @param sessionId
	 * @param endpointConfig
	 * @throws IOException
	 * @throws SocketException
	 * @throws EncodeException 
	 */
	@OnOpen
    public void onOpen(Session session,@PathParam("sessionId") String sessionId,EndpointConfig endpointConfig) throws IOException, SocketException, EncodeException{
		this.session = session;
		LOGGER.debug("新的连接:socketSessionId={},userSessionId={}",session.getId(),sessionId);
//		if(GeneralUtils.isEmpty(sessionId)) {
//			this.session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Fuck Off~~"));
//			return;
//		}
		if(!sessionId.equals(TOURISTS)) {
			String jsonValue = getStringRedisTemplate().opsForValue().get(RedisKeys.SESSION_USER + sessionId);
			if(!GeneralUtils.isEmpty(jsonValue)) {
				UserSession userSession = JSON.parseObject(jsonValue, UserSession.class);
				
				//判断用户是否重复连接
				Iterator<ChatEndpoint> iterator = SESSIONS.iterator();
				while(iterator.hasNext()) {
					ChatEndpoint chatEndpoint = iterator.next();
					//非匿名用户
					if(chatEndpoint.user != TOURISTS_USER) {
						if(userSession.getUser().getUserId().equals(chatEndpoint.user.getUserId())) {
							iterator.remove();		//通过迭代器移除已经存在的channel,在触发 onClose 方法中执行 remove()不存在的channel时,不会抛出 ConcurrentModificationException
							//重复连接,关闭旧连接
							chatEndpoint.session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "账户在异地连接聊天室,当前连接已经断开"));
						}
					}
				}
				
				this.user = userSession.getUser();
			}
		} 
		
		if(this.user == null) {
			this.user = TOURISTS_USER;
		}
		
		//在线用户信息推送
		this.session.getAsyncRemote().sendObject(new ChatMessage(ChatMessage.Type.USERS, null,SESSIONS.stream().map(channel -> {return channel.user;}).collect(Collectors.toList())));
		//广播加入消息
		broadCast(new ChatMessage(ChatMessage.Type.JOIN, null, this.user), chatEndpoint -> {
			return true;
		});
		this.session.setMaxIdleTimeout(-1);
		SESSIONS.add(this);
	}
	
	/**
	 * 连接关闭 
	 * @param closeReason
	 * @throws EncodeException 
	 * @throws IOException 
	 */
	@OnClose
	public void onClose(CloseReason closeReason) throws IOException, EncodeException{
		LOGGER.debug("连接断开:socketSessionId={},reason={}",this.session.getId(),closeReason);
		//从会话移除当前通道
		SESSIONS.remove(this);
		//广播退出消息
		broadCast(new ChatMessage(ChatMessage.Type.QUIT, null, this.user), chatEndpoint -> {
			return true;
		});
	}
	
	/**
	 * 异常处理
	 * @param throwable
	 * @throws IOException
	 */
	@OnError
	public void onError(Throwable throwable) throws IOException {
		LOGGER.error("连接异常:socketSessionId={}",this.session.getId());
		throwable.printStackTrace();
		CloseReason closeReason = null;
		if (throwable instanceof SocketException) {
			closeReason = ((SocketException) throwable).getCloseReason();
		}else {
			closeReason = new CloseReason(CloseCodes.NORMAL_CLOSURE, "服务器异常,连接断开");
		}
		this.session.close(closeReason);
	}
	
	/**
	 * 广播
	 * @param message
	 * @param predicate
	 * @throws IOException
	 * @throws EncodeException
	 */
	public static void broadCast(ChatMessage message,Predicate<ChatEndpoint> predicate) throws IOException, EncodeException {
		for(ChatEndpoint chatEndpoint : SESSIONS) {
			if(predicate.test(chatEndpoint)) {
				Session session = chatEndpoint.session;
				if(session.isOpen()) {
					session.getBasicRemote().sendObject(message);
					//session.getAsyncRemote().sendObject(message);
				}
			}
		}
	}
	
	
	/**
	 * 关闭符合条件的会话
	 * @param predicate
	 * @param closeReason
	 */
	public static void close(Predicate<ChatEndpoint> predicate,CloseReason closeReason) {
		Iterator<ChatEndpoint> iterator = SESSIONS.iterator();
		while(iterator.hasNext()) {
			ChatEndpoint chatEndpoint = iterator.next();
			if(predicate.test(chatEndpoint)) {
				try {
					if(chatEndpoint.session.isOpen()) {
						iterator.remove();		//通过迭代器移除已经存在的channel,在触发 onClose 方法中执行 remove()不存在的channel时,不会抛出 ConcurrentModificationException
						chatEndpoint.session.close(closeReason);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static StringRedisTemplate getStringRedisTemplate(){
		//!synchronized
		if(ChatEndpoint.stringRedisTemplate == null){
			ChatEndpoint.stringRedisTemplate = SpringContext.getBean(StringRedisTemplate.class);
		}
		return ChatEndpoint.stringRedisTemplate;
	}

	
	/*****************		getter/setter	****************************/
	
	public UserEntity getUser() {
		return user;
	}
	
	public Session getSession() {
		return this.session;
	}
}













