package com.prafful.springjwt.service;

public final class SecurityUtils {

	public static ThreadLocal<String> userName = new ThreadLocal<>();

	public static void setEmailId(String username1) {
		userName.set(username1);;
	}
	
	public static String getEmailId() {
		return userName.get();
	}

}
