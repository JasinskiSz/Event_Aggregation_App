package com.sda.eventapp.repository;

import com.sda.eventapp.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}