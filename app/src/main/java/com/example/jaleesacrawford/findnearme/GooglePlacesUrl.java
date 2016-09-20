package com.example.jaleesacrawford.findnearme;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class GooglePlacesUrl {

    public String getUrl(String strUrl) throws IOException {
        String locationDetails = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer stringBuffer = new StringBuffer();

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            locationDetails = stringBuffer.toString();
            Log.d("google places url ", locationDetails.toString());
            bufferedReader.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            inputStream.close();
            urlConnection.disconnect();
        }
        return locationDetails;
    }
}

