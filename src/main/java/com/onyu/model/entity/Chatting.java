package com.onyu.model.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "chattings")
public class Chatting {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String message;
	private Date writed;
	private boolean status = false;
	
	@ManyToOne
	@JoinColumn(name = "chatRoom")
	private ChatRoom chatRoom;
	
	@ManyToOne
	@JoinColumn(name = "chatWriter", referencedColumnName = "loginId")
	private User chatWriter;
}
