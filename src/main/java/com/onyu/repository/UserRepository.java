package com.onyu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.onyu.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByLoginId(String loginId);
	
	boolean existsByLoginId(String loginId);
}
