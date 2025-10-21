package com.prafful.springjwt.pdf2Markdown;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.PrintWriter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

@Service
public class pdf2MarkDownService {
	
    @Autowired
    private ResourceLoader resourceLoader;
    
    private static final String PDF_PATH="classpath:pdf/boat.pdf";
    
    private static final String HTML_PATH="classpath:html/pdf.html"; 

	public String pdf2Markdown() throws IOException, URISyntaxException {
		File file = generateHTMLFromPDF(resourceLoader.getResource(PDF_PATH).getFile());
		Document doc = Jsoup.parse(file, "UTF-8");
		convertHtmlToMarkdown(doc.text());
		return null;
	}

//	private void generateHTMLFromPDF(String file) throws IOException, URISyntaxException {
//	    PDDocument pdf = PDDocument.load(file);
//	    URL resource = getClass().getClassLoader().getResource("html/pdf.html");
//	    Writer output = new PrintWriter(resource.toURI().toString(), "utf-8");
//	    new PDFDomTree().writeText(pdf, output);
//	    
//	    output.close();
//	}
	
	private File generateHTMLFromPDF(File file) throws IOException {
		File outputFile = new File("output.html");
	    try (PDDocument pdf = PDDocument.load(file)) {
	        // Save output HTML to a temp or desired directory
	         // Change path as needed
	        try (Writer output = new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8")) {
	            new PDFDomTree().writeText(pdf, output);
	        }
	    }
	    return outputFile;
	}
	
    public static String convertHtmlToMarkdown(String html) {
        // Parse HTML using Jsoup
        Document doc = Jsoup.parse(html);

        // Select the content you want to convert (e.g., <body>)
        Element body = doc.body();

        // Initialize flexmark-java parser and renderer
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // Convert HTML to Markdown
        String markdown = renderer.render(parser.parse(body.html()));
        
        System.out.println(markdown);

        return markdown;
    }
}
