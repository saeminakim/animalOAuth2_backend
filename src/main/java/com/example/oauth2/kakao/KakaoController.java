package com.example.oauth2.kakao;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.oauth2.user.UserService;
import com.google.gson.Gson;

@RestController
public class KakaoController {

	private UserService userService;
	
	@Autowired
	public KakaoController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping(value="/kakao/validate")
	public String validateToken(@RequestBody String accessToken) throws Exception {
		
		URL url = new URL("https://kapi.kakao.com/v1/user/access_token_info");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		con.addRequestProperty("Authorization", "Bearer " + accessToken);
		
		byte[] result = con.getInputStream().readAllBytes();
		String data = new String(result);
		
		if(con.getResponseCode() == 200) {
			data = getProfile(accessToken);
			
			KakaoUser kakaoUser = new Gson().fromJson(data, KakaoUser.class);
			
			String sessionId = userService.createProfile(kakaoUser);
			
			data = sessionId;
		} else {
			return null;
		}
		
		return data;
	}
	
	private String getProfile(String accessToken) throws IOException {
		
		URL url = new URL("https://kapi.kakao.com/v2/user/me");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		con.addRequestProperty("Authorization", "Bearer " + accessToken);
		
		byte[] result = con.getInputStream().readAllBytes();
		String data = new String(result);
		
		return data;
	}
}
