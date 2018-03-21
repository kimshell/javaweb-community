package io.javaweb.community.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import io.javaweb.community.exception.ServiceException;

public class FileUploadUtils {
	
	//单张图片最大10MB
	public static final Long IMAGE_MAX_SIZE  = (1024L * 1024L) * 10L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtils.class);
	
	/**
	 * 图片上传
	 * @param multipartFiles		
	 * @param imagePath			写入的本地文件夹
	 * @param imagePrefix		访问图片的前缀
	 * @return
	 * @throws ServiceException
	 * @throws IOException
	 */
	public static List<String> fileUploads(MultipartFile[] multipartFiles,String imagePath,String imagePrefix) throws ServiceException, IOException {
		
		if(GeneralUtils.isEmpty(multipartFiles)) {
			throw new ServiceException("上传图片不能为空");
		}else if(multipartFiles.length > 10) {
			throw new ServiceException("一次最多只能上传10张图片");
		}
		
		List<String> imageUrls = new ArrayList<>();
		
		for(MultipartFile multipartFile : multipartFiles) {
			LOGGER.info("上传图片:size={},name={}",multipartFile.getSize(),multipartFile.getOriginalFilename());
			if(multipartFile.getSize() > IMAGE_MAX_SIZE) {
				throw new ServiceException("文件["+multipartFile.getOriginalFilename()+"]大小超过了10MB");
			}
			if(!GeneralUtils.isImage(multipartFile.getInputStream())) {
				throw new ServiceException("只能上传图片文件,别瞎搞");
			}

			//文件重名处理
			String fileName = GeneralUtils.getUUID() + 
							multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
			
			//hash打散目录
			String hashCodeHex = Integer.toHexString(fileName.hashCode());
			
			Path rootPath = Paths.get(imagePath, hashCodeHex.charAt(0) + "",hashCodeHex.charAt(1) + "",hashCodeHex.charAt(2) + "",hashCodeHex.charAt(3) + "");
			
			if(!Files.isDirectory(rootPath)) {
				//目录不存在,创建之
				Files.createDirectories(rootPath);
			}
			
			//io到磁盘
			Files.write(rootPath.resolve(fileName), multipartFile.getBytes(), StandardOpenOption.CREATE_NEW);
			
			imageUrls.add(imagePrefix  + "/" + hashCodeHex.charAt(0) + "/" + hashCodeHex.charAt(1) + "/" + hashCodeHex.charAt(2) + "/" + hashCodeHex.charAt(3) + "/" + fileName);
		}
		return imageUrls;
	}
}
