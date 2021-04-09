package com.example.oauth2.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer{
	// CORS(cross origin resource sharing)을 설정
	// 서로 다른 도메인:포트 간의 통신을 허용을 해줌
	// AJAX(async javascript anad XML) 통신 허용
	@Override
	public void addCorsMappings(CorsRegistry registry) {
				 // 모든 경로(/...)  // 모든 메소드(GET, POST, ....)
		registry.addMapping("/**").allowedMethods("*");
	}

}
