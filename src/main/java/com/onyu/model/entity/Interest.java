package com.onyu.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "Interests", uniqueConstraints = @UniqueConstraint(columnNames = {"user", "post"}))
@AllArgsConstructor
@NoArgsConstructor
public class Interest {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user", referencedColumnName = "loginId")
	private User itrstUser;

	@ManyToOne
	@JoinColumn(name = "post")
	private Post itrstPost;
}
