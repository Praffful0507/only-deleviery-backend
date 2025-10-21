package com.prafful.springjwt.pdf2Markdown;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pdf2markdown")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class pdf2MarkDownController {
	
	@Autowired
	pdf2MarkDownService pdf2MarkDownService;
	
	@GetMapping
	public String sentMail() throws IOException, URISyntaxException {
		pdf2MarkDownService.pdf2Markdown();
		return "it is working";
	}

}

