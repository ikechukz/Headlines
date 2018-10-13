package com.ezeagu.android.headlines;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class NewsLoader extends AsyncTaskLoader<String> {
    private final String REQUEST_URL = "https://content.guardianapis.com/search";
    public static ArrayList<String> newsUrl = new ArrayList<String>();

    public NewsLoader(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public String loadInBackground() {
        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("api-key", "0db00912-4070-4eae-9226-46b6ccdc053b");

        HttpHandler sh = new HttpHandler();

        String url = uriBuilder.toString();
        String jsonString = "";
        try {
            jsonString = sh.makeHttpRequest(createUrl(url));
        } catch (IOException e) {
            return null;
        }

        if (jsonString != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonString);
                JSONObject jsonObjectResponse = jsonObj.getJSONObject("response");

                JSONArray news = jsonObjectResponse.getJSONArray("results");

                // looping through all contents
                for (int i = 0; i < news.length(); i++) {

                    JSONObject c = news.getJSONObject(i);
                    String title = c.getString("webTitle");
                    String section = c.getString("sectionName");
                    String dateRaw = c.getString("webPublicationDate");
                    String webUrl = c.getString("webUrl");

                    newsUrl.add(webUrl);

                    //To properly format the date
                    String currentString = dateRaw;
                    String[] separated = currentString.split("T");
                    String date = separated[0];

                    // tmp hash map for a single news
                    HashMap<String, String> newsData = new HashMap<>();

                    // add each child node to HashMap key => value
                    newsData.put("title", title);
                    newsData.put("section", section);
                    newsData.put("date", date);

                    // adding a news data to our news list
                    MainActivity.newsList.add(newsData);
                }
            } catch (final JSONException e) {
            }
        }
        return null;
    }

    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            return null;
        }
        return url;
    }
}
