package com.restrospare.mongotest.repo;



import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.restrospare.mongotest.dto.User;

@Repository
public interface UserRepo extends MongoRepository<User, String>{

	
	

}
