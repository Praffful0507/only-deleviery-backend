package com.prafful.springjwt;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.prafful.springjwt.spel.PraffulAccessor;
import com.prafful.springjwt.spel.PraffulHelper;

@SpringBootApplication
public class SpringBootSecurityJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityJwtApplication.class, args);
		//String expression = "199.99 + 199.99";
		//String expression = "sum(199.99, 199.99)";
		String expression = "sum(PRAFFUL + YASH)";
		Map<String, Object> map = Map.of("PRAFFUL", 43.0, "YASH", 87.0);
		ExpressionParser parser = PraffulExpressionExpressior.getSpelExpressionParser();
		Expression spelExpression = parser.parseExpression(expression);
		StandardEvaluationContext spelContext = new StandardEvaluationContext();
		spelContext.addPropertyAccessor(new PraffulAccessor());
		PraffulHelper spel = new PraffulHelper(map);
		spelContext.setRootObject(spel);
		Object ans = spelExpression.getValue(spelContext, Object.class);
 		System.out.println(ans);
	}

}
