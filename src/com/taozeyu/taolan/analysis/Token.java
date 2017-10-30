package com.taozeyu.taolan.analysis;

import com.taozeyu.taolan.analysis.classes.Classes;

import java.util.HashSet;

public class Token {

    private static final HashSet<String> keywordsSet = new HashSet<>();

	private boolean isSingleQuoter;
	
    static {
        keywordsSet.add("true");
        keywordsSet.add("false");
        keywordsSet.add("null");
        keywordsSet.addAll(Classes.map.keySet());
        for(String[] array : Classes.map.values()){
            for(String value : array){
                keywordsSet.add(value);
            }
        }
    }

    // Identifier 初步是字母+数字+_，继续拆分出数字和关键字
    public enum Type {
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

        Util.log("Token", toString());
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
