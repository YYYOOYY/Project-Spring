package com.onyu.model.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "chatRooms")
public class ChatRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Date created;
	
	@ManyToOne
	@JoinColumn(name = "post")
	private Post postChatId;	

	@OneToMany(mappedBy = "chatRoom")
	private List<Chatting> chattings;
	
	@ManyToOne
	@JoinColumn(name = "buyer")
	private User buyer;
	
	@ManyToOne
	@JoinColumn(name = "seller")
	private User seller;
}
