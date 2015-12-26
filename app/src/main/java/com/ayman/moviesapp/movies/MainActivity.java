package com.ayman.moviesapp.movies;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ayman.moviesapp.movies.MainActivityFragment.onMovieSelectedListener;


public class MainActivity extends ActionBarActivity implements onMovieSelectedListener{
    public static boolean  mTwoPane;
    private Movie mMovie;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.details_container ) != null){
            mTwoPane = true;

            if(savedInstanceState == null){
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.details_container , new MovieDetailsFragment(),MovieDetailsFragment.LOG_TAG)
                        .commit();
            }
        }
        else
            mTwoPane = false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onMovieSelected(Movie movieInfo) {
        MovieDetailsFragment df = (MovieDetailsFragment) getSupportFragmentManager().findFragmentByTag(MovieDetailsFragment.LOG_TAG);
        if(movieInfo != null)mMovie = movieInfo;
        if(mTwoPane){
            df.updateDetails(mMovie);
        }
        else {
            Intent intent = new Intent(getApplicationContext(),MovieDetailsActivity.class);
            intent.putExtra("title", movieInfo.getTitle());
            intent.putExtra("overview" , movieInfo.getOverview());
            intent.putExtra("posterPath" , movieInfo.getPosterPath());
            intent.putExtra("vote" ,movieInfo.getVote());
            intent.putExtra("releaseDate" ,movieInfo.getReleaseDate());
            intent.putExtra("id", movieInfo.getId());
            startActivity(intent);

        }
    }
}
