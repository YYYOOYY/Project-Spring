package com.onyu.model.dto.response;

import java.util.List;

import com.onyu.model.dto.PostWrapper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostListResponse {
	private Long total;
	private List<PostWrapper> posts;
}
