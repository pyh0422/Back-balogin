package com.example.finalB.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.example.finalB.domain.RoleType;
import com.example.finalB.filter.JwtFilter;
import com.example.finalB.security.AuthEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtFilter jwtFilter;

	@Autowired
	private AuthEntryPoint authEntryPoint;

	@Bean
	public SecurityFilterChain filerChain(HttpSecurity http) throws Exception {
		http.csrf().disable(); // 클라이언트 사이드 랜더링으로 할때는 csrf 사용 X 그래서 비활성화 해주는게 좋음
		http.cors(); // 크로스 오리진 할거다 미리 적음 (아래 CorsConfigurationSource은 이게 있기 때문에 작동할 수 있는거)

		// 세션 비활성화
//		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);


		http.authorizeRequests().antMatchers(HttpMethod.POST, "/**", "/login", "/signup", "/oauth/**", "/idoverlap", "/oauth/kakao", "/oauth/google", "/mileage", "/verifyIamport/**", "/insertTrans", "/notice" , "/onetoone" ,"/questions").permitAll()
				.antMatchers(HttpMethod.PUT, "/updateTrans","/updateMember", "kagoosignup").permitAll()
				.antMatchers(HttpMethod.GET, "/**", "/transPost","/transDetail/{id}", "/intransInfo/**", "/listPages/**", "/board", "/userInfo", "/notice" , "/onetoone" ,"/questions").permitAll().anyRequest().authenticated().and()
				.exceptionHandling() // 예외 발생했을 때
				.authenticationEntryPoint(authEntryPoint).and()
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();

	}

	@Bean
	public CorsConfigurationSource configurationSource() {
		CorsConfiguration config = new CorsConfiguration(); // cors 설정해주는 객체

		config.addAllowedOrigin("http://localhost:3000"); // 3000번 포트로 요청이 오는 모든건 허용
		config.addAllowedOrigin("https://balogin-741ae.web.app");
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // 임폴트할때 reactive가 없는걸로
		source.registerCorsConfiguration("/**", config); // (허용범위, 어디에 있는 소스인지)

		return source;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		// 인증매니저 리턴시키면서 @Bean으로 등록
		return authenticationConfiguration.getAuthenticationManager();
	}

}
