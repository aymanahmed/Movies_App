package com.ayman.moviesapp.movies;

/**
 * Created by Mon on 12/19/2015.
 */
public class Trailer {
    private String name;
    private String path;
    public Trailer(String name, String key){
        this.name = name;
        this.path = "https://www.youtube.com/watch?v="+key;
    }
    public Trailer(String name, String path,boolean x){
        this.name = name;
        this.path = path;
    }
    public String getName(){return name;}
    public String getPath(){return path;}
}
