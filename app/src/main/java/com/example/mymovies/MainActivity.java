package com.example.mymovies;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.adapters.MovieAdapter;
import com.example.mymovies.data.MainViewModel;
import com.example.mymovies.data.Movie;
import com.example.mymovies.databinding.ActivityMainBinding;
import com.example.mymovies.utils.JSONUtils;
import com.example.mymovies.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {

    private ActivityMainBinding binding;

    private MovieAdapter movieAdapter;

    private MainViewModel viewModel;

    private static final int LOADER_ID = 133;
    private LoaderManager loaderManager;

    private static int page = 1;
    private static int methodOfSort;
    private static boolean isLoading = false;

    private static String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        lang = Locale.getDefault().getLanguage();

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        loaderManager = LoaderManager.getInstance(this);

        binding.recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, getColumnCount()));
        movieAdapter = new MovieAdapter();
        binding.recyclerViewPosters.setAdapter(movieAdapter);

        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = movieAdapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                if (!isLoading) {
                    downloadData(methodOfSort, page);
                }
            }
        });

        binding.switchSort.setChecked(true);
        binding.switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                page = 1;
                setMethodOfSort(isChecked);
            }
        });
        binding.switchSort.setChecked(false);

        binding.textViewPopularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMethodOfSort(false);
                binding.switchSort.setChecked(false);
            }
        });
        binding.textViewTopRated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMethodOfSort(true);
                binding.switchSort.setChecked(true);
            }
        });

        LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                if (page == 1) {
                    movieAdapter.setMovies(movies);
                }
            }
        });
    }

    private int getColumnCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return Math.max(width / 185, 2);
    }

    private void setMethodOfSort(boolean isTopRated) {
        if (isTopRated) {
            methodOfSort = NetworkUtils.TOP_RATED;
            binding.textViewTopRated.setTextColor(getResources().getColor(android.R.color.holo_red_light, getTheme()));
            binding.textViewPopularity.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        } else {
            methodOfSort = NetworkUtils.POPULARITY;
            binding.textViewTopRated.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
            binding.textViewPopularity.setTextColor(getResources().getColor(android.R.color.holo_red_light, getTheme()));
        }
        downloadData(methodOfSort, page);
    }

    private void downloadData(int methodOfSort, int page) {
        URL url = NetworkUtils.buildURL(methodOfSort, page, lang);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        loaderManager.restartLoader(LOADER_ID, bundle, this);
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

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, args);
        jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.OnStartLoadingListener() {
            @Override
            public void OnStartLoading() {
                binding.progressBarLoading.setVisibility(View.VISIBLE);
                isLoading = true;
            }
        });
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject data) {
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(data);
        if (movies != null && !movies.isEmpty()) {

            if (page == 1) {
                viewModel.deleteAllMovies();
                movieAdapter.clear();
            }

            for (Movie movie : movies) {
                viewModel.insertMovie(movie);
            }
            movieAdapter.addMovies(movies);
            page++;
        }
        binding.progressBarLoading.setVisibility(View.GONE);
        isLoading = false;
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}