package com.example.travka.test;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DisplayMoviesActivity extends Activity {
    List<String> li;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_movies);


        Intent intent = getIntent();
        String result = intent.getStringExtra("movies");

        //parsing json string
        try{
            JSONObject jsonObject = new JSONObject(result);
            JSONArray movies = jsonObject.getJSONArray("data");
//            Log.d("movies", movies.toString());
            li = new ArrayList<String>();
            for(int i = 0; i < movies.length(); i++){
                JSONObject object = movies.getJSONObject(i);

                li.add("Movie: \""+object.getString("movie") + "\"" + "\nComment: " + object.getString("comment"));
            }
        } catch (JSONException e){
            Log.d("error", e.getLocalizedMessage());
        }

        //display movies into list view
        final ListView list=(ListView) findViewById(R.id.list);

        ArrayAdapter<String> adp=new ArrayAdapter<String>(getBaseContext(), R.layout.movie_item, li);
        list.setAdapter(adp);



    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            MainActivity.allow_click_get_list = true;
            Log.d("lol", "back from second activty");
//            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
