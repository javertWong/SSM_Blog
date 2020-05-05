package com.blog.service;

import java.util.List;

import com.blog.pojo.Blog;

import entity.PageResult;

public interface BlogService {
	/**
	 * 返回全部列表
	 * @return
	 */
	public List<Blog> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(Blog blog);
	
	
	/**
	 * 修改
	 */
	public void update(Blog blog);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public Blog findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(Blog blog, int pageNum,int pageSize);
}
