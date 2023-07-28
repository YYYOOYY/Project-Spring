package com.onyu.model.dto;

import java.text.SimpleDateFormat;

import com.onyu.model.entity.ChatRoom;
import com.onyu.model.entity.Chatting;
import com.onyu.model.entity.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChattingWrapper {
	
	private Long id;
	private String message;
	private String writed;
	private Long roomId;
	private UserWrapper chatWriter;
	private boolean status;
	
	public ChattingWrapper(Chatting entity) {
		this.id = entity.getId();
		this.message = entity.getMessage();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String format = sdf.format(entity.getWrited());
		this.writed = format;
		this.roomId = entity.getChatRoom().getId();
		this.chatWriter = new UserWrapper(entity.getChatWriter());
		this.status = entity.isStatus();
	}
}
