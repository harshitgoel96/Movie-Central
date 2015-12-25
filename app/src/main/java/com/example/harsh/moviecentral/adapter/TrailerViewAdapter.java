package com.example.harsh.moviecentral.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.harsh.moviecentral.R;
import com.example.harsh.moviecentral.model.Video;
import com.example.harsh.moviecentral.model.VideosModel;
import com.example.harsh.moviecentral.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by harsh on 12/25/2015.
 */
public class TrailerViewAdapter extends BaseAdapter {

    Context mContext;
    VideosModel videos;
    List<Video> videoItems;

    @Override
    public int getCount() {
        return videoItems.size();
    }

    @Override
    public Object getItem(int position) {



        return videoItems.get(position);
    }

    @Override
    public long getItemId(int position) {


        return Long.getLong(videoItems.get(position).getId());
    }

    public TrailerViewAdapter(Context context, VideosModel _videos) {
        Log.e("URLS","Init");
       mContext = context;
        videos = _videos;
        videoItems = _videos.getResults();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("URLS","getV");

        if(convertView == null){
            convertView = ((Activity)mContext).getLayoutInflater().inflate(R.layout.trailer_item, null);
        }

        Video vid=videoItems.get(position);
        ImageView trailerIcon=(ImageView)convertView.findViewById(R.id.trailerIcon);
        TextView trailerName=(TextView)convertView.findViewById(R.id.trailerText);
        final String youtubeUrl= Constants.getYoutubeVideoLink(vid.getKey());
        final String youtubeImg= Constants.getYoutubeVideoImage(vid.getKey());
        Log.e("URLS", youtubeUrl + ";" + youtubeImg);
        Picasso.with(mContext).load(youtubeImg).into(trailerIcon);
        trailerName.setText(vid.getName());
        convertView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(youtubeUrl));
                        mContext.startActivity(i);
                    }
                }
        );
        return convertView;
    }
}
