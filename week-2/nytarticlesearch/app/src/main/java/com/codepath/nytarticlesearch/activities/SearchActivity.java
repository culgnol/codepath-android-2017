package com.codepath.nytarticlesearch.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.codepath.nytarticlesearch.adapters.EndlessScrollListener;
import com.codepath.nytarticlesearch.models.Article;
import com.codepath.nytarticlesearch.adapters.ArticleArrayAdapter;
import com.codepath.nytarticlesearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.codepath.nytarticlesearch.R.id.cbArts;
import static com.codepath.nytarticlesearch.R.id.cbSports;

public class SearchActivity extends AppCompatActivity {

    EditText etQuery;
    GridView gvResults;
    Button btSearch;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    static final int REQUEST_CODE_SETTINGS = 100;

    Intent settingsBundle;
    RequestParams requestParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupViews();

    }

    public void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btSearch = (Button) findViewById(R.id.btSearch);

        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        // hook up listener for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // create an intent to display the article
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class); //since were inside annyomus class easy way use getApplicationContext()

                // get the article to display
                Article article = articles.get(position);

                // pass in that article into intent
                i.putExtra("article", article);

                // launch the activity
                startActivity(i);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyDataSetChanged()`

        if (requestParams.has("page")) {
            requestParams.remove("page");
        }

        requestParams.put("page", offset);
        Log.d("DEBUG", "Page request: " + offset);

        makeArticleApiRequest(requestParams);
    }

    public void onArticleSearch(View view) {
        // new search
        requestParams = new RequestParams();
        articles.clear();
        adapter.notifyDataSetChanged();

        // default page
        requestParams.put("page", 0);

        // get query string
        String query = etQuery.getText().toString();

        // Restore filter settings if found
        String newsDeskItems = "";

        if (settingsBundle != null) {
            // sort order
            requestParams.put("sort", settingsBundle.getStringExtra("string_sortOrder"));

            // news desk values
            newsDeskItems = (settingsBundle.getBooleanExtra("bool_cbArts", false)) ? "\"Arts\"" : "";
            newsDeskItems += (settingsBundle.getBooleanExtra("bool_cbFashionStyle", false)) ? " \"Fashion & Style\"" : "";
            newsDeskItems += (settingsBundle.getBooleanExtra("bool_cbSports", false)) ? " \"Sports\"" : "";

            if (!TextUtils.isEmpty(newsDeskItems)) {
                query += "body:(\"" + query + "\")";
                query += "%20AND%20news_desk:(" + newsDeskItems + ")";
            }

            requestParams.put("fq", query);

            // begin date
            String strBeginDate = settingsBundle.getStringExtra("string_beginDate");
            if (!TextUtils.isEmpty(strBeginDate)) {

                requestParams.put("begin_date", strBeginDate);
            }
        } else {
            // general search
            requestParams.put("q", query);
        }

        makeArticleApiRequest(requestParams);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void makeArticleApiRequest(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        String apikey = "6c549ccbe8a24343a19937b21aeaf2fb";

        if (params.has("api-key")) {
            params.remove("api-key");
        }

        params.put("api-key", apikey);

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    // Log.d("DEBUG", articleJsonResults.toString());
                    articles.addAll(Article.fromJsonArray(articleJsonResults));
                    Log.d("DEBUG", articles.toString());
                    adapter.notifyDataSetChanged(); // not needed if using "adapter.addAll" above
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                launchSettingsActivity();
                break;
        }

        return  true;
    }

    public void launchSettingsActivity() {
        Intent i = new Intent(this, SettingsActivity.class);
        // restore settings if exists
        if (settingsBundle != null) {
            i.putExtra("string_beginDate", settingsBundle.getStringExtra("string_beginDate"));
            i.putExtra("string_sortOrder", settingsBundle.getStringExtra("string_sortOrder"));
            i.putExtra("bool_cbArts", settingsBundle.getBooleanExtra("bool_cbArts", false));
            i.putExtra("bool_cbSports", settingsBundle.getBooleanExtra("bool_cbSports", false));
            i.putExtra("bool_cbFashionStyle", settingsBundle.getBooleanExtra("bool_cbFashionStyle", false));
        }
        startActivityForResult(i, REQUEST_CODE_SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SETTINGS) {
            if (resultCode == RESULT_OK) {
                // Do something with result
                settingsBundle = data;
                btSearch.performClick();
            } else {
                settingsBundle = null;
            }
        }
    }
}
