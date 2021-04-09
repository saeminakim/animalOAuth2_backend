package com.example.oauth2.user;

import java.security.MessageDigest;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.oauth2.kakao.KakaoUser;

@Service
public class UserService {

	private ProfileRepository repo;
	private StringRedisTemplate redisTemplate;
	
	@Autowired
	public UserService(ProfileRepository repo, StringRedisTemplate redisTemplate) {
		this.repo = repo;
		this.redisTemplate = redisTemplate;
	}
	
	// 카카오 사용자 정보를 받아서 프로필 정보를 생성
	public String createProfile(KakaoUser kakaoUser) throws Exception {
		
		// 기존 프로필 정보 조회해서 없으면 새로 프로필 생성
		Profile profile = repo.findByUserTypeAndUserId("KAKAO", kakaoUser.getKakao_account().getEmail());
		System.out.println("----------리파지토리에 프로필 있는지 검색 profile---------");
		System.out.println(profile);
		
		if(profile == null) {
			profile = Profile.builder()
					.userType("KAKAO")
					.userId(kakaoUser.getKakao_account().getEmail())
					.name(kakaoUser.getKakao_account().getProfile().getNickname())
					.email(kakaoUser.getKakao_account().getEmail())
					.image(kakaoUser.getKakao_account().getProfile().getProfile_image_url())
					.build();
			
			System.out.println("-----------없어서 새로 만든 프로필------------");
			System.out.println(profile);
			repo.save(profile);
		}
		
		// 세션 정보 생성하고 세션Id 반환
		return createSession(profile);
	}
	
	// 프로필 정보로 세션 정보 생성
	public String createSession(Profile profile) throws Exception {
		
		// 기존 세션Id가 있으면 삭제
		String sessionId = profile.getSessionId();
		if(sessionId != null && !profile.getSessionId().equals("")) {
			redisTemplate.delete(sessionId);
		}
		
		sessionId = sha512(UUID.randomUUID().toString());
		
		// Redis에 세션 정보를 생성
		HashOperations<String, String, String> record = redisTemplate.opsForHash();
		record.put(sessionId, "id", String.valueOf(profile.getId()));
		record.put(sessionId, "userId", profile.getUserId());
		record.put(sessionId, "userType", profile.getUserType());
		record.put(sessionId, "name", profile.getName());
		record.put(sessionId, "email", profile.getEmail());
		record.put(sessionId, "image", profile.getImage() != null ? profile.getImage() : "");
		
		// 세션 만료 시간 지정
		redisTemplate.expire(sessionId, Duration.ofHours(24));
		
		// 새로운 sessionId 저장(기존에 있으면 삭제하기 위해서)
		profile.setSessionId(sessionId);
		repo.save(profile);		
		
		return sessionId;
	}
	
	// session id로 프로필 정보를 조회
	public Profile getSessionProfile(String sessionId) {
		
		HashOperations<String, String, String> record = redisTemplate.opsForHash();
		String profileId = record.get(sessionId, "id");
		if(profileId == null) {
			return null;
		}
		
		return Profile
				.builder()
					.id(Long.valueOf(profileId))
					.userType(record.get(sessionId, "userType"))
					.userId(record.get(sessionId, "userId"))
					.name(record.get(sessionId, "name"))
					.email(record.get(sessionId, "email"))
					.image(record.get(sessionId, "image"))
				.build();
	}
	
	// 세션 삭제
	public void deleteSession(Profile profile, String sessionId) {
		System.out.println(sessionId);
		System.out.println(redisTemplate.delete(sessionId));
		profile.setSessionId(null);
		
		repo.save(profile);
	}
	
	private String sha512(String msg) throws Exception {
		
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(msg.getBytes());
		StringBuilder builder = new StringBuilder();
		for (byte b: md.digest()) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}
}
