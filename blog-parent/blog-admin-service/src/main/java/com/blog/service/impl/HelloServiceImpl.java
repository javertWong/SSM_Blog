package com.blog.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.blog.service.HelloService;

@Service
public class HelloServiceImpl implements HelloService {

	@Override
	public String hello() {
		return "hello Javert";
	}

}
