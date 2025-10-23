package com.prafful.springjwt.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/mail")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EmailController {
	
	@Autowired
	EmailService emailService;
	
	@GetMapping
	public String sentMail() {
		return emailService.sendMail();
	}
	
	@GetMapping("/otp")
	public String sentOtp(HttpSession session) {
		return emailService.sentOtp(session);
	}
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {
		return emailService.verifyOtp(otp, session);
	}
	
}
