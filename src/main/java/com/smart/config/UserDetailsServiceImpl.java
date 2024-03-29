package com.smart.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.User;
import com.smart.Dao.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	 private UserRepository userRepository;
	/*
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// fetching user from database
		User user = userRepository.getUserByUserName(username);
		if(user==null)
		{
			throw new UsernameNotFoundException("Could not found username");
		}
		
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		return customUserDetails;
	}
	*/

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.getUserByUserName(username);
		if(user==null)
		{
			throw new UsernameNotFoundException("Could not found username");
		}
		
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		return customUserDetails;
	}

	
}
