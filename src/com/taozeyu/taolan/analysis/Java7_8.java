package com.taozeyu.taolan.analysis;

import java.util.LinkedList;

public class Java7_8 {
    public static String String_join(String split, LinkedList<String> origins){
        if(origins.size()==0) return "";
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for(String o : origins){
            if(i==0) sb.append(o);
            else sb.append(split).append(o);
            i++;
        }
        return sb.toString();
    }
}
