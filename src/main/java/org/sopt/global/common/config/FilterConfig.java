package org.sopt.global.common.config;

import org.sopt.filter.JwtAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

	private final JwtAuthFilter jwtAuthFilter;

	@Bean
	public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration() {
		FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(jwtAuthFilter);
		registration.addUrlPatterns("/*");  // 모든 URL에 대해 필터 적용
		registration.setOrder(1);           // 필터 순서 (낮을수록 먼저 실행)
		registration.setName("jwtAuthFilter");
		return registration;
	}


}
