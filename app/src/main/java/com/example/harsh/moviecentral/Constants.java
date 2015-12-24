package com.example.harsh.moviecentral;

import java.util.Map;
import java.util.Set;

/**
 * Created by harsh on 12/24/2015.
 */
public class Constants {
    public static final String movieDbkey="API KEY REMOVED ON PURPOSE";
    public static String getQueryParamFromMap(Map<String,String> map){
        Set<String> keySet=map.keySet();
        StringBuilder strB=new StringBuilder("?");
        for (String key: keySet
             ) {
            strB.append(key);
            strB.append("=");
            strB.append(map.get(key));
            strB.append("&");
        }
        strB=strB.deleteCharAt(strB.length()-1);
        return strB.toString();
    }
    public static final String imageBase="http://image.tmdb.org/t/p/w185/";

    public static final String keyName="data";
}
