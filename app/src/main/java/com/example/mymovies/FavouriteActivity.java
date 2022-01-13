package com.example.mymovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.mymovies.adapters.MovieAdapter;
import com.example.mymovies.data.FavouriteMovie;
import com.example.mymovies.data.MainViewModel;
import com.example.mymovies.data.Movie;
import com.example.mymovies.databinding.ActivityFavouriteBinding;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    private ActivityFavouriteBinding binding;
    private MovieAdapter movieAdapter;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavouriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        movieAdapter = new MovieAdapter();
        binding.recyclerViewFavouriteMovies.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerViewFavouriteMovies.setAdapter(movieAdapter);

        LiveData<List<FavouriteMovie>> favouriteMovies = mainViewModel.getFavouriteMovies();
        favouriteMovies.observe(this, new Observer<List<FavouriteMovie>>() {
            @Override
            public void onChanged(List<FavouriteMovie> favouriteMovies) {
                if (favouriteMovies != null) {
                    List<Movie> movies = new ArrayList<>();
                    movies.addAll(favouriteMovies);
                    movieAdapter.setMovies(movies);
                }
            }
        });

        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = movieAdapter.getMovies().get(position);
                Intent intent = new Intent(FavouriteActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
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