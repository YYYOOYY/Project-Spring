package com.onyu.model.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class PostRequest {
	private String title;
	private String content;
	private Integer price;
	private String cate;
	private String city;
	private List<MultipartFile> photos;
}
