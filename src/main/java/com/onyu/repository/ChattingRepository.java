package com.onyu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.onyu.model.entity.ChatRoom;
import com.onyu.model.entity.Chatting;

public interface ChattingRepository extends JpaRepository<Chatting, Long> {
	
	List<Chatting> findAllByChatRoom(ChatRoom chatRoom);
	void deleteAllByChatRoom(ChatRoom chatRoom);
}