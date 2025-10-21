package com.prafful.springjwt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class PraffulExpressionExpressior extends SpelExpressionParser {

	private Map<String, Expression> expressionMap = new ConcurrentHashMap<>();
	private static PraffulExpressionExpressior praffulExpressionExpressior = new PraffulExpressionExpressior();

	private PraffulExpressionExpressior() {
		super();
	}

	public static PraffulExpressionExpressior getSpelExpressionParser() {
		return praffulExpressionExpressior;
	}

	public Expression doParseExpression(String expressionString) {
		if (StringUtils.isBlank(expressionString))
			return null;
		Expression exp = expressionMap.get(expressionString);
		if (exp == null) {
			exp = super.parseExpression(expressionString);
			expressionMap.put(expressionString, exp);
		}
		return exp;
	}
}
