package io.javaweb.community.common;

import javax.websocket.Session;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by KevinBlandy on 2017/10/31 10:16
 */
@Deprecated
public class BaseEndpoint {

	//current session
    protected Session session;

    /**
     * 移除指定的SESSION
     * @param sessions
     * @param predicate
     * @param <K>
     */
    public static <K> void removeSession(Map<K,Session> sessions,Predicate<Map.Entry<K,Session>> predicate){
    	sessions.entrySet().forEach(entry -> {
            if(predicate.test(entry)){
            	sessions.remove(entry.getKey());
            }
        });
    }

    /**
     * 清空所有SESSION
     * @param sessions
     * @param <K>
     */
    public static <K> void clearSession(Map<K,Session> sessions){
    	sessions.clear();
    }

    /**
     * 获取符合条件的第一个SESSION
     * @param sessions
     * @param predicate
     * @param <K>
     * @return
     */
    public static <K> Session getSession(Map<K,Session> sessions,Predicate<Map.Entry<K,Session>> predicate){
        Optional<Map.Entry<K,Session>> optional = sessions.entrySet().stream().filter(predicate).findFirst();
        if(optional.isPresent()){
            return optional.get().getValue();
        }
        return null;
    }

    /**
     * 消息广播
     * @param <K>
     * @param <V>
     * @param predicate
     * @param message
     */
    public static <K, V> void broadCast(Map<K,Session> sessions,Predicate<Map.Entry<K,Session>> predicate,Object message){
        sessions.entrySet().stream().filter(predicate).forEach(entry -> {
            Session session = entry.getValue();
            if(session.isOpen()){
                session.getAsyncRemote().sendObject(message);
            }
        });
    }
}