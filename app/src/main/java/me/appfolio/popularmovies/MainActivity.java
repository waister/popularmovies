package me.appfolio.popularmovies;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, AdapterView.OnItemClickListener, AsyncTaskCompleteListener<List<Movie>> {

    private final static String TAG = MainActivity.class.getSimpleName();

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
            Log.w(TAG, "Preference sort is empty, take first from list:  '" + mMoviesSortBy + "'");

            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString("movies_sort", mMoviesSortBy);
            editor.apply();
        }

        if (!mMoviesSortBy.equals(mLastMoviesSortBy)) {
            Log.w(TAG, "Preference sort change from '" + mLastMoviesSortBy + "' to '" + mMoviesSortBy + "'");

            checkMoviesApi();

            mLastMoviesSortBy = mMoviesSortBy;
        }
    }

    private void setGridColumns() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float deviceWidth = Utils.pxToDp(this, displayMetrics.widthPixels);

        int numColumns = (int) deviceWidth / 120;
        if (numColumns < 2) numColumns = 2;

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
                new CheckMoviesApiTask(mMoviesSortBy, this).execute();
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

    @Override
    public void onTaskComplete(List<Movie> result) {
        if (result != null) {
            mMoviesList = result;

            if (result.size() > 0) {
                MoviesAdapter adapter = new MoviesAdapter(mContext, result);
                mGridMovies.setAdapter(adapter);
            } else {
                showLoadError(getString(R.string.no_movies_found));
            }

            mTextMessage.setVisibility(View.GONE);
            mButtonTryAgain.setVisibility(View.GONE);
        } else {
            showLoadError(getString(R.string.server_connection_error));
        }

        mProgressMovies.setVisibility(View.GONE);
    }

    private void showLoadError(String message) {
        mProgressMovies.setVisibility(View.GONE);
        mTextMessage.setVisibility(View.VISIBLE);
        mButtonTryAgain.setVisibility(View.VISIBLE);

        mGridMovies.setAdapter(null);

        mTextMessage.setText(message);
    }

    private boolean validTmdbApiKey() {
        if (BuildConfig.THE_MOVIE_DB_API_KEY.equals("[YOUR THE MOVIE DB API KEY]")) {
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
