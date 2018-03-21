package io.javaweb.community.web.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.javaweb.community.annotation.VerifyCode;
import io.javaweb.community.common.BaseController;
import io.javaweb.community.common.Message;
import io.javaweb.community.exception.ServiceException;
import io.javaweb.community.utils.FileUploadUtils;

/**
 * 
 * 图片上传
 * @author Kevin
 *
 */
@RestController
@RequestMapping("/upload")
public class UploadController extends BaseController {
	
	@Value("${static.image.prefix}")
	private String imagePrefix;
	
	@Value("${static.image.path}")
	private String imagePath;
	
	/**
	 * 
	 * 图片上传
	 * @param multipartFiles
	 * @return
	 * @throws ServiceException 
	 * @throws IOException 
	 * 
	 */
	@PostMapping
	@VerifyCode(limit = -1,max = 20,name = "upload")		//1个小时,只能连续上传20次图片
	public Message<List<String>> upload(@RequestParam("files")MultipartFile[] multipartFiles) throws ServiceException, IOException{
		return super.getSuccessMessage(FileUploadUtils.fileUploads(multipartFiles, this.imagePath, this.imagePrefix));
	}
}








