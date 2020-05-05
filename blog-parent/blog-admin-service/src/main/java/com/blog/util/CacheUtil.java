package com.blog.util;
/**
 * 
 * ClassName: CacheUtil <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2018年6月14日 下午1:52:36 <br/>
 *
 * @author Administrator
 * @version 
 * @since JDK 1.6
 */
public class CacheUtil {
 
	/**
	 * 是否开启了缓存服务器
	 * 
	 * @return
	 */
	public static boolean isCacheEnabled() {
		try{
			ExtendedPropertyPlaceholderConfigurer pmg = (ExtendedPropertyPlaceholderConfigurer) SpringUtil.context
					.getBean("propertyConfigurer");
			// 1 启用cache ， 其它：不启用
			String cacheEnabledStr= (String) pmg.getProperty("redis.CacheEnabled");
			if ("1".equals(cacheEnabledStr)) {
				return true;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}

	
	public static RedisCache getCache()
	{
		RedisCache  redisCache= (RedisCache) SpringUtil.context.getBean("redisCache");
		return redisCache;
	}
}
