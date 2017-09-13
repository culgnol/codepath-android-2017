package com.codepath.nytarticlesearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

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
    }

    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();

        //Toast.makeText(this, "Searching for" + query, Toast.LENGTH_SHORT);

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        String apikey = "6c549ccbe8a24343a19937b21aeaf2fb";

        RequestParams params = new RequestParams();
        params.put("api-key", apikey);
        params.put("page", 0);

        String newsDeskItems = "";

        if (settingsBundle != null) {
            // sort order
            params.put("sort", settingsBundle.getStringExtra("string_sortOrder"));

            // news desk values
            newsDeskItems = (settingsBundle.getBooleanExtra("bool_cbArts", false)) ? "\"Arts\"" : "";
            newsDeskItems += (settingsBundle.getBooleanExtra("bool_cbFashionStyle", false)) ? " \"Fashion & Style\"" : "";
            newsDeskItems += (settingsBundle.getBooleanExtra("bool_cbSports", false)) ? " \"Sports\"" : "";

            if (!TextUtils.isEmpty(newsDeskItems)) {
                query += "body:(\"" + query + "\")";
                query += "%20AND%20news_desk:(" + newsDeskItems + ")";
            }

            params.put("fq", query);

            // begin date
            String strBeginDate = settingsBundle.getStringExtra("string_beginDate");
            if (!TextUtils.isEmpty(strBeginDate)) {

                params.put("begin_date", strBeginDate);
            }
        } else {
            // general search
            params.put("q", query);
        }

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;
                articles.clear();

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
