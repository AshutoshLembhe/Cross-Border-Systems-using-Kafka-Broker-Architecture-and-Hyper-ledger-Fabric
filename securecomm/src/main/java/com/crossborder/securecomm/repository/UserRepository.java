package com.crossborder.securecomm.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.crossborder.securecomm.model.User;

public interface UserRepository extends MongoRepository<User, String> {

}
