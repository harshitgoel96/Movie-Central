package com.example.harsh.moviecentral.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.harsh.moviecentral.utils.Constants;
import com.example.harsh.moviecentral.R;
import com.example.harsh.moviecentral.model.PosterView;
import com.example.harsh.moviecentral.model.Result;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by harsh on 12/24/2015.
 */
public class PosterViewAdapter extends BaseAdapter {
    private Context mContext;
    private PosterView mData;

    private List<Result> mResult;

    @Override
    public int getCount() {
        return mResult.size();
    }

    @Override
    public Object getItem(int position) {
        return mResult.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mResult.get(position).getId();
    }
    public PosterViewAdapter(Context context,PosterView data)
    {
        this.mData=data;
        this.mContext=context;
        this.mResult=this.mData.getResults();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = ((Activity)mContext).getLayoutInflater().inflate(R.layout.movie_poster_item, null);
        }
        Result single=mResult.get(position);
        if(single.getPosterPath()!=null) {
            ImageView posterImage = (ImageView) convertView.findViewById(R.id.posterImage);
            String imageUrl = Constants.imageBase + single.getPosterPath();
            Picasso.with(mContext).load(imageUrl).into(posterImage);
        }
       TextView titleView=(TextView)convertView.findViewById(R.id.movieTitle);
        titleView.setText(single.getTitle());
/*
        TextView rating=(TextView)convertView.findViewById(R.id.ratingText);
        rating.setText(String.format("%.2f", single.getPopularity()));*/

        return convertView;
    }
}