package com.blog.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.blog.utils.FastDFSClient;

import entity.Result;

@RestController
public class UploadController {
	
	@Value("${FILE_SERVER_URL}")
	private String file_server_url;
	
	@RequestMapping("/upload")
	public Result upload(MultipartFile file){
		String originalFilename =  file.getOriginalFilename();//获取文件名
		String extName=originalFilename.substring(originalFilename.lastIndexOf(".")+1);
		try {
			FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
			String fileId =  client.uploadFile(file.getBytes(), extName);
			String url = file_server_url+fileId;
			return new Result(true, url);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "上传失败");
		}
	}
	@RequestMapping("/kindEditorUpload")
	public Map kindEditorUpload(MultipartFile file){
		Map<String, Object> map = new HashMap<>();
		String originalFilename =  file.getOriginalFilename();//获取文件名
		String extName=originalFilename.substring(originalFilename.lastIndexOf(".")+1);
		try {
			FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
			String fileId =  client.uploadFile(file.getBytes(), extName);
			String url = file_server_url+fileId;
			map.put("error", 0);//这里太坑了，无法直接用String,因为前台写的是data.error===0
			map.put("url", url);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			map.put("error", "1");
			map.put("message", "上传失败");
			return map;
		}
	}
}
