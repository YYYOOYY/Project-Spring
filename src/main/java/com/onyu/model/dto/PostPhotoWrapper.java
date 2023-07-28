package com.onyu.model.dto;

import com.onyu.model.entity.PostPhoto;

import lombok.Data;

@Data
public class PostPhotoWrapper {
	private String type;
	private String imageUrl;
	
	public PostPhotoWrapper(PostPhoto entity) {
		this.type = entity.getType();
		this.imageUrl = entity.getImageUrl();
	}
}
