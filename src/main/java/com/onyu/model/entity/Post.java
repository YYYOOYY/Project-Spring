package com.onyu.model.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "posts")
@NoArgsConstructor
@AllArgsConstructor
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	// 제목
	private String title;
	// 작성일
	private Date writed;
	// 내용
	private String content;
	// 가격
	private Integer price;
	// 카테고리
	private String cate;
	// 게시물 상태
	private boolean status = false;
	// 조회수
	private Long viewCount;
	// 관심수
	private Long interestedCount;
	// 지역
	private String city;
	// 사진들
	@OneToMany(mappedBy = "post")
	private List<PostPhoto> photos;
	// 글쓴이 정보
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "writer", referencedColumnName = "loginId")
	private User writer;
	
	@OneToMany(mappedBy = "itrstPost")
	private List<Interest> interests;
	
	@OneToMany(mappedBy = "postChatId")
	private List<ChatRoom> chatRooms;
}
