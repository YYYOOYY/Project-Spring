package com.onyu.model.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "emailVerifications")
public class EmailVerification {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String email;

	private String code;
	private Date created;
	
	private int count;

	@OneToOne
	@JoinColumn(name = "userId", referencedColumnName = "loginId")
	private User user;

	private boolean status = false;
}
