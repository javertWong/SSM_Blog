package com.blog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.blog.pojo.Admin;

public class UserDetailsServiceImpl implements UserDetailsService {

	private AdminService adminService;

	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		System.out.println("经过了UserDetailsServiceImpl");
		//构建角色列表
		List<GrantedAuthority> grantAuths= new ArrayList();
		grantAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		
		Admin admin =  adminService.findOne(username);
		System.out.println(admin.getPassword());
		if(admin!=null){
			return new User(username,admin.getPassword(), grantAuths);
		}else{
			return null;
		}
		
	}

}
