package io.javaweb.community.web.controller;

import io.javaweb.community.annotation.IgnoreSession;
import io.javaweb.community.common.BaseController;
import io.javaweb.community.constants.RedisKeys;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by KevinBlandy on 2018/1/18 12:31
 */
@Controller
@RequestMapping("/now")
public class NowController extends BaseController {

    //public static final ModelAndView MODEL_AND_VIEW = new ModelAndView("/now/now");
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 加载此刻动态
     * @param page
     * @return
     */
    @GetMapping
    @IgnoreSession
    public ModelAndView nowPage(@RequestParam(value = "rows",defaultValue = "50")Integer page){
    	ModelAndView modelAndView = new ModelAndView("/now/now");
    	List<String> records = stringRedisTemplate.opsForList().range(RedisKeys.DYNAMIC_LIST, 0, page);
    	List<JSONObject> jsonObjects = new ArrayList<>();
    	for(int x = 0;x < records.size() ; x++) {
            jsonObjects.add(JSONObject.parseObject(records.get(x)));
    	}
    	modelAndView.addObject("records", jsonObjects);
        return modelAndView;
    }
}






