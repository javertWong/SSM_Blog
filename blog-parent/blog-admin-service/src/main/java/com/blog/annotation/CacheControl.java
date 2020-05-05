package com.blog.annotation;
import java.lang.annotation.Documented;  

import java.lang.annotation.ElementType;  
import java.lang.annotation.Inherited;  
import java.lang.annotation.Retention;  
import java.lang.annotation.RetentionPolicy;  
import java.lang.annotation.Target;  
/**
 * Reids缓存注解
 * @author javertWong
 * @date 2020-05-03 09:34:12
 */
@Target(ElementType.METHOD)     
@Retention(RetentionPolicy.RUNTIME)     
@Documented    
@Inherited   
public @interface CacheControl {  
    public String description() default "no description";  
    public boolean cachable() default  false;
    public int ttl()  default 10;  //默认的过期时间10分钟
    
}  
