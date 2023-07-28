package com.onyu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.onyu.model.entity.ChatRoom;
import com.onyu.model.entity.Post;
import com.onyu.model.entity.User;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>{

	boolean existsBySellerAndBuyerAndPostChatId(User seller, User buyer, Post post);
	ChatRoom findBySellerAndBuyerAndPostChatId(User seller, User buyer, Post post);
	List<ChatRoom> findBySellerOrBuyerOrderByCreatedAsc(User seller, User buyer);
	long countBypostChatId(Post post);
	List<ChatRoom> findAllByPostChatId(Post post);
	void deleteAllByPostChatId(Post post);
}
