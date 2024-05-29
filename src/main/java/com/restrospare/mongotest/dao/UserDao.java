package com.restrospare.mongotest.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.restrospare.mongotest.dto.User;
import com.restrospare.mongotest.repo.UserRepo;

@Repository
public class UserDao {
	@Autowired
	private UserRepo repo;

	public User saveUser(User user) {
		return repo.save(user);
	}

}
