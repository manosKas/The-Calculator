package com.manolee.thecalculator;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CurrencyConversionAPI extends AsyncTask<String, Void, String> {
        private asyncTaskListener callback;

        public CurrencyConversionAPI(asyncTaskListener l) {
            this.callback = l;
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected String doInBackground(String... urls) {
            String results = null;
            URL url;
            Log.d("derp","doInBackground");
            try {
                for (String url1 : urls) {
                    url = new URL(url1);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    Log.d("derp","url: "+url);
                    try {
                        Log.d("derp","api response code: "+urlConnection.getResponseCode());
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        results = result.toString();
                    } finally {
                        urlConnection.disconnect();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) { }
            return results;
        }

        @Override
        protected void onPostExecute(String result) {
            callback.onTaskComplete(result);
        }
}
