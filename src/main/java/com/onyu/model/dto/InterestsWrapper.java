package com.onyu.model.dto;

import com.onyu.model.entity.Interest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InterestsWrapper {
	
	private Long id;
	
	private UserWrapper user;
	private PostWrapper post;
	
	public InterestsWrapper(Interest entity) {
		this.id = entity.getId();
		this.user = new UserWrapper(entity.getItrstUser());
		this.post = new PostWrapper(entity.getItrstPost());
	}
}
