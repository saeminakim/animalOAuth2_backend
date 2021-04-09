package com.example.oauth2.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity
public class Profile {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String userId;
    private String userType;
    private String name;
    private String email;
    private String image;
    private String sessionId;
}
