package com.prafful.springjwt.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prafful.springjwt.models.User;
import com.prafful.springjwt.repository.UserRepository;
import com.prafful.springjwt.security.jwt.JwtUtils;

import io.jsonwebtoken.Claims;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	UserRepository userRepository;

	@GetMapping("/getUserDetails")
	public Map<String, Object> getDetails(@RequestHeader(value = "Authorization", required = false) String authHeader) {
		Claims emailid = jwtUtils.decodeJWT(authHeader.substring(7));
		User user = userRepository.findByEmail(emailid.getOrDefault("sub", "emailID").toString())
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: "));
		Map<String, Object> data = new HashMap<>();
		data.put("user", user);
		return data;
	}

}
