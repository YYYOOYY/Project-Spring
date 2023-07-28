package com.onyu.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.onyu.model.entity.Post;
import com.onyu.model.entity.User;

public interface PostRepository extends JpaRepository<Post, Long>{
	
	List<Post> findAllByWriter(User user); 
	Page<Post> findAllByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}
