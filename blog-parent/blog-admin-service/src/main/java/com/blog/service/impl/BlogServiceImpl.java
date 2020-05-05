package com.blog.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.blog.mapper.BlogMapper;
import com.blog.pojo.Blog;
import com.blog.pojo.BlogExample;
import com.blog.pojo.BlogExample.Criteria;
import com.blog.service.AdminService;
import com.blog.service.BlogService;
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
public class BlogServiceImpl implements BlogService {

	@Autowired
	private BlogMapper blogMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<Blog> findAll() {
		return blogMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<Blog> page = (Page<Blog>) blogMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Blog Blog) {
		blogMapper.insert(Blog);
	}

	/**
	 * 修改
	 */
	@Override
	public void update(Blog Blog) {
		blogMapper.updateByPrimaryKey(Blog);
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Blog findOne(Long id) {
		return blogMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			blogMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(Blog blog, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		BlogExample example = new BlogExample();
		Criteria criteria = example.createCriteria();

		if (blog != null) {

			if (blog.getId() != null) {
				criteria.andIdEqualTo(blog.getId());
			}
			if (blog.getTitle() != null && blog.getTitle().length() > 0) {
				criteria.andTitleLike("%" + blog.getTitle() + "%");
			}

		}

		Page<Blog> page = (Page<Blog>) blogMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}


}
