package com.example.harsh.moviecentral;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

/**
 * Created by harsh on 12/24/2015.
 */
public class DetailActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.movie_details);
        this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        ((ImageView)findViewById(R.id.menubtn)).setVisibility(View.INVISIBLE);
        Bundle bundle = getIntent().getExtras();
        //int value = bundle.getInt("some_key");
        String str=bundle.getString(Constants.keyName);
        Log.e("Str", str);
        Gson g=new Gson();
        Result data= g.fromJson(str, Result.class);
        ImageView backDrop= (ImageView) findViewById(R.id.BackDrop);
        ImageView thumbnail=(ImageView) findViewById(R.id.thumbnail);
        TextView summry=(TextView) findViewById(R.id.summry);
        TextView title=(TextView) findViewById(R.id.detail_title);
        TextView releaseDate=(TextView) findViewById(R.id.release_date);
        //TextView movieLength=(TextView) findViewById(R.id.length);
        TextView rating=(TextView) findViewById(R.id.rating_text);
        if(data.getPosterPath()!=null)
        Picasso.with(this).load(Constants.imageBase+data.getPosterPath()).into(thumbnail);
        if(data.getBackdropPath()!=null)
        Picasso.with(this).load(Constants.imageBase+data.getBackdropPath()).into(backDrop);
        summry.setText(data.getOverview());
        title.setText(data.getTitle());
        releaseDate.setText(data.getReleaseDate());
        //movieLength.setText(data.get);
        rating.setText(String.format("%.2f",data.getVoteAverage())+"/10");

    }
}
