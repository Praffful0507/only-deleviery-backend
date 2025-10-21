package com.prafful.springjwt.spel;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PraffulData {
	
	public Map<String, Object> data;
	public List<Map<String, Object>> listData;
	public String fieldValue;

}
