package com.prafful.springjwt.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prafful.springjwt.models.User;
import com.prafful.springjwt.repository.RoleRepository;
import com.prafful.springjwt.repository.UserRepository;
import com.prafful.springjwt.security.jwt.JwtUtils;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/external")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/login")
	public String authenticateUser(@RequestBody User user) {

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		return jwt;
	}

	@PostMapping("/register")
	public Map<String, Object> registerUser(@RequestBody User user) {
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new RuntimeException("Error: Email is already taken!");
		}

		user.setPassword(encoder.encode(user.getPassword()));
		userRepository.save(user);
		Map<String, Object> response = new HashMap<>();

		Map<String, Object> userData = new HashMap<>();
		userData.put("email", user.getEmail());
		userData.put("fullname", user.getFullname());

		response.put("success", true);
		response.put("message", "User registered successfully!");
		response.put("data", userData);

		return response;
	}

	@GetMapping("/verify")
	public String verifyUser() {
		return "true";
	}
}
