package com.luv2code.springsecurity.demo.service;

import com.luv2code.springsecurity.demo.entity.Users;
import com.luv2code.springsecurity.demo.user.CrmUser;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

	public Users findByUserName(String userName);

	public void save(CrmUser crmUser);
}
