package com.ezeagu.android.headlines;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static com.ezeagu.android.headlines.NewsLoader.newsUrl;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    private String TAG = MainActivity.class.getSimpleName();
    private ListView list_view;
    private TextView emptyStateTextview;
    String section = "";
    String date = "";
    String title = "";
    LoaderManager mLoaderManager;

    public static ArrayList<HashMap<String, String>> newsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            mLoaderManager = getSupportLoaderManager();
            mLoaderManager.initLoader(1, null, this);

        } else {

            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            emptyStateTextview = (TextView) findViewById(R.id.emptystate);
            emptyStateTextview.setText("NO INTERNET CONNECTION");
        }

        newsList = new ArrayList<>();
        list_view = (ListView) findViewById(R.id.list);

        emptyStateTextview = (TextView) findViewById(R.id.emptystate);
        list_view.setEmptyView(emptyStateTextview);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String links = newsUrl.get(position);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(links));
                        startActivity(intent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Refreshing Data",Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new NewsLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        ListAdapter adapter = new SimpleAdapter(MainActivity.this, newsList,
                R.layout.list_item, new String[]{"title", "section", "date"},
                new int[]{R.id.titleText, R.id.sectionText, R.id.dateText});
        list_view.setAdapter(adapter);

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}



