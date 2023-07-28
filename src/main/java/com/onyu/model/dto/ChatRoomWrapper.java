package com.onyu.model.dto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.onyu.model.entity.ChatRoom;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoomWrapper {
	
	private Long id;
	private String created;
	private PostWrapper post;
	private List<ChattingWrapper> chattings;
	private UserWrapper buyer;
	private UserWrapper seller;
	private Integer unreadCnt;
	
	public ChatRoomWrapper(ChatRoom entity) {
		this.id = entity.getId();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String format = sdf.format(entity.getCreated());
		this.created = format;
		this.post = new PostWrapper(entity.getPostChatId());
		List<ChattingWrapper> list;
		if(entity.getChattings() == null || entity.getChattings().isEmpty()) {
			list = new ArrayList<>();
		}else {
			list = entity.getChattings().stream().map(e -> new ChattingWrapper(e)).toList();
		}
		this.chattings = list;
		this.buyer = new UserWrapper(entity.getBuyer());
		this.seller = new UserWrapper(entity.getSeller());
	}
}
