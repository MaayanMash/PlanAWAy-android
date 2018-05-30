package com.example.maayanmash.planaway;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import net.steamcrafted.loadtoast.LoadToast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TaskRequestDirections extends AsyncTask<String, Void, String> {

    private GoogleMap mMap;
    private LoadToast lt;

    public TaskRequestDirections(GoogleMap mMap,LoadToast lt){
        this.mMap=mMap;
        this.lt=lt;
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d("server","String doInBackground "+strings[0]);
        String responseString = "";
        try {
            responseString = requestDirection(strings[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("server","onPostExecute "+s);
        taskParser taskParser= new taskParser(mMap,lt);
        taskParser.execute(s);
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();

            bufferedReader.close();
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }
}