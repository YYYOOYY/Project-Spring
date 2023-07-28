package com.onyu.model.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(unique = true)
	private String loginId;
	private String loginPassword;
	private String nickname;
	private Date joinde;
	
	private String profileImage;
	
	@OneToOne(mappedBy = "user")
	private EmailVerification email;
	
	@OneToMany(mappedBy = "writer")
    private List<Post> posts;
	
    @OneToMany(mappedBy = "itrstUser")
    private List<Interest> interests;
	
    @OneToMany(mappedBy = "chatWriter")
    private List<Chatting> chattings;
    
    @OneToMany(mappedBy = "buyer")
    private List<ChatRoom> ChatBuyer;
    
    @OneToMany(mappedBy = "seller")
    private List<ChatRoom> ChatSeller;
}
