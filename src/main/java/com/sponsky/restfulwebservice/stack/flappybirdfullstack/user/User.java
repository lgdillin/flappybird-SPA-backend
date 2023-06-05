package com.sponsky.restfulwebservice.stack.flappybirdfullstack.user;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	@Id @GeneratedValue
	private Integer id;
	
	@Lob
	private byte[] imageData;
	
	private String username;
	private String password;
	private String roles;
	
	private LocalDate date;
	private Integer score; 
	
	public User() {}
	
	public User(String username, String password, String roles) {
		super();
		this.username = username;
		this.password = password;
		this.roles = roles;
	}
	
	public User(Integer id, String username, Integer score) {
		super();
		this.id = id;
		this.username = username;
		this.score = score;
		this.imageData = null;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Integer getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public Integer getScore() {
		return score;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}
	
	private String printImageString() {
		try {
			return new String(imageData, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + username 
				+ ", score=" + score + ", image exists?: " 
				+ (imageData != null ?  printImageString() : "no") + ", date: " + (date != null ?  date.toString() : "N/A") + "]";
	}
}
