package com.ayman.moviesapp.movies;

/**
 * Created by Mon on 11/28/2015.
 */
public class Movie {
    private String title;
    private String overview;
    private String releaseDate;
    private String  vote;
    private String posterPath;
    private String id;
    private Trailer[] trailers;
    private Review[] reviews;
    public Movie(String title, String overview, String releaseDate, String vote, String posterPath, String id){
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.vote = vote;
        if(posterPath.contains("http://"))
            this.posterPath = posterPath;
        else
            this.posterPath = "http://image.tmdb.org/t/p/w185/"+ posterPath;
        this.id = id;
    }
    public String getTitle(){return title;}
    public String getOverview(){return overview;}
    public String getReleaseDate(){return releaseDate;}
    public String getVote(){return vote;}
    public String getPosterPath(){return posterPath;}
    public String getId(){return id;}

    public Trailer[] getTrailers() {
        return trailers;
    }

    public String[] getTrailersNames(){
        String[] names = new String[trailers.length];
        for (int i = 0;i<trailers.length;i++)
            names[i] = trailers[i].getName();
        return names;
    }
    public String[] getTrailersPaths(){
        String[] names = new String[trailers.length];
        for (int i = 0;i<trailers.length;i++)
            names[i] = trailers[i].getPath();
        return names;
    }

    public void setTrailers(Trailer[] trailers){
        this.trailers = trailers;
    }
    public void setTrailers(String[] names , String[] paths){
        this.trailers = new Trailer[names.length];
        for(int i=0;i<names.length;i++){
            trailers[i] = new Trailer(names[i] , paths[i] ,true);
        }
    }

    public Review[] getReviews(){return reviews;}

    public String[] getReviewsAuthors(){
        String[] names = new String[reviews.length];
        for (int i = 0;i<reviews.length;i++)
            names[i] = reviews[i].getAuthor();
        return names;
    }
    public String[] getReviewsPaths(){
        String[] names = new String[reviews.length];
        for (int i = 0;i<reviews.length;i++)
            names[i] = reviews[i].getPath();
        return names;
    }

    public void setReviews(Review[] reviews){this.reviews = reviews;}
    public void setReviews(String[] authors , String[] paths){
        this.reviews = new Review[authors.length];
        for(int i=0;i<authors.length;i++){
            reviews[i] = new Review(authors[i] , paths[i] ,true);
        }
    }



}
