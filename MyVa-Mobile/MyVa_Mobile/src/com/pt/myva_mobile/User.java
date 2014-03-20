package com.pt.myva_mobile;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class User implements Serializable{

	private int id;
	private String username;
	private String email;
	private String password;
	private Date birthdate;

	public User() {
	}

	public User(String username, String email, String password, Date birthdate) {
		this.id = 0;
		this.setUsername(username);
		this.setEmail(email);
		this.setPassword(password);
		this.setBirthdate(birthdate);
	}

	String getUsername() {
		return username;
	}

	void setUsername(String username) {
		this.username = username;
	}

	String getEmail() {
		return email;
	}

	void setEmail(String email) {
		this.email = email;
	}

	String getPassword() {
		return password;
	}

	void setPassword(String password) {
		this.password = password;
	}

	Date getBirthdate() {
		return birthdate;
	}

	void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	int getId() {
		return id;
	}

	void setId(int id) {
		this.id = id;
	}
}
