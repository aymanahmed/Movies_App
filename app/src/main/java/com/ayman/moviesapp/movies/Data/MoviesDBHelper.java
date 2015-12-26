package com.ayman.moviesapp.movies.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ayman.moviesapp.movies.Movie;
import com.ayman.moviesapp.movies.Review;
import com.ayman.moviesapp.movies.Trailer;

/**
 * Created by Mon on 12/23/2015.
 */
public class MoviesDBHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = MoviesDBHelper.class.getSimpleName();

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    private static final String SQL_CREATE_MOVIES_TABLE =
            "CREATE TABLE " + MoviesDBContract.MovieTable.TABLE_NAME + " (" +
                    MoviesDBContract.MovieTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MoviesDBContract.MovieTable.COL_MOVIE_ID + " TEXT UNIQUE," +
                    MoviesDBContract.MovieTable.COL_TITLE + " TEXT ,"+
                    MoviesDBContract.MovieTable.COL_OVERVIEW + " TEXT ,"+
                    MoviesDBContract.MovieTable.COL_VOTE + " TEXT ,"+
                    MoviesDBContract.MovieTable.COL_POSTERPATH + " TEXT ,"+
                    MoviesDBContract.MovieTable.COL_RELEASEDATE + " TEXT "+
            " )";

    private static final String SQL_CREATE_TRAILER_TABLE =
            "CREATE TABLE " + MoviesDBContract.TrailerTable.TABLE_NAME + " (" +
                    MoviesDBContract.TrailerTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MoviesDBContract.TrailerTable.COL_MOVIE_ID+ " TEXT ,"+
                    MoviesDBContract.TrailerTable.COL_TRAILER_NAME+ " TEXT ,"+
                    MoviesDBContract.TrailerTable.COL_TRAILER_PATH+ " TEXT"+
                    " )";

    private static final String SQL_CREATE_REVIEW_TABLE =
            "CREATE TABLE " + MoviesDBContract.ReviewTable.TABLE_NAME + " (" +
                    MoviesDBContract.ReviewTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MoviesDBContract.ReviewTable.COL_MOVIE_ID+ " TEXT ,"+
                    MoviesDBContract.ReviewTable.COL_REVIEW_AUTHOR+ " TEXT ,"+
                    MoviesDBContract.ReviewTable.COL_REVIEW_PATH+ " TEXT"+
                    " )";

    private static final String SQL_DELETE_MOVIE_TABLE =
            "DROP TABLE IF EXISTS " + MoviesDBContract.MovieTable.TABLE_NAME;
    private static final String SQL_DELETE_TRAILER_TABLE =
            "DROP TABLE IF EXISTS " + MoviesDBContract.TrailerTable.TABLE_NAME;
    private static final String SQL_DELETE_REVIEW_TABLE =
            "DROP TABLE IF EXISTS " + MoviesDBContract.ReviewTable.TABLE_NAME;

    public MoviesDBHelper(Context c){
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_MOVIE_TABLE);
        db.execSQL(SQL_DELETE_REVIEW_TABLE);
        db.execSQL(SQL_DELETE_TRAILER_TABLE);

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_MOVIE_TABLE);
        db.execSQL(SQL_DELETE_REVIEW_TABLE);
        db.execSQL(SQL_DELETE_TRAILER_TABLE);
        onCreate(db);
    }

    public void insertMovie(Movie mMovie) {
        removeMovie(mMovie.getId());
        insertMovieDetails(mMovie);
        insertTrailersDetails(mMovie);
        insertReviewsDetails(mMovie);

    }

    private void insertReviewsDetails(Movie mMovie) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(mMovie.getReviews() != null )
        for(Review r : mMovie.getReviews()){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MoviesDBContract.ReviewTable.COL_MOVIE_ID ,mMovie.getId());
            contentValues.put(MoviesDBContract.ReviewTable.COL_REVIEW_AUTHOR , r.getAuthor());
            contentValues.put(MoviesDBContract.ReviewTable.COL_REVIEW_PATH , r.getPath());
            db.insert(MoviesDBContract.ReviewTable.TABLE_NAME , null , contentValues);
        }
        db.close();
    }

    private void insertTrailersDetails(Movie mMovie) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(mMovie.getTrailers() != null)
        for (Trailer t : mMovie.getTrailers()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MoviesDBContract.TrailerTable.COL_MOVIE_ID , mMovie.getId());
            contentValues.put(MoviesDBContract.TrailerTable.COL_TRAILER_NAME , t.getName());
            contentValues.put(MoviesDBContract.TrailerTable.COL_TRAILER_PATH , t.getPath());
            db.insert(MoviesDBContract.TrailerTable.TABLE_NAME ,null ,contentValues);
        }
        db.close();
    }


    public void insertMovieDetails(Movie mMovie) {
        Log.w(LOG_TAG , "insert movie details");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesDBContract.MovieTable.COL_TITLE , mMovie.getTitle());
        contentValues.put(MoviesDBContract.MovieTable.COL_OVERVIEW , mMovie.getOverview());
        contentValues.put(MoviesDBContract.MovieTable.COL_MOVIE_ID , mMovie.getId());
        contentValues.put(MoviesDBContract.MovieTable.COL_POSTERPATH , mMovie.getPosterPath());
        contentValues.put(MoviesDBContract.MovieTable.COL_RELEASEDATE, mMovie.getReleaseDate());
        contentValues.put(MoviesDBContract.MovieTable.COL_VOTE, mMovie.getVote());

        db.insert(MoviesDBContract.MovieTable.TABLE_NAME, null, contentValues);
        db.close();
    }

    public int removeMovie(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int r = db.delete(MoviesDBContract.MovieTable.TABLE_NAME , MoviesDBContract.MovieTable.COL_MOVIE_ID +" = "+id , null);
        db.delete(MoviesDBContract.TrailerTable.TABLE_NAME , MoviesDBContract.TrailerTable.COL_MOVIE_ID +" = "+id , null);
        db.delete(MoviesDBContract.ReviewTable.TABLE_NAME , MoviesDBContract.ReviewTable.COL_MOVIE_ID +" = "+id , null);
        db.close();
        return r;
    }
}
