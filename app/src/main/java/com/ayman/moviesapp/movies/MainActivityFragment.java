package com.ayman.moviesapp.movies;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.ayman.moviesapp.movies.Data.MoviesDBContract;
import com.ayman.moviesapp.movies.Data.MoviesDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    public static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private ArrayAdapter<String> moviesAdapter;
    private Movie[] moviesObjs;
    GridView moviesGrid;
    private int pos;
    onMovieSelectedListener mOnMovieSelectedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnMovieSelectedListener = (onMovieSelectedListener) activity;
    }

    public interface onMovieSelectedListener{
        public void onMovieSelected(Movie MovieInfo);
    }
    public MainActivityFragment() {
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null)
            pos = savedInstanceState.getInt("pos");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        moviesGrid = (GridView) rootView.findViewById(R.id.movies_grid);
        return rootView;
    }


    public void onStart() {
        super.onStart();

        updateMovies();
    }

    public  void updateMovies(){
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = pref.getString(getString(R.string.pref_sort_key),getString(R.string.pref_sort_pop));

        if(sort.equals(getString(R.string.pref_sort_rate))) {
            fetchMoviesTask.execute("vote_average.desc");

        }
        else if(sort.equals(getString(R.string.pref_sort_pop))) {
            fetchMoviesTask.execute("popularity.desc");
        }
        else{
            SQLiteDatabase db = new MoviesDBHelper(getActivity().getApplicationContext()).getReadableDatabase();
            Cursor c = db.query(MoviesDBContract.MovieTable.TABLE_NAME, new String[]{MoviesDBContract.MovieTable.COL_POSTERPATH},
                    null, null, null, null, null);
            String[] posters = new String[c.getCount()];
            c.moveToFirst();
            for(int i=0;i<c.getCount();i++){
                c.moveToPosition(i);
                posters[i] = c.getString(c.getColumnIndex(MoviesDBContract.MovieTable.COL_POSTERPATH));
            }
            c.close();
            db.close();
            MoviesAdapter adap = new MoviesAdapter(getActivity() , posters , getActivity().getLayoutInflater());
            moviesGrid.setAdapter(adap);

            moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    pos = position;

                    SQLiteDatabase db = new MoviesDBHelper(getActivity().getApplicationContext()).getReadableDatabase();
                    Cursor c = db.query(MoviesDBContract.MovieTable.TABLE_NAME,null,
                            null ,
                            null,null,null,null);
                    c.moveToPosition(position);
                    Movie mMovie = new Movie(
                        c.getString(c.getColumnIndex(MoviesDBContract.MovieTable.COL_TITLE)),
                        c.getString(c.getColumnIndex(MoviesDBContract.MovieTable.COL_OVERVIEW)),
                        c.getString(c.getColumnIndex(MoviesDBContract.MovieTable.COL_RELEASEDATE)),
                        c.getString(c.getColumnIndex(MoviesDBContract.MovieTable.COL_VOTE)),
                        c.getString(c.getColumnIndex(MoviesDBContract.MovieTable.COL_POSTERPATH)),
                        c.getString(c.getColumnIndex(MoviesDBContract.MovieTable.COL_MOVIE_ID)));
                    c.close();
                    db.close();
                    mOnMovieSelectedListener.onMovieSelected(mMovie);
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("pos" , pos);
        super.onSaveInstanceState(outState);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {


        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                Log.w(LOG_TAG,"inside onPostExecute");
                MoviesAdapter adap = new MoviesAdapter(getActivity() , strings , getActivity().getLayoutInflater());
                moviesGrid.setAdapter(adap);
                moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        Movie movieInfo = new Movie(
                            moviesObjs[position].getTitle(),
                            moviesObjs[position].getOverview(),
                            moviesObjs[position].getReleaseDate(),
                            moviesObjs[position].getVote(),
                            moviesObjs[position].getPosterPath(),
                            moviesObjs[position].getId());
                        mOnMovieSelectedListener.onMovieSelected(movieInfo);
                    }
                });
            }
        }
        protected String[] getMoviesNamesFromJson(String moviesJsonStr)throws JSONException{
            Log.w(LOG_TAG, "inside getMoviesNamesFromJson");
            Log.w(LOG_TAG,moviesJsonStr);
            JSONObject jMovies = new JSONObject(moviesJsonStr);
            JSONArray jMoviesArray = jMovies.getJSONArray("results");
            String[] moviesPosters = new String[jMoviesArray.length()];
            moviesObjs = new Movie[jMoviesArray.length()];
            for(int i=0 ; i< jMoviesArray.length() ; i++){
                moviesPosters[i] = jMoviesArray.getJSONObject(i).getString("poster_path");
                //    public Movie(String title , String overview , String releaseDate , String vote , String posterPath ){

                moviesObjs[i] = new Movie(
                        jMoviesArray.getJSONObject(i).getString("title"),
                        jMoviesArray.getJSONObject(i).getString("overview"),
                        jMoviesArray.getJSONObject(i).getString("release_date"),
                        jMoviesArray.getJSONObject(i).getString("vote_average"),
                        jMoviesArray.getJSONObject(i).getString("poster_path"),
                        jMoviesArray.getJSONObject(i).getString("id")

                );
            }


            return  moviesPosters;
        }
        //the end
        @Override
        protected String[] doInBackground(String... params) {
            Log.w(LOG_TAG,"in side doInBackGround");
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
            Log.w(LOG_TAG,"inside doInBackground before try block");

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
                final String API_KEY_PARAM = "api_key";
                final String API_PAGE_NUM = "page";
                final String API_SORT_BY = "sort_by";

                String apiKey = "7ced4a8217ca6678a46ac06ee0842f7e";
                int pageNum = 1;
                String sort = params[0] ;
                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .appendQueryParameter(API_PAGE_NUM, pageNum+"")
                        .appendQueryParameter(API_SORT_BY,sort).build();
                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                try {
                    return getMoviesNamesFromJson(moviesJsonStr);
                } catch (JSONException e) {
                    Log.e("error in the return", e.toString());
                }

            } catch (IOException e) {
                Log.e("MoviesFragmentError", e.toString());
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ForecastFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mainactivityfragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if( id == R.id.action_refresh){
            updateMovies();
        }

        return super.onOptionsItemSelected(item);
    }
}
