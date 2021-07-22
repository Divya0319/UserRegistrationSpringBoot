package com.luv2code.springsecurity.demo.dao;

import com.luv2code.springsecurity.demo.entity.Users;

public interface UserDao {

    public Users findByUserName(String userName);
    
    public void save(Users user);
    
}
