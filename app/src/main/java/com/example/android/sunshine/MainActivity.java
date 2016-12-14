package com.example.android.sunshine;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private TextView tvURL;
    private TextView tvSearch;
    public class GithubQueryTask extends AsyncTask<URL, Void, String> {
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
            if(s!=null && !s.equals("")){
                tvSearch.setText(s);
            }
            super.onPostExecute(s);
        }
    }

    public void makeGitHubSearchQuery(){
        String githubQuery = editText.getText().toString();
        URL githubSearchURL = NetworkUtils.buildURL(githubQuery);
        tvURL.setText(githubSearchURL.toString());
        String gitHubSearchResult = null;
        new GithubQueryTask().execute(githubSearchURL);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.edit_query);
        tvURL = (TextView) findViewById(R.id.text_view);
        tvSearch = (TextView) findViewById(R.id.next_tv);
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
