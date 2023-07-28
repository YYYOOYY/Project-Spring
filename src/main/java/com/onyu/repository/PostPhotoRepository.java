package com.onyu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.onyu.model.entity.Post;
import com.onyu.model.entity.PostPhoto;

public interface PostPhotoRepository extends JpaRepository<PostPhoto, Long> {
	
	void deleteAllByPost(Post post);
}
