package io.javaweb.community.web.socket.encoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Created by KevinBlandy on 2017/10/31 10:47
 */
public class MessageEncode implements Encoder.Text<Object>{

    @Override
    public String encode(Object object) throws EncodeException {
        //to json
        return JSON.toJSONString(object,SerializerFeature.DisableCircularReferenceDetect);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
