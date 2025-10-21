package com.prafful.springjwt.service;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.annotation.PostConstruct;

@ComponentScan(basePackages = "com.prafful")
@Configuration
@EnableScheduling
@EnableAsync
public class Appconfig {

	@PostConstruct
	void setGlobalSecurityConstruct() {
		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
	}
}
