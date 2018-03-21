package io.javaweb.community.web.socket.decoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import io.javaweb.community.utils.JsoupUtils;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * Created by KevinBlandy on 2017/10/31 10:46
 */
public class MessageDecoder implements Decoder.Text<String> {

    @Override
    public String decode(String s) throws DecodeException {
    	try {
    		JSONObject jsonObject = JSON.parseObject(s);
    		//xss过滤
    		return JsoupUtils.cleanXss(jsonObject.getString("content"));
    	}catch (JSONException e) {
    		return null;
		}
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
