package me.appfolio.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    public final static String MOVIE = "movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Movie movie = getIntent().getParcelableExtra(MOVIE);

        if (movie != null) {
            String poster = "http://image.tmdb.org/t/p/w342/" + movie.getPosterPath();
            Picasso.with(this).load(poster).into((ImageView) findViewById(R.id.image_poster));

            String title = movie.getOriginalTitle();
            String popularity = String.format(Locale.getDefault(), "%.2f", movie.getPopularity());
            String releaseDate = Utils.formatDate(movie.getReleaseDate());
            String overview = movie.getOverview();

            String voteAverage = String.valueOf(movie.getVoteAverage());
            String voteCount = String.valueOf(movie.getVoteCount());
            voteAverage = getString(R.string.vote_average, voteAverage, voteCount);

            ((TextView) findViewById(R.id.text_title)).setText(title);
            ((TextView) findViewById(R.id.text_date)).setText(releaseDate);
            ((TextView) findViewById(R.id.text_vote)).setText(voteAverage);
            ((TextView) findViewById(R.id.text_popularity)).setText(popularity);
            ((TextView) findViewById(R.id.text_overview)).setText(overview);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
