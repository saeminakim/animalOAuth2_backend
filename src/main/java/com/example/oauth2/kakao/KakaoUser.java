package com.example.oauth2.kakao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class KakaoUser {

	private long id;
	private Account kakao_account;
	
	@Data
	public class Account {
		private String email;
		private Profile profile;
	}
	
	@Data
	public class Profile {
		private String nickname;
		private String profile_image_url;
	}
}
