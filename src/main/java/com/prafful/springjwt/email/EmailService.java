package com.prafful.springjwt.email;

import java.io.File;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class EmailService {

	public String sendMail() {

		System.out.println("preparing to send message ...");
		String message = "Hello , Dear, this is message for security check . ";
		String subject = "CodersArea : Confirmation";
		String to = "yjain7573@gmail.com";
		String from = "aprafful52@gmail.com";

		sendEmail(message, subject, to, from);

		return "Mail Sent";
	}

	// this is responsible to send the message
	public Boolean sendEmail(String message, String subject, String to, String from) {
		// Variable for gmail
		String host = "smtp.gmail.com";

		// get the system properties
		Properties properties = System.getProperties();
		System.out.println("PROPERTIES " + properties);

		// setting important information to properties object

		// host set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// Step 1: to get the session object..
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("aprafful52@gmail.com", "nvifvhwgirqtyrgz");
			}
		});
		session.setDebug(true);

		// Step 2 : compose the message [text,multi media]
		MimeMessage m = new MimeMessage(session);

		try {

			// from email
			m.setFrom(from);

			// adding recipient to message
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// adding subject to message
			m.setSubject(subject);

			// adding text to message
			m.setText(message);

			// send

			// Step 3 : send the message using Transport class
			Transport.send(m);

			System.out.println("Sent success...................");
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// this is responsible to send the message with attachment
	private static void sendAttach(String message, String subject, String to, String from) {

		// Variable for gmail
		String host = "smtp.gmail.com";

		// get the system properties
		Properties properties = System.getProperties();
		System.out.println("PROPERTIES " + properties);

		// setting important information to properties object

		// host set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// Step 1: to get the session object..
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("techsoftindia2018@gmail.com", "*******");
			}

		});

		session.setDebug(true);

		// Step 2 : compose the message [text,multi media]
		MimeMessage m = new MimeMessage(session);

		try {

			// from email
			m.setFrom(from);

			// adding recipient to message
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// adding subject to message
			m.setSubject(subject);

			// attachement..

			// file path
			String path = "C:\\Users\\user\\Desktop\\ca_logo.png";

			MimeMultipart mimeMultipart = new MimeMultipart();
			// text
			// file

			MimeBodyPart textMime = new MimeBodyPart();

			MimeBodyPart fileMime = new MimeBodyPart();

			try {

				textMime.setText(message);

				File file = new File(path);
				fileMime.attachFile(file);

				mimeMultipart.addBodyPart(textMime);
				mimeMultipart.addBodyPart(fileMime);

			} catch (Exception e) {

				e.printStackTrace();
			}

			m.setContent(mimeMultipart);

			// send

			// Step 3 : send the message using Transport class
			Transport.send(m);

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Sent success...................");
	}

	public String sentOtp(HttpSession session) {
			 Random random = new Random();
			 int otp = random.nextInt(9999);
		System.out.println("preparing to send message ...");
		String message = " " + otp + " ";
		String subject = "Otp from Finance";
		String to = "pra571999@gmail.com";
		String from = "aprafful52@gmail.com";

		boolean flag = sendEmail(message, subject, to, from);	
		if(flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", to);
			return "OTP Sent";
		}else {
			return "Otp is not Sent";
		}
	}
	
	public String verifyOtp(int otp, HttpSession session) {
		int myOtp = (int) session.getAttribute("myOtp");
		String email = (String) session.getAttribute("email");
		if(myOtp == otp) {
			return "otp matched";
		}else {
			return "otp is not matched";
		}
	}
}
