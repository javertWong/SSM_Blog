package com.blog.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringUtil implements ApplicationContextAware{

	public static ApplicationContext context;
	
	public static ApplicationContext getApplicationContext(){
		return context;
	}
	/**
	 * 加载Spring配置文件时，
	 * 如果Spring配置文件中所定义的Bean类实现了ApplicationContextAware 接口，
	 * 那么在加载Spring配置文件时，会自动调用
	 */
	@SuppressWarnings("static-access")
	@Override
	public void setApplicationContext(ApplicationContext contex) throws BeansException {
		this.context=contex;
		
	}

	public static void setContext(ApplicationContext context) {
		SpringUtil.context = context;
	}
	
	//通过name获取 Bean.
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }
	
}

