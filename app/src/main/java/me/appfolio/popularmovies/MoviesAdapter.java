package me.appfolio.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class MoviesAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Movie> movies = new ArrayList<>();

    MoviesAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
        this.inflater = LayoutInflater.from(context);
    }

    public void setData(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public int getCount() {
        return movies.size();
    }

    public Movie getItem(int position) {
        return movies.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_movie, parent, false);
            holder = new ViewHolder();

            holder.imageMovie = (ImageView) convertView.findViewById(R.id.image_movie);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageMovie.setContentDescription(movie.getTitle());

        Picasso.with(context)
                .load("http://image.tmdb.org/t/p/w342/" + movie.getPosterPath())
                .resize(342, 501)
                .centerCrop()
                .into(holder.imageMovie);

        return convertView;
    }

    private class ViewHolder {
        ImageView imageMovie;
    }
}