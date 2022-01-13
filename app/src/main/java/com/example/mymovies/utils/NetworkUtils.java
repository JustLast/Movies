package com.example.mymovies.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class NetworkUtils {
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String BASE_URL_VIDEOS = "https://api.themoviedb.org/3/movie/%s/videos";
    private static final String BASE_URL_REVIEWS = "https://api.themoviedb.org/3/movie/%s/reviews";

    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_LANGUAGE = "language";
    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_PAGE = "page";
    private static final String PARAMS_MIN_VOTE_COUNT = "vote_count.gte";

    private static final String API_KEY = "";
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";
    private static final String MIN_VOTE_COUNT_VALUE = "1000";

    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;

    public static URL buildURL(int sortBy, int page, String lang) {
        URL url = null;
        String methodOfSort = "";

        if (sortBy == POPULARITY) {
            methodOfSort = SORT_BY_POPULARITY;
        } else {
            methodOfSort = SORT_BY_TOP_RATED;
        }

        Uri uri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, lang)
                .appendQueryParameter(PARAMS_SORT_BY, methodOfSort)
                .appendQueryParameter(PARAMS_PAGE, Integer.toString(page))
                .appendQueryParameter(PARAMS_MIN_VOTE_COUNT, MIN_VOTE_COUNT_VALUE)
                .build();

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildURLtoVideos(int id, String lang) {
        URL url = null;
        Uri uri = Uri.parse(String.format(BASE_URL_VIDEOS, id))
                .buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, lang)
                .build();
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildURLtoReviews(int id, String lang) {
        URL url = null;
        Uri uri = Uri.parse(String.format(BASE_URL_REVIEWS, id))
                .buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, lang)
                .build();
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static JSONObject getJSONFromNetwork(int sortBy, int page, String lang) {
        JSONObject jsonObject = null;

        URL url = buildURL(sortBy, page, lang);
        try {
            jsonObject = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject getJSONForVideos(int id, String lang) {
        JSONObject jsonObject = null;

        URL url = buildURLtoVideos(id, lang);
        try {
            jsonObject = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject getJSONForReviews(int id, String lang) {
        JSONObject jsonObject = null;

        URL url = buildURLtoReviews(id, lang);
        try {
            jsonObject = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static class JSONLoader extends AsyncTaskLoader<JSONObject> {

        private Bundle bundle;
        private OnStartLoadingListener onStartLoadingListener;

        public interface OnStartLoadingListener {
            void OnStartLoading();
        }

        public void setOnStartLoadingListener(OnStartLoadingListener onStartLoadingListener) {
            this.onStartLoadingListener = onStartLoadingListener;
        }

        public JSONLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (onStartLoadingListener != null) {
                onStartLoadingListener.OnStartLoading();
            }
            forceLoad();
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {
            if (bundle == null) {
                return null;
            }

            String urlAsString = bundle.getString("url");
            URL url = null;
            try {
                url = new URL(urlAsString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = null;
            if (url == null) {
                return jsonObject;
            }

            HttpURLConnection httpURLConnection = null;

            try {
                StringBuilder stringBuilder = new StringBuilder();

                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();

                while (line != null) {
                    stringBuilder.append(line);
                    line = bufferedReader.readLine();
                }

                jsonObject = new JSONObject(stringBuilder.toString());

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return jsonObject;
        }
    }

    private static class JSONLoadTask extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject jsonObject = null;

            if (urls == null || urls.length == 0) {
                return jsonObject;
            }

            HttpURLConnection httpURLConnection = null;

            try {
                StringBuilder stringBuilder = new StringBuilder();

                httpURLConnection = (HttpURLConnection) urls[0].openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();

                while (line != null) {
                    stringBuilder.append(line);
                    line = bufferedReader.readLine();
                }

                jsonObject = new JSONObject(stringBuilder.toString());

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return jsonObject;
        }
    }
}
