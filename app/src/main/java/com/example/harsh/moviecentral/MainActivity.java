package com.example.harsh.moviecentral;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import cz.msebera.android.httpclient.*;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
//import com.example.harsh.moviecentra.Contstants;

public class MainActivity extends AppCompatActivity {

    private String sortBy="release_date";
    private String sortOrd="desc";
    private TextView msg;
    private GridView gv;
    private Context c;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msg=(TextView)findViewById(R.id.connectivityPopup);
        gv=(GridView)findViewById(R.id.grid_keeper);
        c=this;
        if(isConnected())
        {
            msg.setVisibility(View.GONE);
            loadGrid();
        }
        else
        {
            msg.setVisibility(View.VISIBLE);
        }

    }
    private  void loadGrid(){
        Map<String,String> reqMap=new HashMap<>();
        reqMap.put("api_key", Constants.movieDbkey);
        reqMap.put("sort_by", sortBy + "." + sortOrd);
        Date now = new Date();
        String strDate = sdf.format(now);
        String url=getResources().getString(R.string.discoverMovie);
        url+=Constants.getQueryParamFromMap(reqMap);
        url+="&release_date.lte="+strDate+"&language=en";
        Log.e("Movie Central",url);
        new HttpAsyncTask().execute(url);
        //RequestModel request=new RequestModel("loadGrid",getResources().getString(R.string.discoverMovie),reqMap,null);

    }
    private boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    public String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }
   private String convertInputStreamToString(InputStream inputStream)
    {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
            {  result += line;
            }

            inputStream.close();
            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "Error";
        }
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Gson g=new Gson();
            PosterView output=g.fromJson(result,PosterView.class);
            populateGrid(output);
        }
    }
    void populateGrid(PosterView data){
        PosterViewAdapter adap=new PosterViewAdapter(c,data);
        gv.setAdapter(adap);
    }


}
