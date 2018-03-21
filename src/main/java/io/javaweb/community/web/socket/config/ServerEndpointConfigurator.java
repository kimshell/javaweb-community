package io.javaweb.community.web.socket.config;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * Created by KevinBlandy on 2017/10/31 10:41
 */
public class ServerEndpointConfigurator  extends ServerEndpointConfig.Configurator{

    public static final String HTTP_SESSION = "http_session";

    /**
     * ws 握手
     * @param serverEndpointConfig
     * @param handshakeRequest
     * @param handshakeResponse
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig serverEndpointConfig, HandshakeRequest handshakeRequest, HandshakeResponse handshakeResponse) {
        super.modifyHandshake(serverEndpointConfig, handshakeRequest, handshakeResponse);
//        Object httpSession = handshakeRequest.getHttpSession();
//        if(httpSession != null){
//            //http session
//            serverEndpointConfig.getUserProperties().put(HTTP_SESSION,httpSession);
//        }
    }
}
