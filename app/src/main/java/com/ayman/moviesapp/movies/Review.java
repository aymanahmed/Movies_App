package com.ayman.moviesapp.movies;

/**
 * Created by Mon on 12/22/2015.
 */
public class Review {
    private String author;
    private String path;
    public Review(String name, String id){
        this.author = name;
        this.path = "https://www.themoviedb.org/review/"+id;
    }
    public Review(String name, String path , boolean x){
        this.author = name;
        this.path = path;
    }
    public String getAuthor(){return author;}
    public String getPath(){return path;}
}
