package com.example.mymovies.utils;


import com.example.mymovies.data.Movie;
import com.example.mymovies.data.Review;
import com.example.mymovies.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils {

    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    public static final String SMALL_POSTER_SIZE = "w185";
    public static final String BIG_POSTER_SIZE = "w780";

    private static final String KEY_RESULTS = "results";

    // Overviews keys
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";

    // Videos keys
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    private static final String KEY_OF_VIDEO = "key";
    private static final String KEY_NAME = "name";

    // Movies keys
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";

    public static ArrayList<Movie> getMoviesFromJSON(JSONObject jsonObject) {
        ArrayList<Movie> result = new ArrayList<>();

        if (jsonObject == null) {
            return result;
        }

        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectMovie = jsonArray.getJSONObject(i);

                int id = objectMovie.getInt(KEY_ID);
                int voteCount = objectMovie.getInt(KEY_VOTE_COUNT);
                String title = objectMovie.getString(KEY_TITLE);
                String originalTitle = objectMovie.getString(KEY_ORIGINAL_TITLE);
                String overview = objectMovie.getString(KEY_OVERVIEW);
                String posterPath = BASE_POSTER_URL + SMALL_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String bigPosterPath = BASE_POSTER_URL + BIG_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String backdropPath = objectMovie.getString(KEY_BACKDROP_PATH);
                double voteAverage = objectMovie.getDouble(KEY_VOTE_AVERAGE);
                String releaseDate = objectMovie.getString(KEY_RELEASE_DATE);

                result.add(new Movie(id, voteCount, title, originalTitle, overview, posterPath, bigPosterPath, backdropPath, voteAverage, releaseDate));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<Review> getReviewsFromJSON(JSONObject jsonObject) {
        ArrayList<Review> result = new ArrayList<>();

        if (jsonObject == null) {
            return result;
        }

        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectReview = jsonArray.getJSONObject(i);
                String author = jsonObjectReview.getString(KEY_AUTHOR);
                String content = jsonObjectReview.getString(KEY_CONTENT);
                result.add(new Review(author, content));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<Trailer> getTrailersFromJSON(JSONObject jsonObject) {
        ArrayList<Trailer> result = new ArrayList<>();

        if (jsonObject == null) {
            return result;
        }

        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectTrailer = jsonArray.getJSONObject(i);
                String keyOfVideo = BASE_YOUTUBE_URL + jsonObjectTrailer.getString(KEY_OF_VIDEO);
                String name = jsonObjectTrailer.getString(KEY_NAME);
                result.add(new Trailer(keyOfVideo, name));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}
