package me.appfolio.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class CheckMoviesApiTask extends AsyncTask<Void, Void, List<Movie>> {

    private static final String TAG = CheckMoviesApiTask.class.getSimpleName();

    private String sortBy;
    private AsyncTaskCompleteListener<List<Movie>> listener;

    CheckMoviesApiTask(String sortBy, AsyncTaskCompleteListener<List<Movie>> listener) {
        this.sortBy = sortBy;
        this.listener = listener;
    }

    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Movie> doInBackground(Void... params) {
        String apiUrl = Uri.parse("http://api.themoviedb.org/3/movie/" + sortBy)
                .buildUpon()
                .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY)
                .build()
                .toString();

        String jsonStr = new HttpHandler().makeServiceCall(apiUrl);

        Log.w(TAG, "API URL: " + apiUrl);
        Log.w(TAG, "API response: " + jsonStr);

        if (jsonStr != null) {
            try {
                List<Movie> moviesList = new ArrayList<>();
                JSONObject jsonObj = new JSONObject(jsonStr);

                JSONArray contacts = jsonObj.getJSONArray("results");

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

                    moviesList.add(movie);
                }

                return moviesList;
            } catch (final JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Movie> result) {
        super.onPostExecute(result);
        listener.onTaskComplete(result);
    }
}
