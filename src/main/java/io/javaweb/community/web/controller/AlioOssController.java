package io.javaweb.community.web.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.OSSClient;

import io.javaweb.community.common.BaseController;
import io.javaweb.community.common.Message;
import io.javaweb.community.exception.ServiceException;
import io.javaweb.community.utils.FileUploadUtils;
import io.javaweb.community.utils.GeneralUtils;

/**
 * 
 * @author KevinBlandy
 *
 */
@RestController
@RequestMapping("/oss")
public class AlioOssController extends BaseController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AlioOssController.class);
	
//	private OSSClient ossClient = null;
	
	//访问端点
	@Value("${oss.endpoint}")
	private String ossEndpoint;
	
	//仓库
	@Value("${oss.bucket.name}")
	private String ossBucketMame;
	
	//资源访问前缀
	@Value("${oss.url.prefix}")
	private String ossUrlPrefix;
	
	//key
	@Value("${access.key.id}")
	private String accessKeyId;
	
	//secret
	@Value("${access.key.secret}")
	private String accessKeySecret;
	
//	@PostConstruct
//	public void ossClientInit() {
//		ossClient = new OSSClient(this.ossEndpoint, this.accessKeyId, this.accessKeySecret);
//	}
//	
//	@PreDestroy
//	public void ossClientDestory() {
//		if(this.ossClient != null) {
//			ossClient.shutdown();
//		}
//	}
	
	/**
	 * 上传文件到oss服务器
	 * @param multipartFiles
	 * @return
	 * @throws ServiceException
	 * @throws IOException
	 */
	@PostMapping("/upload")
	public Message<List<String>> upload(@RequestParam("files") MultipartFile[] multipartFiles) throws ServiceException, IOException {
		
		if(GeneralUtils.isEmpty(multipartFiles)) {
			throw new ServiceException("图片不能为空");
		}else if(multipartFiles.length > 10) {
			throw new ServiceException("一次最多只能上传10张图片");
		}
		List<String> urls = new ArrayList<>();
		OSSClient ossClient = null;
		try{
			ossClient = new OSSClient(this.ossEndpoint, this.accessKeyId, this.accessKeySecret);
			for(MultipartFile multipartFile : multipartFiles) {
				String fileName = LocalDateTime.now().getDayOfWeek().name().toLowerCase() + "/" + GeneralUtils.getUUID() + multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
				if(!GeneralUtils.isImage(multipartFile.getInputStream())) {
					throw new ServiceException("只能上传图片文件,别瞎搞");
				}
				if(multipartFile.getSize() > FileUploadUtils.IMAGE_MAX_SIZE) {
					throw new ServiceException("单个图片不能超过10MB");
				}
				LOGGER.debug("上传图片到OSS:name={},size={},url={}",multipartFile.getOriginalFilename(),multipartFile.getSize(),fileName);
				ossClient.putObject(this.ossBucketMame, fileName, multipartFile.getInputStream());
				urls.add(this.ossUrlPrefix + "/" + fileName);
			}
		}finally {
			if(ossClient != null) {
				ossClient.shutdown();
			}
		}
		return super.getSuccessMessage(urls);
	}
}




