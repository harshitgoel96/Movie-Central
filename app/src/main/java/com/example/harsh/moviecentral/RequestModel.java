package com.example.harsh.moviecentral;

import java.util.Map;

/**
 * Created by harsh on 12/24/2015.
 */
public class RequestModel {
    private String action;
    private String url;
    private Map<String,String> params;
    private Map<String,String> headers;

    public RequestModel(String action, String url, Map<String, String> params, Map<String, String> headers) {
        this.action = action;
        this.url = url;
        this.params = params;
        this.headers = headers;
    }

    public String getAction() {
        return action;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
