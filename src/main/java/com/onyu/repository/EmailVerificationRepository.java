package com.onyu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.onyu.model.entity.EmailVerification;
import com.onyu.model.entity.User;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
	
	Optional<EmailVerification> findByEmail(String email);
	
	void deleteByUser(User user);
}
