package com.example.harsh.moviecentral.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harsh.moviecentral.utils.Constants;
import com.example.harsh.moviecentral.R;
import com.example.harsh.moviecentral.adapter.PosterViewAdapter;
import com.example.harsh.moviecentral.model.PosterView;
import com.example.harsh.moviecentral.model.Result;
import com.example.harsh.moviecentral.utils.MyDBHandler;
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
import java.util.List;
import java.util.Map;

//import com.example.harsh.moviecentra.Contstants;

public class MainActivity extends Activity {

    private String sortBy = "release_date";
    private String sortOrd = "desc";
    private TextView msg;
    private GridView gv;
    private Context c;
    private PosterView output;
    private ImageView imgBtn;
    private PosterViewAdapter adap;
    private MyDBHandler dbH;
    String ketForFavSort="fav";
    private List<Result> favPosters;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private void showToast(String msg){
        Toast.makeText(c,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menues, menu);
        MenuItem mI= menu.findItem(R.id.listFav);
        if(dbH.count()<1){

            mI.setVisible(false);

        }
        else{
            mI.setVisible(true);
        }


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Handle item selection

        switch (item.getItemId()) {
            case R.id.sortByRelease:
                //newGame();
                if (!sortBy.equals("release_date")) {
                    adap.notifyDataSetInvalidated();
                    gv.invalidateViews();
                    sortBy = "release_date";
                    loadGrid();
                }
                else{
                    showToast("Already Sorted");
                }
                return true;
            case R.id.sortByRating:
                //showHelp();
                if (!sortBy.equals("vote_average")) {
                    adap.notifyDataSetInvalidated();
                    gv.invalidateViews();
                    sortBy = "vote_average";
                    loadGrid();
                }
                else{
                    showToast("Already Sorted");
                }
                return true;
            case R.id.sortByPopularity:
                if (!sortBy.equals("popularity")) {
                    adap.notifyDataSetInvalidated();
                    gv.invalidateViews();
                    sortBy = "popularity";
                    loadGrid();
                }else{
                    showToast("Already Sorted");
                }
                return true;
            case R.id.listFav:
                if (!sortBy.equals(ketForFavSort)) {
                    adap.notifyDataSetInvalidated();
                    gv.invalidateViews();
                    sortBy = "fav";
                    loadGrid();
                }else{
                    showToast("Already Sorted");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        dbH=new MyDBHandler(getApplication());
        msg = (TextView) findViewById(R.id.connectivityPopup);
        gv = (GridView) findViewById(R.id.grid_keeper);
        imgBtn = (ImageView) findViewById(R.id.menubtn);
        c = this;
        registerForContextMenu(imgBtn);
        imgBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        openContextMenu(imgBtn);
                    }
                }
        );


        if (isConnected()) {
            msg.setVisibility(View.GONE);
            loadGrid();
        } else {
            msg.setVisibility(View.VISIBLE);
        }
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.e("FavLoad", position + ":" + id);
                Result m;
                if(sortBy.equals(ketForFavSort)){
                   m= favPosters.get(position);
                }
                else {
                     m = output.getResults().get(position);
                }
                    Intent intent = new Intent(c, DetailActivity.class);
                    Gson g = new Gson();
                    String output = g.toJson(m, Result.class);
                    Log.e("output", output);
                    intent.putExtra(Constants.keyName, output);
                    startActivity(intent);

            }
        });

    }


    private void loadGrid() {

        if(!sortBy.equals(ketForFavSort))
        {
        Map<String, String> reqMap = new HashMap<>();
        reqMap.put("api_key", Constants.movieDbkey);
        reqMap.put("sort_by", sortBy + "." + sortOrd);
        Date now = new Date();
        String strDate = sdf.format(now);
        String url = getResources().getString(R.string.discoverMovie);
        url += Constants.getQueryParamFromMap(reqMap);
        url += "&release_date.lte=" + strDate + "&language=en";
        Log.e("Movie Central", url);
        new HttpAsyncTask().execute(url);
            return;
        }
        else{
            PosterView temp=new PosterView();
            favPosters=dbH.getFavMovies();
            int i=0;
            for(Result poster:favPosters)
            {
                Log.e("FavLoad",i+":"+poster.getId()+":"+poster.getTitle());
            }
            temp.setResults(favPosters);

            populateGrid(temp);
            return;
        }
        //RequestModel request=new RequestModel("loadGrid",getResources().getString(R.string.discoverMovie),reqMap,null);

    }

    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public String GET(String url) {
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
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private String convertInputStreamToString(InputStream inputStream) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            inputStream.close();
            return result;
        } catch (Exception e) {
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
            Gson g = new Gson();
            output = g.fromJson(result, PosterView.class);
            populateGrid(output);
        }
    }

    void populateGrid(PosterView data) {
        adap = new PosterViewAdapter(c, data);
        gv.setAdapter(adap);
    }

    @Override
     protected void onResume(){
        super.onResume();
        loadGrid();
        Log.e("resume","Activity resumed");
    }


}
