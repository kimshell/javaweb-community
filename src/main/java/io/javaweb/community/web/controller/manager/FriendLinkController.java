package io.javaweb.community.web.controller.manager;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.javaweb.community.annotation.Admin;
import io.javaweb.community.common.BaseController;
import io.javaweb.community.common.Message;
import io.javaweb.community.common.Messages;
import io.javaweb.community.constants.RedisKeys;
import io.javaweb.community.entity.FriendLinkEntity;
import io.javaweb.community.service.FriendLinkService;
import io.javaweb.community.utils.FileUploadUtils;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.utils.PageUtils;
import io.javaweb.community.web.model.Datagrid;

@RestController
@RequestMapping("/manager/friendLink")
public class FriendLinkController extends BaseController {	
	
	@Value("${static.image.prefix}")
	private String imagePrefix;
	
	@Value("${static.image.path}")
	private String imagePath;
	
	@Autowired
	private FriendLinkService friendLinkService;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	//检索所有的友情连接记录
	@GetMapping
	public Message<Datagrid<FriendLinkEntity>> users(HttpServletRequest request,
									FriendLinkEntity friendLinkEntity,
									@RequestParam(value = "totalCount",defaultValue = "true") Boolean totalCount,
						            @RequestParam(value="page",defaultValue="1")Integer page,
						            @RequestParam(value="rows",defaultValue="20")Integer rows,
						            @RequestParam(value="sort",defaultValue="createDate") String[] sorts,
						            @RequestParam(value="order",defaultValue="desc")String[] orders)throws Exception{
		return super.getSuccessMessage(new Datagrid<>(this.friendLinkService.queryByParamSelective(friendLinkEntity, PageUtils.getPageBounds(page, rows,totalCount, PageUtils.getOrders(sorts, orders)))));
	}
	
	//创建新的友情连接
	@PostMapping("/create")
	@Admin
	public Message<Void> create(@RequestParam(value = "logo",required = false)String logo,
								@RequestParam("name")String name,
								@RequestParam("url")String url,
								@RequestParam("sorted")Long sorted)throws Exception{
		FriendLinkEntity friendLinkEntity = new FriendLinkEntity();
		friendLinkEntity.setFrendLinkId(GeneralUtils.getUUID());
		friendLinkEntity.setLogo(logo);
		friendLinkEntity.setName(name);
		friendLinkEntity.setUrl(url);
		friendLinkEntity.setSorted(sorted);
		friendLinkEntity.setCreateDate(new Date());
		this.friendLinkService.create(friendLinkEntity);
		
		this.stringRedisTemplate.delete(RedisKeys.FRIEND_LINK);
		return Messages.SUCCESS;
	}
	
	//修改友情连接
	@PostMapping("/update")
	@Admin
	public Message<Void> update(@RequestParam("frendLinkId")String frendLinkId,
								@RequestParam(value = "logo",required = false)String logo,
								@RequestParam("name")String name,
								@RequestParam("url")String url,
								@RequestParam("sorted")Long sorted)throws Exception{
		FriendLinkEntity friendLinkEntity = new FriendLinkEntity();
		friendLinkEntity.setFrendLinkId(frendLinkId);
		friendLinkEntity.setLogo(logo);
		friendLinkEntity.setName(name);
		friendLinkEntity.setUrl(url);
		friendLinkEntity.setSorted(sorted);
		friendLinkEntity.setCreateDate(new Date());
		this.friendLinkService.updateByPrimaryKeySelective(friendLinkEntity);
		
		this.stringRedisTemplate.delete(RedisKeys.FRIEND_LINK);
		return Messages.SUCCESS;
	}
	
	//删除友情连接
	@PostMapping("/delete")
	@Admin
	public Message<Void> delete(@RequestParam("firendLinkIds")String[] firendLinkIds)throws Exception{
		if(!GeneralUtils.isEmpty(firendLinkIds)) {
			this.friendLinkService.batchDeleteById(firendLinkIds);
		}
		
		this.stringRedisTemplate.delete(RedisKeys.FRIEND_LINK);
		return Messages.SUCCESS;
	}
	
	//上传logo到oss服务器
	@PostMapping("/upload")
	@Admin
	public Message<String> upload(@RequestParam("file")MultipartFile multipartFile)throws Exception{
		return super.getSuccessMessage(FileUploadUtils.fileUploads(new MultipartFile[] {multipartFile}, this.imagePath, this.imagePrefix).get(0));
	}
}
