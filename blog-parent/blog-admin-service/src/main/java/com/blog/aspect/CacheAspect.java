package com.blog.aspect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blog.annotation.CacheControl;
import com.blog.util.CacheUtil;
import com.blog.utils.SerializeUtil;

public class CacheAspect {
	static Logger logger = LoggerFactory.getLogger("CacheAspect");
	// 记录存储method文件的上次修改时间
	private static long fileLastModified = 0;
	// private static CacheMethodThread methodThread = null;
	// 用于保存走缓存的方法集合
	private static Set<String> cachedMethodList = new HashSet<String>();
	// 2分钟读取一次文件
	private long interval = 2;

	Runnable ra = new Runnable() {
		@Override
		public void run() {
			loadCachedMethod();
		}
	};

	/**
	 * 构造是，启动线程方法读取
	 */
	public CacheAspect() {

		// 每2分钟执行一次
		new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(ra, 0, 60 * interval, TimeUnit.SECONDS);
	}

	public Object doAround(ProceedingJoinPoint jp) throws Throwable {
		// 全局控制，不使用Cache
		if (!CacheUtil.isCacheEnabled()) {
			return jp.proceed();
		}
		Method method = getMethod(jp);
		boolean hasAnnotation = method.isAnnotationPresent(CacheControl.class);
		// 有CacheControl注解或配置文件 才考虑缓存操作
		// if (!hasAnnotation) {
		// logger.info(" 判断方法的缓存走向...."+method.toString());
		// }
		if (hasAnnotation) {
			return hasCacheControlAnnotation(jp, method);
		} else if (cachedMethodList.contains(method.toString())) {
			String redisKey = makeKey(jp);
			return getDataFromCache(jp, 3, redisKey);
		} else {
			// logger.info("方法：----method.toString()......"+method.toString()+"....走数据库......");
			return jp.proceed(); // 无注解，则继续处理
		}

	}

	/**
	 * getMethod:得到方法的原型
	 */
	private Method getMethod(ProceedingJoinPoint jp) throws NoSuchMethodException {
		String methodName = jp.getSignature().getName();
		Class<? extends Object> targetClass = jp.getTarget().getClass();
		Method method = null;
		MethodSignature signature = (MethodSignature) jp.getSignature();
		method = signature.getMethod();
		method = targetClass.getMethod(methodName, method.getParameterTypes());
		return method;
	}

	private Object hasCacheControlAnnotation(ProceedingJoinPoint jp, Method method) throws Throwable {
		int ttl;
		CacheControl annotation = method.getAnnotation(CacheControl.class);
		// 注解为不缓存，则不拦截，继续执行
		if (!annotation.cachable()) {
			// logger.info("方法：----method.toString()......"+method.toString()+"....走数据库......");
			return jp.proceed();
		}
		ttl = annotation.ttl();
		// logger.info("调用方法: "+method.toString());
		// 构造缓存的KEY关键字[方法原型+参数值]
		String redisKey = makeKey(jp);
		// 从缓存中提取数据
		// logger.info("方法：----method.toString()......"+method.toString()+"....走缓存......");
		return getDataFromCache(jp, ttl, redisKey);
	}

	/**
	 * getDataFromCache:从缓存中获取数据
	 * @author javertWong
	 * @param jp
	 * @param ttl
	 * @param redisKey
	 * @return
	 * @throws Throwable
	 */
	private Object getDataFromCache(ProceedingJoinPoint jp, int ttl, String redisKey) throws Throwable {
		// 从缓存中查询
		Object cachedObject = null;
		try {
			// 解决缓存服务器出问题，则返回空，使得后续从数据库查询
			// logger.info("method.toString()......"+
			// jp.getSignature().getName()+"....走缓存......");
			cachedObject = CacheUtil.getCache().getDataFromRedis(redisKey);
		} catch (Exception ex) {
			ex.printStackTrace();
			cachedObject = null;
		}
		// 如果非空，则返回缓存中对象
		if (null != cachedObject) {
			return cachedObject;
		} else {
			Object obj = jp.proceed(); // 处理方法调用
			try {
				// 解决缓存服务器出问题，则返回空，使得后续从数据库查询
				CacheUtil.getCache().setDataToRedis(redisKey, ttl, obj);
			} catch (Exception ex) {
				ex.printStackTrace();
				cachedObject = null;
			}
			return obj;
		}
	}

	/**
	 * 生成缓存需要的KEY，格式如：全路径类名.方法名.参数1.参数2.参数3.....
	 * @param jp
	 * @return
	 */
	private String makeKey(ProceedingJoinPoint jp) {
		Method method = null;
		String sKey = "";
		try {
			MethodSignature signature = (MethodSignature) jp.getSignature();
			method = signature.getMethod();
			method = jp.getTarget().getClass().getMethod(jp.getSignature().getName(), method.getParameterTypes());
			// 得到参数列表
			Object[] paraObj = jp.getArgs();
			String paraPart = "////";
			for (int i = 0; i < method.getParameterTypes().length; i++) {
				if (null != paraObj[i]) {
					paraPart += paraObj[i].getClass().getCanonicalName() + "=" + SerializeUtil.bytesToHexString(SerializeUtil.serialize(paraObj[i]));
				}
			}
			sKey = method.toString() + "-" + paraPart;

			// logger.info("redis Key:"+sKey);
		} catch (Exception e) {
			e.printStackTrace();
			sKey = method.toString();
		}
		return String.valueOf(sKey.replaceAll("\\s{1,}", "-"));
	}

	public static String str2HexStr(String origin) {
		byte[] bytes = origin.getBytes();
		String hex = bytesToHexString(bytes);
		return hex;
	}

	private static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 加载文件中配置的走缓存的方法
	 */
	private void loadCachedMethod() {
		try {
			// logger.info("----从cachedmethod.properties读取缓存方法");
			URL url = this.getClass().getResource("/properties/cachedmethod.properties");
			if (url == null) {
				return;
			}
			String fileName = url.getPath();
			File file = new File(fileName);
			long lastModified = file.lastModified();
			// 文件未修改，则不读取文件
			if (lastModified <= fileLastModified) {
				return;
			}
			fileLastModified = file.lastModified();
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			cachedMethodList.clear();
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				cachedMethodList.add(line);
			}
			if (br != null) {
				br.close();
			}
			for (String cachedMethod : cachedMethodList) {
				// logger.info("----配置的需要从缓存读数据的方法："+cachedMethod);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			// logger.error("---cachedmethod.properties文件不在或内容不正确，原因："+ex.getMessage());
		}
	}

}