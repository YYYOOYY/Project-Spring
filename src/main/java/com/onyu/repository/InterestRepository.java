package com.onyu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.onyu.model.entity.Interest;
import com.onyu.model.entity.Post;
import com.onyu.model.entity.User;

public interface InterestRepository extends JpaRepository<Interest, Long> {
	
	List<Interest> findAllByItrstUser(User user);
	long countByItrstPost(Post post);
	boolean existsByItrstPostAndItrstUser(Post post, User user);
	void deleteByItrstPostAndItrstUser(Post post, User user);
	void deleteAllByItrstPost(Post post);
}
