package com.example.oauth2.user;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

	private UserService service;
	
	@Autowired
	public ProfileController(UserService service) {
		this.service = service;
	}
	
	@GetMapping(value="/profile")
	public Profile getProfile(@RequestHeader String authorization, HttpServletResponse res) {
		System.out.println(authorization);
		
		Profile profile = service.getSessionProfile(authorization.replace("Bearer ", ""));
		
		if(profile == null) {
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		
		return profile;
	}
	
	@DeleteMapping(value="/signout")
	public boolean signOut(@RequestHeader String authorization, HttpServletResponse res) {
		
		String sessionId = authorization.replace("Bearer ", "");
		
		Profile profile = service.getSessionProfile(sessionId);
		
		if(profile == null) {
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
		
		service.deleteSession(profile, sessionId);
		return true;
	}
}
