package org.jsp.jsp_gram.dto;

import java.util.List;
import java.util.ArrayList;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Size(min = 3, max = 10, message = "It should be between 3 and 10 charecters")
	private String firstname;
	@Size(min = 1, max = 15, message = "It should be between 1 and 15 charecters")
	private String lastname;
	@Size(min = 5, max = 15, message = "It should be between 5 and 15 charecters")
	private String username;
	@Email(message = "It should be proper Email format")
	@NotEmpty(message = "It is required Field")
	private String email;
	@DecimalMin(value = "6000000000", message = "It should be proper mobile number")
	@DecimalMax(value = "9999999999", message = "It should be proper mobile number")
	private long mobile;
	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "It should contain atleast 8 charecter, one uppercase, one lowercase, one number and one speacial charecter")
	private String password;
	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "It should contain atleast 8 charecter, one uppercase, one lowercase, one number and one speacial charecter")
	@Transient
	private String confirmpassword;
	@NotNull(message = "It is required Field")
	private String gender;
	private int otp;
	private boolean verified;
	
	private String bio;
	private String imageUrl;
	
	@ManyToMany(fetch = FetchType.EAGER)
	List<User> followers = new ArrayList<User>();
	@ManyToMany(fetch = FetchType.EAGER)
	List<User> following = new ArrayList<User>();


	
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public long getMobile() {
		return mobile;
	}
	public void setMobile(long mobile) {
		this.mobile = mobile;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmpassword() {
		return confirmpassword;
	}
	public void setConfirmpassword(String confirmpassword) {
		this.confirmpassword = confirmpassword;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getOtp() {
		return otp;
	}
	public void setOtp(int otp) {
		this.otp = otp;
	}
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	public List<User> getFollowers() {
		return followers;
	}
	public void setFollowers(List<User> followers) {
		this.followers = followers;
	}
	public List<User> getFollowing() {
		return following;
	}
	public void setFollowing(List<User> following) {
		this.following = following;
	}
	 
	 
}
