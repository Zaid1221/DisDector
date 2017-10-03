package com.example.zaid.disdetector;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private ProgressBar spinner;
    private String userInput;
    private EditText TermtoAnalyze;
    private TextView Result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        TermtoAnalyze = (EditText) findViewById(R.id.TermtoAnalyze);
        Result = (TextView) findViewById(R.id.result);
    }

    public void searchMethod(View v) {;
        Uri builtUri2 = Uri.parse("https://api.meaningcloud.com/sentiment-2.1").buildUpon()
                .appendQueryParameter("key", "5cefecfee765dde6c9c943db4c891c88")
                .appendQueryParameter("txt", TermtoAnalyze.getText().toString())
                .appendQueryParameter("lang", "en")
                .build();
        new FindSentimentTask().execute(builtUri2.toString());
        spinner.setVisibility(View.VISIBLE);
    }

    public class FindSentimentTask extends AsyncTask <String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
           //android.os.Debug.waitForDebugger();

            String toreturn = "Did not work";
            try{
                toreturn = getResponseFromHttpUrl(url[0]);
            }catch(Exception e){
                Log.d("ErrorInApp", "exception on get Response from HTTP call" + e.getMessage());
            }
            return toreturn;
        }

        protected void onPostExecute(String sentimentData) {
            int x=5;
            x=x+1;
            /*P+: strong positive
            P: positive
            NEU: neutral
            N: negative
            N+: strong negative
            NONE: without sentiment*/
            try {
                JSONObject sentimentJSON = new JSONObject(sentimentData);
                //int x=5;
                //((TextView)findViewById(R.id.textView2)).setText(sentimentJSON.toString());
                //JSONObject status= sentimentJSON.getJSONObject("model");
                String scoreTag=sentimentJSON.get("score_tag").toString();
                String confidenceTag=sentimentJSON.get("confidence").toString();
                String ironyTag=sentimentJSON.get("irony").toString();

                String score = "";

                if(scoreTag == "P+"){
                    score = ("Very Positive");
                }
                else if(scoreTag.toString() == "P"){
                    score = ("Positive");
                }
                else if(scoreTag == "NEU"){
                    score = ("Nuetral");
                }
                else if(scoreTag == "N"){
                    score = ("Negative");
                }
                else if(scoreTag == "N+"){
                    score = ("Very Negative");
                }
                else if(scoreTag == "NONE"){
                    score = ("No Dis Detecded");
                }


                Result.setText("Score Tag: " + scoreTag + "\n" + "Confidence: " + confidenceTag + "\n" + "Irony: " + ironyTag);
                spinner.setVisibility(View.GONE);
                //Result.setText(confidenceTag);

            }catch(Exception e){
                e.printStackTrace();
            }
            super.onPostExecute(sentimentData);
        }
    }

    public static String getResponseFromHttpUrl(String url) throws IOException {
        URL theURL = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) theURL.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
