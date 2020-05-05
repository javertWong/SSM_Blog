package com.blog.util;

import com.blog.utils.SerializeUtil;

import redis.clients.jedis.*;

public class RedisCache {
    
    private JedisPool jedisPool;
    
    public JedisPool getJedisPool() {
        return jedisPool;
    }
    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    //从redis缓存中查询，反序列化
    public Object getDataFromRedis(String redisKey){
        //查询
        Jedis jedis = jedisPool.getResource();
        byte[] result = jedis.get(redisKey.getBytes());
        jedis.close();
        //如果查询没有为空
        if(null == result){
            return null;
        }
        //查询到了，反序列化
        return SerializeUtil.unSerialize(result);
    }
    
    //将数据库中查询到的数据放入redis
    public void setDataToRedis(String redisKey, int ttl, Object obj){
        
        //序列化
        byte[] bytes = SerializeUtil.serialize(obj);
        //对象超过50M，则放弃进缓存，避免redis中单对象过大
        if (bytes.length>50*1024*1024)
        	return;
        
        //存入redis
        Jedis jedis = jedisPool.getResource();
        //String success = jedis.set(redisKey.getBytes(), SerializeUtil.bytesToHexString(bytes).getBytes());
        //ttl单位为分钟，转换为秒
        jedis.setex(redisKey.getBytes(),ttl * 60 ,  bytes);
//        if("OK".equals(success)){
//            System.out.println("数据成功保存到redis...");
//        }
        jedis.close();
    }
   //清除缓存
    public Object removeObject(Object arg0) {
        // TODO Auto-generated method stub
        Jedis jedis = jedisPool.getResource();
        Object expire = jedis.expire(
                SerializeUtil.serialize(arg0.toString()), 0);
        jedis.close();
        return expire;
    }
	
}