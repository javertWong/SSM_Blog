package com.blog.controller;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.blog.pojo.Admin;
import com.blog.service.AdminService;
import com.blog.utils.StringUtil;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("admin")
public class AdminController {
	
	@Reference
	private AdminService adminService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<Admin> findAll(){			
		return adminService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return adminService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param admin
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Admin admin){
		//密码加密
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		if(StringUtil.isEmpty(admin.getAccount())||StringUtil.isEmpty(admin.getPassword()))return null;
		String encode=passwordEncoder.encode(admin.getPassword());
		admin.setPassword(encode);
		try {
			adminService.add(admin);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param admin
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Admin admin){
		try {
			adminService.update(admin);
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
	public Admin findOne(Long id){
		return adminService.findOne(id);		
	}
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOneByAccount")
	public Admin findOne(String account){
		return adminService.findOne(account);		
	}
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			adminService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param admin
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody Admin admin, int page, int rows  ){
		return adminService.findPage(admin, page, rows);		
	}
	
	
}
