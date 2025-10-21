package com.prafful.springjwt.security;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.http.HttpServletRequest;

public class CsrfSecurityRequestMatcher implements RequestMatcher {
	private static final List<String> CSRF_DISABLED_HEADERS = Arrays.asList("EXCEL");
	private Pattern allowedMethods = Pattern.compile("^(GET)$");

	@Override
	public boolean matches(HttpServletRequest request) {
		if (allowedMethods.matcher(request.getMethod()).matches()) {
			return false;
		}
		if (request.getHeader("Requested-Source") != null
				&& CSRF_DISABLED_HEADERS.contains(request.getHeader("Requested-Source").toUpperCase())) {
			return false;
		}
		return true;
	}
}
