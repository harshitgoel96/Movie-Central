package com.example.harsh.moviecentral.utils;

import java.util.Map;
import java.util.Set;

/**
 * Created by harsh on 12/24/2015.
 */
public class Constants {
    public static final String movieDbkey="8736331982ed8dd2fdd5e3de4ece825a";
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
    private static final String yotubeImgUnf="http://img.youtube.com/vi/%s/0.jpg";
    private static final String youtubeVideoLink="http://www.youtube.com/watch?v=";
    private static final String vidLinkUnf="http://api.themoviedb.org/3/movie/%s/videos?api_key=%s";
    private static final String revLink="http://api.themoviedb.org/3/movie/%s/reviews?api_key=%s";
    public static final String keyName="data";
    public static String getYoutubeVideoLink(String vId){
        return youtubeVideoLink+vId;
    }
    public static String getYoutubeVideoImage(String vId){
        return String.format(yotubeImgUnf,vId);
    }
    public static String getVideoLink(String id){
        return String.format(vidLinkUnf,id,movieDbkey);
    }
    public static String getReviewLink(String id){
        return String.format(revLink,id,movieDbkey);
    }
}
