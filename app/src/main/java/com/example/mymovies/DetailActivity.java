package com.example.mymovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mymovies.adapters.ReviewAdapter;
import com.example.mymovies.adapters.TrailerAdapter;
import com.example.mymovies.data.FavouriteMovie;
import com.example.mymovies.data.MainViewModel;
import com.example.mymovies.data.Movie;
import com.example.mymovies.data.Review;
import com.example.mymovies.data.Trailer;
import com.example.mymovies.databinding.ActivityDetailBinding;
import com.example.mymovies.utils.JSONUtils;
import com.example.mymovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;

    private Movie movie;
    private FavouriteMovie favouriteMovie;

    private MainViewModel mainViewModel;
    private int id = -1;

    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;

    private static String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        lang = Locale.getDefault().getLanguage();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        } else {
            Toast.makeText(getApplicationContext(), "Ошибка: детальное описание фильма недоступно", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (id != -1) {
            mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
            movie = mainViewModel.getMovieById(id);
            setMovie();
        }

        setFavourite();

        reviewAdapter = new ReviewAdapter();
        binding.movieInfo.recyclerViewOverviews.setLayoutManager(new LinearLayoutManager(this));
        binding.movieInfo.recyclerViewOverviews.setAdapter(reviewAdapter);
        JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId(), lang);
        ArrayList<Review> reviewsArrayList = JSONUtils.getReviewsFromJSON(jsonObjectReviews);
        reviewAdapter.setReviewList(reviewsArrayList);

        trailerAdapter = new TrailerAdapter();
        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToTrailer);
            }
        });
        binding.movieInfo.recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        binding.movieInfo.recyclerViewTrailers.setAdapter(trailerAdapter);
        JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVideos(movie.getId(), lang);
        ArrayList<Trailer> trailerArrayList = JSONUtils.getTrailersFromJSON(jsonObjectTrailers);
        trailerAdapter.setTrailerList(trailerArrayList);

        binding.imageViewAddToFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favouriteMovie == null) {
                    mainViewModel.insertFavouriteMovie(new FavouriteMovie(movie));
                    Toast.makeText(getApplicationContext(), R.string.add_to_favourite, Toast.LENGTH_SHORT).show();
                } else {
                    mainViewModel.deleteFavouriteMovie(favouriteMovie);
                    Toast.makeText(getApplicationContext(), R.string.remove_from_favourite, Toast.LENGTH_SHORT).show();
                }
                setFavourite();
            }
        });

        binding.scrollViewInfo.smoothScrollTo(0,0);
    }

    private void setMovie() {
        Picasso.get().load(movie.getBigPosterPath()).into(binding.imageViewBigPoster);
        binding.movieInfo.textViewTitle.setText(movie.getTitle());
        binding.movieInfo.textViewOriginalTitle.setText(movie.getOriginalTitle());
        binding.movieInfo.textViewRating.setText(Double.toString(movie.getVoteAverage()));
        binding.movieInfo.textViewReleaseDate.setText(movie.getReleaseDate());
        binding.movieInfo.textViewOverview.setText(movie.getOverview());
    }

    private void setFavourite() {
        favouriteMovie = mainViewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null) {
            binding.imageViewAddToFavourite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        } else {
            binding.imageViewAddToFavourite.setImageResource(R.drawable.ic_baseline_favorite_24);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.itemMain: {
                startActivity(new Intent(this, MainActivity.class));
                break;
            }
            case R.id.itemFavourite: {
                startActivity(new Intent(this, FavouriteActivity.class));
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}