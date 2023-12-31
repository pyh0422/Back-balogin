package com.example.finalB.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.finalB.domain.Member;
import com.example.finalB.repository.MemberRepository;

@Service
public class LoginService {
	
	@Autowired
	MemberRepository memberrepository;
	
	public Member loginIdPwChk(String username, String password) {
		
		Member member = memberrepository.findByUsername(username).orElse(null);

		if (member.getPassword().equals(password)) {
			return member;
		}
		return null;
	}
	
	public Member getMember(String username) {
		
		return memberrepository.findByUsername(username).orElseGet(() -> {
			
			return new Member();
		});
	}
}


