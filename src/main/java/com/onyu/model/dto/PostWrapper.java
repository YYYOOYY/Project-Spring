package com.onyu.model.dto;

import java.text.SimpleDateFormat;
import java.util.List;

import com.onyu.model.entity.Post;

import lombok.Data;

@Data
public class PostWrapper {

	private Long id;
	
	private UserWrapper writer;
	
	private String title;
	private String content;
	private String writed;
	private Integer price;
	private String cate;
	private String city;
	private boolean status;
	private Long viewCount;
	private Long interestedCount;
	private long chatRoomCnt;
	
	private List<PostPhotoWrapper> photos;
	
	public PostWrapper(Post entity) {
		this.id = entity.getId();
		this.title = entity.getTitle();
		this.content = entity.getContent();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String format = sdf.format(entity.getWrited());
		this.city = entity.getCity();
		this.writer = new UserWrapper(entity.getWriter());
		this.writed = format;
		this.price = entity.getPrice();
		this.cate = entity.getCate();
		this.status = entity.isStatus();
		this.viewCount = entity.getViewCount();
		this.interestedCount = entity.getInterestedCount();
		
		this.photos = entity.getPhotos().stream().map(photo -> new PostPhotoWrapper(photo)).toList();
	}
}
