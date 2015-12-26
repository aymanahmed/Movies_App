package com.ayman.moviesapp.movies;


import android.content.Intent;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ayman.moviesapp.movies.Data.MoviesDBContract;
import com.ayman.moviesapp.movies.Data.MoviesDBHelper;
import com.squareup.picasso.Picasso;

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
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment {
    public final static String LOG_TAG = MovieDetailsFragment.class.getSimpleName();


    private MoviesDBHelper dbHelper;
    private ArrayAdapter<String> trailersAdapter;
    private ArrayAdapter<String> reviewsAdapter;
    private Movie mMovie;
    private ListView trailersListView;
    private ListView reviewsListView;
    private View rootView;
    public MovieDetailsFragment() {    }



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_details, container, false);
        trailersListView = (ListView)rootView.findViewById(R.id.trailers_list);
        reviewsListView = (ListView)rootView.findViewById(R.id.reviews_list);
        dbHelper = new MoviesDBHelper(getActivity().getApplicationContext());

        Intent intent = getActivity().getIntent();
        if(MainActivity.mTwoPane){
            return rootView;
        }

        mMovie = new Movie(
                intent.getStringExtra("title"),
                intent.getStringExtra("overview"),
                intent.getStringExtra("releaseDate"),
                intent.getStringExtra("vote"),
                intent.getStringExtra("posterPath"),
                intent.getStringExtra("id")
        );
        updateDetails(mMovie);
        return rootView;
    }


    public void updateDetails(Movie movieInfo){
        if(movieInfo != null)
            mMovie = movieInfo;

        TextView title = (TextView)rootView.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(mMovie.getTitle());

        TextView overVeiw = (TextView)rootView.findViewById(R.id.overview);
        overVeiw.setText( mMovie.getOverview());

        TextView release = (TextView)rootView.findViewById(R.id.releaseDate);
        release.setText("Release date : " + mMovie.getReleaseDate());

        TextView vote = (TextView)rootView.findViewById(R.id.voteAvg);
        vote.setText("Vote average : " + mMovie.getVote());

        ImageView poster = (ImageView)rootView.findViewById(R.id.poster);
        Picasso.with(getActivity()).load(mMovie.getPosterPath()).into(poster);

        ((TextView)rootView.findViewById(R.id.reviews_header)).setVisibility(View.VISIBLE);
        ((TextView)rootView.findViewById(R.id.trailers_header)).setVisibility(View.VISIBLE);

        ToggleButton favorite = (ToggleButton) rootView.findViewById(R.id.favorite_btn);
        favorite.setVisibility(View.VISIBLE);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(MoviesDBContract.MovieTable.TABLE_NAME ,null ,
                MoviesDBContract.MovieTable.COL_MOVIE_ID +" = "+mMovie.getId() ,null,null,null,null );
        if(c.getCount() != 0)favorite.setChecked(true);
        else favorite.setChecked(false);
        c.close();
        db.close();
        favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.w(LOG_TAG, "clicked");
                    dbHelper.insertMovie(mMovie);
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String sort = pref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_pop));
                    if (sort.equals(getString(R.string.pref_sort_favorite)) && MainActivity.mTwoPane) {
                        MainActivityFragment f = (MainActivityFragment) getFragmentManager().findFragmentById(R.id.fragment);
                        f.updateMovies();
                    }

                } else {
                    dbHelper.removeMovie(mMovie.getId());
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String sort = pref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_pop));
                    if (sort.equals(getString(R.string.pref_sort_favorite)) && MainActivity.mTwoPane) {
                        MainActivityFragment f = (MainActivityFragment) getFragmentManager().findFragmentById(R.id.fragment);
                        f.updateMovies();
                    }
                }
            }
        });

        ScrollView s = (ScrollView)rootView.findViewById(R.id.scroll);
        s.scrollTo(0,0);
        FetchTrailers fetchTrailers = new FetchTrailers();
        fetchTrailers.execute(mMovie.getId());

        FetchReviews fetchReviews = new FetchReviews();
        fetchReviews.execute(mMovie.getId());


    }

    public class FetchTrailers extends AsyncTask<String, Void, String[]> {


        private final String LOG_TAG = FetchTrailers.class.getSimpleName();

        @Override
        protected void onPostExecute(String[] strings) {
            Log.w(LOG_TAG, "on post execute");

            trailersAdapter = new ArrayAdapter<String>(getActivity(),R.layout.trailer_list_item,R.id.trailer_name ,mMovie.getTrailersNames());
            trailersListView.setAdapter(trailersAdapter);
            trailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mMovie.getTrailers()[position].getPath())));
                }
            });
            Utilities.setDynamicHeight(trailersListView);


        }
        protected String[] getTrailersFromJson(String moviesJsonStr)throws JSONException {
            Log.w(LOG_TAG , moviesJsonStr);

            JSONObject jTrailerObj = new JSONObject(moviesJsonStr);
            JSONArray jTrailerArray = jTrailerObj.getJSONArray("results");
            Trailer[] trailers = new Trailer[jTrailerArray.length()];
            for(int i = 0 ; i<jTrailerArray.length() ; i++){
                trailers[i] = new Trailer(
                        jTrailerArray.getJSONObject(i).getString("name"),
                        jTrailerArray.getJSONObject(i).getString("key")
                );

            }
            mMovie.setTrailers(trailers);

            return null;
        }
        //the end
        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String id = params[0];

                URL url = new URL("https://api.themoviedb.org/3/movie/"+id+"/videos?api_key=7ced4a8217ca6678a46ac06ee0842f7e");
                Log.w("my trailers link", url.toString());

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
                    return getTrailersFromJson(moviesJsonStr);
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

    public class FetchReviews extends AsyncTask<String, Void, String[]> {


        private final String LOG_TAG = FetchTrailers.class.getSimpleName();

        @Override
        protected void onPostExecute(String[] strings) {
            Log.w(LOG_TAG, "on post execute");

            reviewsAdapter = new ArrayAdapter<String>(getActivity(),R.layout.review_list_item,R.id.author_name ,mMovie.getReviewsAuthors());
            reviewsListView.setAdapter(reviewsAdapter);
            reviewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mMovie.getReviews()[position].getPath())));
                }
            });
            Utilities.setDynamicHeight(reviewsListView);


        }
        protected String[] getTrailersFromJson(String moviesJsonStr)throws JSONException {
            Log.w(LOG_TAG , moviesJsonStr);

            JSONObject jReviewObj = new JSONObject(moviesJsonStr);
            JSONArray jReviewArray = jReviewObj.getJSONArray("results");
            Review[] reviews = new Review[jReviewArray.length()];
            for(int i = 0 ; i<jReviewArray.length() ; i++){
                reviews[i] = new Review(
                        jReviewArray.getJSONObject(i).getString("author"),
                        jReviewArray.getJSONObject(i).getString("id")
                );

            }
            mMovie.setReviews(reviews);

            return null;
        }
        //the end
        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String id = params[0];

                URL url = new URL("https://api.themoviedb.org/3/movie/"+id+"/reviews?api_key=7ced4a8217ca6678a46ac06ee0842f7e");
                Log.w("my reviews link", url.toString());

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
                    return getTrailersFromJson(moviesJsonStr);
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
}
