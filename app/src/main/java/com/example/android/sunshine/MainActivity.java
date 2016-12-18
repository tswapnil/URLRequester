package com.example.android.sunshine;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.*;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private EditText editText;
    private TextView tvURL;
    private TextView tvSearch;
    private TextView tvError;
    private ProgressBar progressBar;
    private static final int GITHUB_SEARCH_ID = 22;
    private static final String SEARCH_QUERY_URL_EXTRA = "query";


    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
      outState.putString(SEARCH_QUERY_URL_EXTRA, tvURL.getText().toString());
     //  outState.putString("Result", tvSearch.getText().toString());

    }


    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {

        return new android.support.v4.content.AsyncTaskLoader<String>(this) {
            private String mGitHubJson;
            @Override
            protected void onStartLoading(){
                super.onStartLoading();
                if(args==null){
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                if (mGitHubJson!=null){
                    deliverResult(mGitHubJson);
                }
                else
                forceLoad();

            }
            @Override
            public String loadInBackground() {
                String searchQueryURL = args.getString(SEARCH_QUERY_URL_EXTRA);
                if(searchQueryURL == null || TextUtils.isEmpty(searchQueryURL)){
                    return null;
                }
                String searchResults = null;
                try{
                    URL searchURL = new URL(searchQueryURL);

                    searchResults = NetworkUtils.getResponsefromHTTPURL(searchURL);
                    return searchResults;

                }
                catch (IOException e){
                    e.printStackTrace();
                    return null;
                }




           }

            @Override
            public void deliverResult(String data) {
                mGitHubJson = data;
                super.deliverResult(data);

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        progressBar.setVisibility(View.INVISIBLE);

        if(data!=null && !data.equals("")){
            tvSearch.setText(data);
            showJSONData();
        }
        else{
            showErrorMessage();
        }


    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }


    public class GithubQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(URL... urls) {
            URL searchURL = urls[0];
            String searchResults = null;
            try{
                searchResults = NetworkUtils.getResponsefromHTTPURL(searchURL);
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);

            if(s!=null && !s.equals("")){
                tvSearch.setText(s);
            }
            else{
                showErrorMessage();
            }
            super.onPostExecute(s);
        }
    }

    public void makeGitHubSearchQuery(){
        String githubQuery = editText.getText().toString();

        if(TextUtils.isEmpty(githubQuery)){
            tvURL.setText("No Query Entered");
            return;
        }
        URL githubSearchURL = NetworkUtils.buildURL(githubQuery);
        tvURL.setText(githubSearchURL.toString());
        String gitHubSearchResult = null;
       // new GithubQueryTask().execute(githubSearchURL);
        Bundle bundle = new Bundle();
        bundle.putString(SEARCH_QUERY_URL_EXTRA, githubSearchURL.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> gitHubLoader = loaderManager.getLoader(GITHUB_SEARCH_ID);
        if (gitHubLoader == null){
            loaderManager.initLoader(GITHUB_SEARCH_ID,bundle,this);
        }
        else{
            loaderManager.restartLoader(GITHUB_SEARCH_ID, bundle, this);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.edit_query);
        tvURL = (TextView) findViewById(R.id.text_view);
        tvSearch = (TextView) findViewById(R.id.next_tv);
        tvError = (TextView) findViewById(R.id.tv_error);
        progressBar = (ProgressBar) findViewById(R.id.loading_indicator);
        if(savedInstanceState!=null){
        //    String URL = savedInstanceState.getString("URL");
        //    String result = savedInstanceState.getString("Result");
        //    tvSearch.setText(result);
        //    tvURL.setText(URL);
        }
        getSupportLoaderManager().initLoader(GITHUB_SEARCH_ID,null, this);
    }
    private void showJSONData(){
        tvError.setVisibility(View.INVISIBLE);
        tvSearch.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage(){
        tvError.setVisibility(View.VISIBLE);
        tvSearch.setVisibility(View.INVISIBLE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
      getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int itemThatWasClicked = menuItem.getItemId();
        if(itemThatWasClicked == R.id.action_Search){
            Context context = MainActivity.this;
            String textToShow = "Search Clicked";
            Toast.makeText(context, textToShow, Toast.LENGTH_LONG).show();
            makeGitHubSearchQuery();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
