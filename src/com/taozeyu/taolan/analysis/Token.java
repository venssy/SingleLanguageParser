package com.taozeyu.taolan.analysis;

import com.taozeyu.taolan.analysis.Clash0Function.Math;
import com.taozeyu.taolan.analysis.Clash0Function.System;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Token {

    private static final HashSet<String> keywordsSet = new HashSet<>();
    private static final Map<String, HashSet<String>> classSet = new HashMap<>();
    private static final HashSet<String> functionSet = new HashSet<>();

	private boolean isSingleQuoter;
	
    static {
        keywordsSet.add("true");
        keywordsSet.add("false");
        keywordsSet.add("null");

        classSet.put("String", com.taozeyu.taolan.analysis.Clash0Function.String.functionsSet);
        classSet.put("Math", Math.functionsSet);
        classSet.put("Math", System.functionsSet);

        functionSet.addAll(com.taozeyu.taolan.analysis.Clash0Function.String.functionsSet);
        functionSet.addAll(Math.functionsSet);
        functionSet.addAll(System.functionsSet);
    }

    // Identifier 初步是字母+数字+_，继续拆分出数字和关键字
    public static enum Type {
        Keyword, Number, Identifier, Sign, String, Space, NewLine, EndSymbol
    }

    final Type type;
    final String value;

    Token(Type type, String value) {

        if(type == Type.Identifier) {
            char firstChar = value.charAt(0);
            if(firstChar >= '0' & firstChar < '9') {
                type = Type.Number;
            } else if(keywordsSet.contains(value)){
                type = Type.Keyword;
            }
        }
        else if(type == Type.String) {
            value = value.substring(1, value.length() - 1);
			isSingleQuoter = value.startsWith("'");
        }
        else if(type == Type.EndSymbol) {
            value = null;
        }
        this.type = type;
        this.value = value;
    }
	
	public boolean isSingleQuoter()
	{
		return isSingleQuoter;
	}

    @Override
    public String toString() {
        return String.format("%s(%s) ", type, value);
    }
}
