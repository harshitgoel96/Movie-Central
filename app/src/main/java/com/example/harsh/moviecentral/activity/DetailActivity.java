package com.example.harsh.moviecentral.activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.harsh.moviecentral.adapter.ReviewListAdapter;
import com.example.harsh.moviecentral.adapter.TrailerViewAdapter;
import com.example.harsh.moviecentral.model.ReviewModel;
import com.example.harsh.moviecentral.model.VideosModel;
import com.example.harsh.moviecentral.utils.Constants;
import com.example.harsh.moviecentral.R;
import com.example.harsh.moviecentral.model.Result;
import com.example.harsh.moviecentral.utils.MyDBHandler;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by harsh on 12/24/2015.
 */
public class DetailActivity extends Activity {
    ListView trailerList;
    ListView reviewList;
    VideosModel videosModel;
    ReviewModel reviewModel;
    Result data;
    TextView trailer_l;
    TextView revi_l;
    MyDBHandler dbHandler;
    ImageView likeBtn;
    private Context c;

    class GetParams {
        private String url;
        private String action;

        public GetParams(String url, String action) {
            this.url = url;
            this.action = action;
        }

        public String getUrl() {
            return url;
        }

        public String getAction() {
            return action;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.movie_details);
        this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        c = this;
         dbHandler=new MyDBHandler(getApplication());
        ((ImageView) findViewById(R.id.menubtn)).setVisibility(View.INVISIBLE);
        trailerList = (ListView) findViewById(R.id.trailer_list);
        reviewList = (ListView) findViewById(R.id.review_list);
        trailer_l = (TextView) findViewById(R.id.train_label);
        revi_l = (TextView) findViewById(R.id.rev_label);
        likeBtn=(ImageView)findViewById(R.id.likeBtn);
        Bundle bundle = getIntent().getExtras();
        //int value = bundle.getInt("some_key");
        String str = bundle.getString(Constants.keyName);
        Log.e("Str", str);
        Gson g = new Gson();
        data = g.fromJson(str, Result.class);
        ImageView backDrop = (ImageView) findViewById(R.id.BackDrop);
        ImageView thumbnail = (ImageView) findViewById(R.id.thumbnail);
        TextView summry = (TextView) findViewById(R.id.summry);
        TextView title = (TextView) findViewById(R.id.detail_title);
        TextView releaseDate = (TextView) findViewById(R.id.release_date);
        //TextView movieLength=(TextView) findViewById(R.id.length);
        TextView rating = (TextView) findViewById(R.id.rating_text);
        if (data.getPosterPath() != null)
            Picasso.with(this).load(Constants.imageBase + data.getPosterPath()).into(thumbnail);
        if (data.getBackdropPath() != null)
            Picasso.with(this).load(Constants.imageBase + data.getBackdropPath()).into(backDrop);
        summry.setText(data.getOverview());
        title.setText(data.getTitle());
        releaseDate.setText(data.getReleaseDate());
        //movieLength.setText(data.get);
        rating.setText(String.format("%.2f", data.getVoteAverage()) + "/10");
        updateFavImg();
        likeBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleFav();
                    }
                }
        );
        LoadTrailers();
        LoadReviews();

    }

    //add to fav
    private void updateFavImg(){
        if(dbHandler.isFav(data.getId())){
            likeBtn.setImageResource(R.drawable.like);
        }
        else
        {
            likeBtn.setImageResource(R.drawable.unlike);
        }

    }
    private void toggleFav(){
        if(dbHandler.isFav(data.getId()))
        {
            //delete
            dbHandler.deleteFav(data.getId());
            likeBtn.setImageResource(R.drawable.unlike);
        }
        else{
            dbHandler.addFavMovie(data);
            likeBtn.setImageResource(R.drawable.like);
        }
        updateFavImg();
    }

    //starting stage2
    private void LoadReviews() {

        new HttpAsyncTask().execute(new GetParams(Constants.getReviewLink(String.valueOf(data.getId())), "GetReviews"));
    }

    private void LoadTrailers() {
        new HttpAsyncTask().execute(new GetParams(Constants.getVideoLink(String.valueOf(data.getId())), "GetVideos"));
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


    private class HttpAsyncTask extends AsyncTask<GetParams, Void, String> {
        String action;

        @Override
        protected String doInBackground(GetParams... data) {

            action = data[0].getAction();
            return GET(data[0].getUrl());
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if (action.equals("GetVideos")) {
                Log.e("GetVideos", result);
                Gson g = new Gson();

                videosModel = g.fromJson(result, VideosModel.class);
                //Log.e("TrailerView", String.valueOf(videosModel));

                if (videosModel.getResults().size() == 0) {
                    trailer_l.setVisibility(View.GONE);

                } else {
                    populateTrailerList(videosModel);
                }

            } else if (action.equals("GetReviews")) {
                Gson g = new Gson();

                reviewModel = g.fromJson(result, ReviewModel.class);
                if (reviewModel.getResults().size() == 0) {
                    revi_l.setVisibility(View.GONE);
                } else {
                    populateReviewList(reviewModel);
                }
            }

        }
    }

    void populateTrailerList(VideosModel vid) {
        // Log.e("Trailers","Trying to set TrailerList");
        TrailerViewAdapter tVA = new TrailerViewAdapter(c, vid);
        trailerList.setAdapter(tVA);
        setListViewHeightBasedOnChildren(trailerList);

    }

    void populateReviewList(ReviewModel rev) {
        ReviewListAdapter rVA = new ReviewListAdapter(c, rev);
        reviewList.setAdapter(rVA);
        setListViewHeightBasedOnChildren(reviewList);

    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        BaseAdapter listAdapter = (BaseAdapter) listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        Log.e("height", String.valueOf(params.height));
        listView.setLayoutParams(params);
    }

}
