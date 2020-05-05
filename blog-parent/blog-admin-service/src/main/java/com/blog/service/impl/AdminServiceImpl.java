package com.blog.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.blog.annotation.CacheControl;
import com.blog.mapper.AdminMapper;
import com.blog.pojo.Admin;
import com.blog.pojo.AdminExample;
import com.blog.pojo.AdminExample.Criteria;
import com.blog.service.AdminService;
import com.blog.util.CacheUtil;
import com.blog.util.RedisCache;
import com.blog.utils.StringUtil;

import entity.PageResult;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private AdminMapper adminMapper;
	@Autowired
	RedisCache redisCache;

	/**
	 * 查询全部
	 */
	@Override
	@CacheControl(cachable=true,ttl=5)
	public List<Admin> findAll() {
		return adminMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		System.out.println(CacheUtil.isCacheEnabled()+"-----------------");
		PageResult pageResult = (PageResult) redisCache.getDataFromRedis("zzz");
		if(null!=pageResult)return pageResult;
		Page<Admin> page = (Page<Admin>) adminMapper.selectByExample(null);
		PageResult result =new PageResult(page.getTotal(), page.getResult());
		redisCache.setDataToRedis("zzz", 10, result);
		return result;
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Admin admin) {
		adminMapper.insert(admin);
	}

	/**
	 * 修改
	 */
	@Override
	public void update(Admin admin) {
		adminMapper.updateByPrimaryKey(admin);
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Admin findOne(Long id) {
		return adminMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			adminMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(Admin admin, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		AdminExample example = new AdminExample();
		Criteria criteria = example.createCriteria();

		if (admin != null) {

			if (admin.getRealName() != null && admin.getRealName().length() > 0) {
				criteria.andPasswordLike("%" + admin.getPassword() + "%");
			}
			if (admin.getEmail() != null && admin.getEmail().length() > 0) {
				criteria.andEmailLike("%" + admin.getEmail() + "%");
			}

		}

		Page<Admin> page = (Page<Admin>) adminMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public Admin findOne(String account) {
		//if(StringUtil.isEmpty(account))return null;
		return adminMapper.selectByAccount(account);
	}

}
