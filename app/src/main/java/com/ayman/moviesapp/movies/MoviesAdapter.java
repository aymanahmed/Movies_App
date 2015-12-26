package com.ayman.moviesapp.movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Mon on 11/28/2015.
 */
public class MoviesAdapter extends BaseAdapter {
    private String [] paths;
    private String [] posters;
    private Context mContext;
    private LayoutInflater inflater;
    public MoviesAdapter(Context c , String[] posters , LayoutInflater inflater){
        super();
        mContext = c;
        this.paths = posters;
        this.inflater = inflater;
    }
    @Override
    public int getCount() {
        return paths.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        getPosters();

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            convertView = inflater.inflate(R.layout.movie_grid_cell ,parent , false);
            imageView = (ImageView) convertView.findViewById(R.id.gridCell);
        } else {
            imageView = (ImageView) convertView;
        }
        Picasso.with(mContext).load(posters[position]).into(imageView);
        return imageView;
    }

    private void getPosters() {
        posters = new String[paths.length];
        for(int i = 0 ; i<paths.length ; i++){
            posters[i] = "http://image.tmdb.org/t/p/w185/" +paths[i] ;
        }
    }
}
