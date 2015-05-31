package com.example.travka.test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONObject;


public class MainActivity extends Activity {

    //properties
    final private String url = "http://recommendfilm-omgwtf.rhcloud.com/";
    private TextView alert;
    private EditText movie_input_ref;
    private EditText comment_input_ref;
    private Runnable alert_hide;
    static boolean allow_click_recommend;
    static boolean allow_click_get_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alert = (TextView)findViewById(R.id.alert);
        alert_hide = new Runnable() {
            public void run() {
                alert.setText("");
            }
        };

        final EditText movie_input = (EditText)findViewById(R.id.movie);
        movie_input_ref = movie_input;
        final EditText comment_input = (EditText)findViewById(R.id.comment);
        comment_input_ref = comment_input;


        final Button recommend_button = (Button)findViewById(R.id.recommend);
        final Button view_button = (Button)findViewById(R.id.view_list);

        allow_click_recommend = true;
        allow_click_get_list = true;


        recommend_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(allow_click_recommend){
                    allow_click_recommend = false;
                    //waiting
                    alert.setTextColor(Color.YELLOW);
                    alert.setText("Please wait...");

                    String movie = movie_input.getText().toString();
                    String comment = comment_input.getText().toString();

                    if(movie.equals("") || comment.equals("")) {
                        String empty_message = "Empty fields: ";
                        if(movie.equals("")) empty_message += "movie";
                        if(movie.equals("") && comment.equals("")) empty_message += ", ";
                        if(comment.equals("")) empty_message += "comment";
                        alert.setTextColor(Color.RED);
                        alert.setText(empty_message);
                        alert.removeCallbacks(alert_hide);
                        alert.postDelayed(alert_hide, 3000);
                        allow_click_recommend = true;
                    } else {
                        new Post_recommendation().execute(movie, comment);
                    }
                }

            }
        });
        view_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(allow_click_get_list){
                    allow_click_get_list = false;

                    //waiting
                    alert.setTextColor(Color.YELLOW);
                    alert.setText("Please wait...");

                    new Get_recommended_movies().execute();
                }


            }
        });
    }

    class Post_recommendation extends AsyncTask<String, Integer, String>
    {

        @Override
        protected String doInBackground(String... params) {

            String result = "";
            try {

                HttpURLConnection urlConnection = (HttpURLConnection)new URL(url+"recommendedmovies").openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                //data to post
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("movie", params[0]);
                jsonParam.put("comment", params[1]);

                // Send POST output.
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                out.write(jsonParam.toString());
                out.close();


                int code = urlConnection.getResponseCode();
                if (code ==  200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    String response = "";
                    while ((line = in.readLine()) != null){
                        response += line;
                    };
                    in.close();
                    result = response;
                    urlConnection.disconnect();
                }else{

                    result = "fail";
                    Log.d("code", Integer.toString(code));
                }
            } catch (Exception e) {
                Log.d("error", e.getLocalizedMessage());
                result = "fail";
            }
            Log.d("result", result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            if(result.equals("fail")) {
                alert.setTextColor(Color.RED);
                alert.setText("Failed post data");
            } else {
                alert.setTextColor(Color.GREEN);
                alert.setText("Succesfull post data");
                movie_input_ref.setText("");
                comment_input_ref.setText("");
            }

            alert.removeCallbacks(alert_hide);
            alert.postDelayed(alert_hide, 3000);
            allow_click_recommend = true;

        }

    }

    class Get_recommended_movies extends AsyncTask<String, Integer, String>
    {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                HttpURLConnection urlConnection = (HttpURLConnection)new URL(url+"recommendedmovies").openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("GET");


                int statusCode = urlConnection.getResponseCode();

                if (statusCode ==  200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    String response = "";
                    while ((line = in.readLine()) != null){
                        response += line;
                    };
                    in.close();
                    result = response;

                    urlConnection.disconnect();
                }else{
                    result = "fail";
                }
            } catch (Exception e) {
                Log.d("error", e.getLocalizedMessage());
                result = "fail";
            }
            Log.d("result", result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);



            if(result.equals("fail")) {
                alert.setTextColor(Color.RED);
                alert.setText("Failed to get list");
                alert.removeCallbacks(alert_hide);
                alert.postDelayed(alert_hide, 3000);
                allow_click_get_list = true;
            }
            else {
                Intent intent = new Intent(getBaseContext(), DisplayMoviesActivity.class);
                intent.putExtra("movies", result);
                startActivity(intent);
                alert.setText("");
            }



        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
