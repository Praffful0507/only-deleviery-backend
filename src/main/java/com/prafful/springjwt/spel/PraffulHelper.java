package com.prafful.springjwt.spel;

import java.math.BigDecimal;
import java.util.Map;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PraffulHelper extends PraffulData {

	public PraffulHelper(Map<String, Object> data) {
		super();
		this.data = data;
	}

	public Object sum(Object... data) {
		BigDecimal sum = BigDecimal.ZERO;
		for (Object object : data) {
			BigDecimal s = BigDecimal.valueOf((Double)object);
			sum = sum.add(s);
		}
		return sum;
	}

}
