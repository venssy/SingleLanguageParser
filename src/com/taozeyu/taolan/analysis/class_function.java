package com.taozeyu.taolan.analysis;
import java.util.Map;
import java.util.HashMap;

public class class_function
{
	public static Map<String, String[]> map = new HashMap<>();
	
	static {
		map.put("String", new String[]{ "valueOf", "format", });
		map.put("Math", new String[]{ "pow", "abs", });
		map.put("System", new String[]{ "currentTimeMillis", "format", });
	}
}
