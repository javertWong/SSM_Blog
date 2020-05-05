package com.blog.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.blog.pojo.Blog;
import com.blog.service.BlogService;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("blog")
public class BlogController {
	
	@Reference
	private BlogService blogService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<Blog> findAll(){			
		return blogService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return blogService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param blog
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Blog blog){
		try {
			blogService.add(blog);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param blog
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Blog blog){
		try {
			blogService.update(blog);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Blog findOne(Long id){
		return blogService.findOne(id);		
	}
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			blogService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param blog
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody Blog blog, int page, int rows  ){
		return blogService.findPage(blog, page, rows);		
	}
	
	
}
