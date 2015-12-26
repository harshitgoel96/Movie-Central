package com.example.harsh.moviecentral.utils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.harsh.moviecentral.model.Result;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harsh on 12/26/2015.
 */
public class MyDBHandler extends SQLiteAssetHelper {


    public MyDBHandler(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }
private void createTable(){

}

    public boolean addFavMovie(Result movie) {

        ContentValues values = new ContentValues();

        //values.put("firstname", movie.firstname);
        //values.put("email", movie.email);

        values.put("adult", (movie.getAdult()) ? 1 : 0);
        values.put("video", (movie.getVideo()) ? 1 : 0);
        values.put("popularity", movie.getPopularity());
        values.put("voteAverage", movie.getVoteAverage());
        values.put("id", movie.getId());
        values.put("voteCount", movie.getVoteCount());
        values.put("backdropPath", (String) movie.getBackdropPath());
        values.put("posterPath", (String) movie.getPosterPath());
        values.put("originalLanguage", movie.getOriginalLanguage());
        values.put("originalTitle", movie.getOriginalTitle());
        values.put("overview", movie.getOverview());
        values.put("releaseDate", movie.getReleaseDate());
        values.put("title", movie.getTitle());

        SQLiteDatabase db = this.getWritableDatabase();

        boolean createSuccessful = db.insert("fav_move", null, values) > 0;
        db.close();

        return createSuccessful;
    }


    public List<Result> getFavMovies() {
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor c= db.rawQuery("Select * from fav_move",null);
        Cursor c = db.query("fav_move", Constants.fav_mov_cols, null, null, null, null, null);
        ArrayList<Result> resultsList = new ArrayList<>();
        while (c.moveToNext()) {
            //0-12
            Result temp = new Result(c);
            resultsList.add(temp);

        }
        return resultsList;
    }

    public int count() {

        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT count(*) FROM fav_move";
        int recordCount;
        Cursor c = db.rawQuery(sql, null);

        if (c.moveToNext()) {
            recordCount = c.getInt(0);
        } else {
            recordCount = 0;
        }
        // /
        // int recordCount = db.rawQuery(sql, null).getInt(0);
        db.close();

        return recordCount;

    }

    public boolean isFav(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] ids = {String.valueOf(id)};
        Cursor c = db.query("fav_move", null, "id=?", ids, null, null, null);

        if (c.getCount() > 0) {
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }
    }
    public boolean deleteFav(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] ids = {String.valueOf(id)};
        int r=db.delete("fav_move","id=?",ids);
        db.close();
        return  r>0;
    }
}
