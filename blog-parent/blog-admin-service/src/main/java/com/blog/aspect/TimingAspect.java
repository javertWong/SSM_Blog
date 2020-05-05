package com.blog.aspect;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entity.ManagerMethodUseLog;


/**
 * 用于记录service层方法的执行时间，并记录到数据库，后期可改为发送到消息服务器。
 * @author javertWong
 */
public class TimingAspect {
	static Logger logger = LoggerFactory.getLogger(TimingAspect.class);
	/**
	 * @description:获取ip地址
	 * @author javertWong
	 * @date: 2020-10-20
	 */
	public static List<String> getLocalIpList() {
		List<String> ipList = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaces.nextElement();
				Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					InetAddress inetAddress = inetAddresses.nextElement();
					if (inetAddress != null && inetAddress instanceof Inet4Address) { // IPV4
						ipList.add(inetAddress.getHostAddress());
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return ipList;
	}

	private static String localIps = null;

	/**
	 * 获取ip地址
	 */
	public static String getLocalIps() {
		if (localIps == null) {
			List<String> ipList = getLocalIpList();
			for (String ip : ipList) {
				if (!"127.0.0.1".equals(ip)) {
					if (localIps == null) {
						localIps = ip;
					} else {
						localIps += (";" + ip);
					}
				}
			}
		}
		return localIps;
	}

	public static LinkedBlockingQueue<ManagerMethodUseLog> mmulQueue = null;

	public TimingAspect() {
		if (mmulQueue == null) {
			mmulQueue = new LinkedBlockingQueue<ManagerMethodUseLog>();
			Executors.newSingleThreadExecutor().execute(saveTimingDataTask);
		}
	}

	/**
	 * @description:aop doAround
	 * @author javertWong
	 * @date: 2020-10-20
	 */
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {

		long startTime = 0;
		String methodName = "";
		try {
			startTime = System.currentTimeMillis();
			methodName = getMethod(joinPoint).toString();
			Object obj = joinPoint.proceed();
			return obj;
		} finally {
			try {
				long endTime = System.currentTimeMillis();
				ManagerMethodUseLog ml = new ManagerMethodUseLog();
				ml.setManagerMethodName(methodName);
				ml.setStartDate(Calendar.getInstance().getTime());
				ml.setLastTime(endTime - startTime);
				ml.setIp(getLocalIps());
				// 超过7S的方法，记录下来
				if (ml.getLastTime() >= 7000) {
					mmulQueue.put(ml);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	String mmulCols[] = { "id", "manager_method_name", "start_date", "last_time", "ip" };
	String mmulSql = "INSERT INTO manager_method_use_log(id,manager_method_name,start_date,last_time,ip) VALUES(:id,:manager_method_name,:start_date,:last_time,:ip)";
	// 将队列中的数据保存进数据库
	Runnable saveTimingDataTask = new Runnable() {
		@Override
		public void run() {
			try {
				ArrayList<Map<String, Object>> ars = new ArrayList<Map<String, Object>>();
				int i = 0;
				ManagerMethodUseLog data = null;
				while ((data = mmulQueue.take()) != null && i <= 10) {
					i++;
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put(mmulCols[1], data.getManagerMethodName());
					paramMap.put(mmulCols[2], data.getStartDate());
					paramMap.put(mmulCols[3], data.getLastTime());
					paramMap.put(mmulCols[4], data.getIp());
					ars.add(paramMap);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				this.run();
			}
		}
	};

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
}
