package com.kusur.Kusur.util;

public class UniqueTagGenerator {
    public static String generateUniqueTag(String username){
        Integer number = (int)(Math.random()*9999);
        return username+"#"+number;
    }
}
