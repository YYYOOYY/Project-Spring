package com.onyu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfigureation {

	@Bean
	SecurityFilterChain ProjectSecurityChain(HttpSecurity http) throws Exception {
		
		http.csrf(t -> t.disable());
		http.sessionManagement(t -> t.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		http.authorizeHttpRequests(t -> t
				.requestMatchers("/**").permitAll());
		
		http.anonymous(t->t.disable());
		http.cors();
		return http.build();
	}
}
