package com.ayman.moviesapp.movies.Data;

import android.provider.BaseColumns;

/**
 * Created by Mon on 12/23/2015.
 */
public class MoviesDBContract {

    public static class MovieTable implements BaseColumns {
        public static final String TABLE_NAME = "movies";
        public static final String COL_TITLE = "title";
        public static final String COL_OVERVIEW ="overview";
        public static final String COL_RELEASEDATE ="releasedate";
        public static final String COL_VOTE ="vote";
        public static final String COL_POSTERPATH ="posterpath";
        public static final String COL_MOVIE_ID ="movie_id";
    }

    public static class TrailerTable implements BaseColumns{
        public static final String TABLE_NAME = "trailers";
        public static final String COL_MOVIE_ID = "trailer_id";
        public static final String COL_TRAILER_NAME = "name";
        public static final String COL_TRAILER_PATH = "path";
    }

    public static class ReviewTable implements BaseColumns{
        public static final String TABLE_NAME = "reviews";
        public static final String COL_MOVIE_ID = "review_id";
        public static final String COL_REVIEW_AUTHOR = "author";
        public static final String COL_REVIEW_PATH = "path";
    }
}
