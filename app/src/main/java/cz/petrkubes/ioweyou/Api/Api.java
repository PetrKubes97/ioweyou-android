package cz.petrkubes.ioweyou.Api;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.petrkubes.ioweyou.Const;

/**
 * Created by petr on 3.1.17.
 */

public class Api {

    private static final String BASE_URL = "http://34.194.114.99/";

    public void download(SimpleCallback callback) {
        new Downloader().execute(callback);
    }


    private class Downloader extends AsyncTask<SimpleCallback, Void, Void> {
        @Override
        protected Void doInBackground(SimpleCallback... params) {

            Log.d(Const.TAG, "TEst asdf ");

            JSONObject json = connect("payuback-api/www/api/user");

            params[0].onSuccess();

            return null;
        }
    }

    private JSONObject connect(String address) {

        URL url;
        HttpURLConnection urlConnection = null;
        JSONObject json = null;
        int code = 0;

        try {
            url = new URL("http://34.194.114.99/" + address);

            urlConnection = (HttpURLConnection) url.openConnection();

            code = urlConnection.getResponseCode();
            
            InputStream in = urlConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            String result = "";
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                result += current;
            }

            try {
                json = new JSONObject(result);

                System.out.print(json.toString());

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return json;
    }


}
