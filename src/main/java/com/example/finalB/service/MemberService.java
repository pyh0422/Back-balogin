package com.example.finalB.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.finalB.domain.Member;
import com.example.finalB.repository.MemberRepository;
import com.google.gson.Gson;
import com.example.finalB.domain.RoleType;
import com.example.finalB.domain.Trans;
import com.example.finalB.domain.OAuthType;

@Service
public class MemberService {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtService jwtService;

	@Value("${google.default.password}")
	private String googlePassword;

	@Value("${kakao.default.password}")
	private String kakaoPassword;

	public void insertMember(Member member) {

		member.setPassword(passwordEncoder.encode(member.getPassword()));
		member.setRole(RoleType.MEMBER);

		memberRepository.save(member);

	}
	
	public Member getMember(String username) {
		
		return memberRepository.findByUsername(username).orElseGet(() -> {
			return new Member();
		});
	}
	
	public Member getIdPassword(String phone) {
		
		return memberRepository.findByPhone(phone).orElseGet(() -> {
			return new Member();
		});
	}

    public void updateMember(Member member) {
    	
		Member originalMember = memberRepository.findByUsername(member.getUsername()).get();
		
		originalMember.setPassword(passwordEncoder.encode(member.getPassword()));
		originalMember.setName(member.getName());
		originalMember.setEmail(member.getEmail());
		originalMember.setPhone(member.getPhone());
		originalMember.setAddress(member.getAddress());
		originalMember.setDetailAddress(member.getDetailAddress());
		originalMember.setBirthdate(member.getBirthdate());
		
		memberRepository.save(originalMember);

    }

	public Member googleLogin(String token) {
		
		RestTemplate restTemplate = new RestTemplate();

		String userInfoEndPoint = "https://www.googleapis.com/oauth2/v1/userinfo";

		HttpHeaders header = new HttpHeaders();
		header.add("Authorization", "Bearer " + token);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(header);

		ResponseEntity<String> response = restTemplate.exchange(userInfoEndPoint, HttpMethod.GET, request,
				String.class);

		String userInfo = response.getBody();

		Gson gson = new Gson();

		Map<?, ?> data = gson.fromJson(userInfo, Map.class);

		Member member = new Member();

		member.setUsername("(g)" + (String) data.get("email"));
		member.setEmail((String) data.get("email"));
		member.setName((String) data.get("name"));
		member.setOauth(OAuthType.GOOGLE);
		member.setPassword(googlePassword);

		return member;
	}


	public ResponseEntity<?> getResponseEntity(String username, String password) {

		UsernamePasswordAuthenticationToken upaToken = new UsernamePasswordAuthenticationToken(username, password);

		Authentication auth = authenticationManager.authenticate(upaToken);
		String jwt = jwtService.getToken(auth.getName());

		Member member = memberRepository.findById(username).get();

		MultiValueMap<String, Member> body = new LinkedMultiValueMap<>();

		body.add("member", member);

		return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
				.header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization").body(body);
	}


	public String getKakaoAccessToken(String code) {

	      HttpHeaders header = new HttpHeaders();

	      header.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

	      // body에다가 심을 것들 ( Kakao에서 시킴 )
	      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

	      body.add("grant_type", "authorization_code");
	      body.add("client_id", "ccc3b6d2fedd138aa407aa4112b315cd"); // 각자 rest api key
//	      body.add("redirect_uri", "http://localhost:3000/oauth/kakao");
	      body.add("redirect_uri", "http://https://balogin-741ae.web.app/oauth/kakao");
	      body.add("code", code);

	      HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, header);

	      RestTemplate restTemplate = new RestTemplate();

	      ResponseEntity<String> response = restTemplate.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST,
	            request, String.class);

	      String json = response.getBody();

	      Gson gson = new Gson();
	      Map<?, ?> data = gson.fromJson(json, Map.class);

	      return (String) data.get("access_token");

	}

	public Member kakaoLogin(String accessToken) {

		HttpHeaders header = new HttpHeaders();
		header.add("Authorization", "Bearer " + accessToken);
		header.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(header);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> response = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST,
				request, String.class);

		String json = response.getBody();
		Gson gson = new Gson();
		Map<?, ?> data = gson.fromJson(json, Map.class);

		// 닉네임을 빼오려면 properties 먼저 빼와야함
		// properties는 Map 안에 있기 때문에 Map으로 꺼낸 후
		// nickname은 String 형태기 때문에 형변환
		String username = (String) ((Map<?, ?>) data.get("properties")).get("nickname");
		String email = (String) ((Map<?, ?>) data.get("kakao_account")).get("email");

		Member member = new Member();

		member.setUsername("(k)" + email);
		member.setEmail(email);
		member.setName(username);
		member.setRole(RoleType.MEMBER);
		member.setOauth(OAuthType.KAKAO);
		member.setPassword(kakaoPassword);

		return member;

	}

}
