package org.jsp.jsp_gram.service;

import java.util.Random;
import java.util.ArrayList;

import java.util.List;
import org.jsp.jsp_gram.repository.PostRepository;
import org.jsp.jsp_gram.dto.Post;
import org.jsp.jsp_gram.helper.CloudinaryHelper;
import org.jsp.jsp_gram.dto.User;
import org.jsp.jsp_gram.helper.AES;
import org.jsp.jsp_gram.helper.EmailSender;
import org.jsp.jsp_gram.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

@Service
public class UserService {

	@Autowired
	UserRepository repository;
	
	
	@Autowired
	PostRepository postRepository;

	@Autowired
	EmailSender emailSender;
	
	@Autowired
	CloudinaryHelper cloudinaryHelper;
	
	public String loadRegister(ModelMap map, User user) {
		map.put("user", user);
		return "register.html";
	}

	public String register(User user, BindingResult result, HttpSession session) {
		if (!user.getPassword().equals(user.getConfirmpassword()))
			result.rejectValue("confirmpassword", "error.confirmpassword", "Passwords not Matching");

		if (repository.existsByEmail(user.getEmail()))
			result.rejectValue("email", "error.email", "Email already Exists");

		if (repository.existsByMobile(user.getMobile()))
			result.rejectValue("mobile", "error.mobile", "Mobile Number Already Exists");

		if (repository.existsByUsername(user.getUsername()))
			result.rejectValue("username", "error.username", "Username already Taken");

		if (result.hasErrors())
			return "register.html";
		else {
			user.setPassword(AES.encrypt(user.getPassword()));
			int otp = new Random().nextInt(100000, 1000000);
			user.setOtp(otp);
			System.err.println(otp);
			// emailSender.sendOtp(user.getEmail(), otp, user.getFirstname());
			repository.save(user);
			session.setAttribute("pass", "Otp Sent Success");
			return "redirect:/otp/" + user.getId();
		}
	}

	public String verifyOtp(int otp, int id, HttpSession session) {
		User user = repository.findById(id).get();
		if (user.getOtp() == otp) {
			user.setVerified(true);
			user.setOtp(0);
			repository.save(user);
			session.setAttribute("pass", "Account Created Success");
			return "redirect:/login";
		} else {
			session.setAttribute("fail", "Invalid Otp, Try Again!!!");
			return "redirect:/otp/" + id;
		}

	}

	public String resendOtp(int id, HttpSession session) {
		User user = repository.findById(id).get();
		int otp = new Random().nextInt(100000, 1000000);
		user.setOtp(otp);
		System.err.println(otp);
		// emailSender.sendOtp(user.getEmail(), otp, user.getFirstname());
		repository.save(user);
		session.setAttribute("pass", "Otp Re-Sent Success");
		return "redirect:/otp/" + user.getId();
	}

	public String login(String username, String password, HttpSession session) {
		User user = repository.findByUsername(username);
		if (user == null) {
			session.setAttribute("fail", "Invalid Username");
			return "redirect:/login";
		} else {
			if (AES.decrypt(user.getPassword()).equals(password)) {
				if (user.isVerified()) {
					session.setAttribute("user", user);
					session.setAttribute("pass", "Login Success");
					return "redirect:/home";
				} else {
					int otp = new Random().nextInt(100000, 1000000);
					user.setOtp(otp);
					System.err.println(otp);
					// emailSender.sendOtp(user.getEmail(), otp, user.getFirstname());
					repository.save(user);
					session.setAttribute("pass", "Otp Sent Success, First Verify Email to Login");
					return "redirect:/otp/" + user.getId();
				}
			} else {
				session.setAttribute("fail", "Incorrect Password");
				return "redirect:/login";
			}
		}
	}

	public String loadHome(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null)
			return "home.html";
		else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

	public String logout(HttpSession session) {
		session.removeAttribute("user");
		session.setAttribute("pass", "Logout Success");
		return "redirect:/login";
	}

	

	public String profile(HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			List<Post> posts = postRepository.findByUser(user);
			if(!posts.isEmpty())
				map.put("posts", posts);
			return "profile.html";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}
	
	
	public String editProfile(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			return "edit-profile.html";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}
	
	public String updateProfile(HttpSession session, MultipartFile image, String bio) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			user.setBio(bio);
			user.setImageUrl(cloudinaryHelper.saveImage(image));
			repository.save(user);
			return "redirect:/profile";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}
	
	public String loadAddPost(ModelMap map, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			map.put("add", "add");
			return "profile.html";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}
	public String addPost(Post post, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			post.setImageUrl(cloudinaryHelper.saveImage(post.getImage()));
			post.setUser(user);
			postRepository.save(post);
			
			session.setAttribute("pass", "Posted Success");
			return "redirect:/profile";
			
		}else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}
	public String deletePost(int id, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			postRepository.deleteById(id);
			session.setAttribute("pass", "Deleted Success");
			return "redirect:/profile";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}
	public String viewSuggestions(HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			List<User> suggestions = repository.findByVerifiedTrue();
			List<User> usersToRemove = new ArrayList<User>();
			for (User suggestion : suggestions) {
				if (suggestion.getId() == user.getId()) {
					usersToRemove.add(suggestion);
				}
				for (User followingUser : user.getFollowing()) {
					if (followingUser.getId() == suggestion.getId()) {
						usersToRemove.add(suggestion);
					}
				}
			}
			if (suggestions.isEmpty()) {
				session.setAttribute("fail", "No Suggestions");
				return "redirect:/profile";
			} else {
				suggestions.removeAll(usersToRemove);
				map.put("suggestions", suggestions);
				return "suggestions.html";
			}
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}
	public String followUser(int id, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			User folllowedUser = repository.findById(id).get();
			user.getFollowing().add(folllowedUser);
			folllowedUser.getFollowers().add(user);
			repository.save(user);
			repository.save(folllowedUser);
			return "redirect:/profile";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}
}
