package io.javaweb.community.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Created by KevinBlandy on 2017/10/31 11:09
 */
public class JsoupUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsoupUtils.class);

    private static final Whitelist XSS_LIST = Whitelist.none();
    
    private static final Whitelist WHITELIST = Whitelist.none();

    /**
     * 过滤所有的HTML元素
     * @param content
     * @return
     */
    public static String cleanHTML(String content) {
    	return WHITELIST == null ? null : Jsoup.clean(content, WHITELIST);
    }
    
    /**
     * 过滤XSS字符
     * @param content
     * @return
     */
    public static String cleanXss(String content) {
        return content == null ? null : Jsoup.clean(content, XSS_LIST);
    }

    static {

        //读取配置JSON文件
        Resource resource = new ClassPathResource("xss-white.json");
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))){
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line.trim());
            }

            JSONObject jsonObject = JSON.parseObject(stringBuilder.toString());

            //允许标签
            JSONArray tags = jsonObject.getJSONArray("allow_tags");
            XSS_LIST.addTags(tags.toArray(new String[tags.size()]));
            LOGGER.info("允许标签:{}", tags);

            //允许属性
            JSONArray properties = jsonObject.getJSONArray("allow_properties");
            XSS_LIST.addAttributes(":all",properties.toArray(new String[properties.size()]));
            LOGGER.info("允许属性:{}",properties);

            //允许特殊属性
            JSONObject specialProperties = jsonObject.getJSONObject("special_properties");
            specialProperties.keySet().stream().forEach(tag -> {
                JSONArray attributes = specialProperties.getJSONArray(tag);
                XSS_LIST.addAttributes(tag,attributes.toArray(new String[attributes.size()]));
                LOGGER.info("允许特殊属性:标签={},属性={}",tag,attributes);
            });

            //允许特殊协议
            JSONObject protocols = jsonObject.getJSONObject("protocols");
            protocols.keySet().stream().forEach(tag -> {
                JSONObject protoObject = protocols.getJSONObject(tag);
                protoObject.keySet().stream().forEach(attr -> {
                    JSONArray protocolValues = protoObject.getJSONArray(attr);
                    XSS_LIST.addProtocols(tag,attr,protocolValues.toArray(new String[protocolValues.size()]));
                    LOGGER.info("允许特殊协议:标签={},属性={},协议={}",tag,attr,protocolValues);
                });
            });

            //固定属性值,非必须的
            JSONObject fixedProperties = jsonObject.getJSONObject("fixed_properties");
            if(fixedProperties != null && !fixedProperties.isEmpty()) {
                fixedProperties.keySet().stream().forEach(tag -> {
                    JSONObject property = fixedProperties.getJSONObject(tag);
                    if(property != null && !property.isEmpty()) {
                        property.keySet().stream().forEach(attr -> {
                            String value = property.getString(attr);
                            XSS_LIST.addEnforcedAttribute(tag, attr, value);
                            LOGGER.info("强制属性:标签={},属性={},值={}",tag,attr,value);
                        });
                    }
                });
            }
            
            //允许a标签的href属性值为相对路径
            XSS_LIST.preserveRelativeLinks(true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("加载XSS过滤白名单异常,请检查文件 xss-white.json");
        }
    }
}
