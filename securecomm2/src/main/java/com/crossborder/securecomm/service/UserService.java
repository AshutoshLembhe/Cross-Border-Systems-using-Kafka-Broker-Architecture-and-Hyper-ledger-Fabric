package com.crossborder.securecomm.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crossborder.securecomm.model.User;
import com.crossborder.securecomm.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;
	
	public UserService(UserRepository userRepository) {
		this.userRepository=userRepository;
	}
	
	public User save(User user) {
		return userRepository.save(user);
	}
	
	public List<User> findAll(){
		return userRepository.findAll();	
	}
	
}
