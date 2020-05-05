package com.blog.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.blog.service.HelloService;

@RestController
public class HelloController {
	
	@Reference
	HelloService helloService;
	
	@RequestMapping("hello")
	public String hello(){
		return helloService.hello();
	}
}
