package com.onyu.config.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.onyu.service.JWTservice;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {

	private final JWTservice jwtService;
	
	public void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain fc) throws IOException, ServletException {
		
		String authorization = req.getHeader("Authorization");
		log.info("권한 부여 헤더 값 : {}", authorization);
		
		if(authorization == null) {
			log.info("권한 부여 헤더를 찾지 못해 인증요청을 처리하지 못했습니다", authorization);
			fc.doFilter(req, resp);
			return;
		}
		try {
		String loginId = jwtService.verifyToken(authorization);
		
		List<String> roles = new ArrayList<>();
		if(loginId.equals("Onyu")) {
			roles.add("ROLE_ADMIN");
		}else {
			roles.add("ROLE_NORMAL");
		}
		
		List<? extends GrantedAuthority> authorities = 
				roles.stream().map(t -> new SimpleGrantedAuthority(t)).toList();
		
		Authentication authentication =
				new UsernamePasswordAuthenticationToken(loginId, authorization, authorities);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		}catch(Exception e) {
			log.error("토큰인증 실패 - {}", e.getMessage());
			throw new BadCredentialsException("잘 못된 토큰입니다");
		}
		
		
		fc.doFilter(req, resp);
	}
}
