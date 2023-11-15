package com.example.finalB.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer  {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		registry.addMapping("/**")	// 모든 요청을 받았을때
				//.allowedOrigins("http://localhost:3000")	// 3000 포트번호로 들어오는거 허용하셈
				.allowedOrigins("https://balogin-741ae.web.app")
				.allowedMethods("GET", "POST", "DELETE", "PUT");


		
		// 시큐리티 쓰면 시큐리티도 설정해야함
		// 배포(FireBase)를 하면 달라지기 때문에 도메인으로 바꿔줘야함
	}
}


