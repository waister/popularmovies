package me.appfolio.popularmovies;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, AdapterView.OnItemClickListener {

    /* The Movie DB API Key */
    public final static String THE_MOVIE_DB_KEY = "[YOUR THE MOVIE DB API KEY]";

    private final static String TAG = MainActivity.class.getSimpleName();
    public final static int IMAGE_WIDTH = 342;

    private GridView mGridMovies;
    private ProgressBar mProgressMovies;
    private TextView mTextMessage;
    private Button mButtonTryAgain;
    private SharedPreferences mPreferences;
    private String mMoviesSortBy = "";
    private String mLastMoviesSortBy = "";
    private MainActivity mContext = MainActivity.this;
    private List<Movie> mMoviesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        mGridMovies = (GridView) findViewById(R.id.list_movies);
        mProgressMovies = (ProgressBar) findViewById(R.id.progress_movies);
        mTextMessage = (TextView) findViewById(R.id.text_message);
        mButtonTryAgain = (Button) findViewById(R.id.button_try_again);

        mGridMovies.setOnItemClickListener(this);
        mButtonTryAgain.setOnClickListener(this);

        setGridColumns();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mMoviesSortBy = mPreferences.getString("movies_sort", "");

        if (mMoviesSortBy.isEmpty()) {
            mMoviesSortBy = (getResources().getStringArray(R.array.moviesSortValues))[0];

            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString("movies_sort", mMoviesSortBy);
            editor.apply();
        }

        if (!mMoviesSortBy.equals(mLastMoviesSortBy)) {
            checkMoviesApi();

            mLastMoviesSortBy = mMoviesSortBy;
        }
    }

    private void setGridColumns() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;
        int numColumns = deviceWidth / IMAGE_WIDTH;

        mGridMovies.setNumColumns(numColumns);
    }

    private void checkMoviesApi() {
        mProgressMovies.setVisibility(View.GONE);
        mButtonTryAgain.setVisibility(View.GONE);
        mGridMovies.setAdapter(null);

        if (validTmdbApiKey()) {
            mProgressMovies.setVisibility(View.VISIBLE);
            mTextMessage.setVisibility(View.GONE);

            if (Utils.isOnline(this)) {
                new CheckMoviesApiTask().execute();
            } else {
                showLoadError(getString(R.string.you_offline));
            }
        } else {
            mTextMessage.setText(R.string.tmdb_api_message);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        checkMoviesApi();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(mContext, DetailsActivity.class);
        intent.putExtra(DetailsActivity.MOVIE, mMoviesList.get(position));
        startActivity(intent);
    }

    private class CheckMoviesApiTask extends AsyncTask<Void, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Movie> doInBackground(Void... params) {
            mMoviesList.clear();

            String apiUrl = Uri.parse("http://api.themoviedb.org/3/movie/" + mMoviesSortBy)
                    .buildUpon()
                    .appendQueryParameter("api_key", THE_MOVIE_DB_KEY)
                    .build()
                    .toString();

            // Making a request to url and getting response
            String jsonStr = new HttpHandler().makeServiceCall(apiUrl);

            Log.w(TAG, "URL: " + apiUrl);
            Log.w(TAG, "RESPONSE: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("results");

                    // looping through all movies
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject apiMovie = contacts.getJSONObject(i);

                        Movie movie = new Movie();

                        movie.setTitle(apiMovie.getString("title"));
                        movie.setOriginalTitle(apiMovie.getString("original_title"));
                        movie.setOverview(apiMovie.getString("overview"));
                        movie.setPosterPath(apiMovie.getString("poster_path"));
                        movie.setReleaseDate(apiMovie.getString("release_date"));
                        movie.setPopularity(apiMovie.getDouble("popularity"));
                        movie.setVoteAverage(apiMovie.getDouble("vote_average"));
                        movie.setVoteCount(apiMovie.getInt("vote_count"));

                        // adding contact to contact list
                        mMoviesList.add(movie);
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                    showLoadError(e.getMessage());
                }
            } else {
                showLoadError(getString(R.string.server_connection_error));
            }

            return mMoviesList;
        }

        @Override
        protected void onPostExecute(List<Movie> result) {
            super.onPostExecute(result);

            if (result.size() > 0) {
                MoviesAdapter adapter = new MoviesAdapter(mContext, result);
                mGridMovies.setAdapter(adapter);
            } else {
                showLoadError(getString(R.string.no_movies_found));
            }

            mProgressMovies.setVisibility(View.GONE);
            mTextMessage.setVisibility(View.GONE);
            mButtonTryAgain.setVisibility(View.GONE);
        }

    }

    private void showLoadError(String message) {
        mProgressMovies.setVisibility(View.GONE);
        mTextMessage.setVisibility(View.VISIBLE);
        mButtonTryAgain.setVisibility(View.VISIBLE);

        mGridMovies.setAdapter(null);

        mTextMessage.setText(message);
    }

    private boolean validTmdbApiKey() {
        if (THE_MOVIE_DB_KEY.equals("[YOUR THE MOVIE DB API KEY]")) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setTitle(R.string.tmdb_api_title);
            dialog.setMessage(R.string.tmdb_api_message);
            dialog.setPositiveButton(R.string.tmdb_api_open, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.themoviedb.org/settings/api")));
                }
            });
            dialog.setNegativeButton(R.string.cancel, null);
            dialog.show();

            return false;
        }

        return true;
    }
}
