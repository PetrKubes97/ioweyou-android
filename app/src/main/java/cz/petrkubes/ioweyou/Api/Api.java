package cz.petrkubes.ioweyou.Api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import cz.petrkubes.ioweyou.Const;
import cz.petrkubes.ioweyou.Database.DatabaseHandler;
import cz.petrkubes.ioweyou.Pojos.ApiParams;
import cz.petrkubes.ioweyou.Pojos.ApiResult;
import cz.petrkubes.ioweyou.Pojos.User;

/**
 * Created by petr on 3.1.17.
 */

public class Api {

    public static final int API_GET_USER = 1;
    public static final int API_LOGIN = 2;

    private static final String BASE_URL = "http://192.168.2.155/payuback-api/www/api/";
    private DatabaseHandler db;
    private Context context;
    private ApiFailureHandler apiFailureHandler;

    public Api(Context context) {
        this.db = new DatabaseHandler(context);
        this.context = context;
        this.apiFailureHandler = new ApiFailureHandler(context, this.db);
    }

    public void download(int type, ApiParams params) {

        switch (type) {
            case API_GET_USER:
                new getUser().execute(params);
                break;
            case API_LOGIN:
                new login().execute(params);
                break;
        }

    }


    private class login extends AsyncTask<ApiParams, Void, ApiResult> {
        @Override
        protected ApiResult doInBackground(ApiParams... params) {
            Log.d(Const.TAG, "LOGIN BEGINS");
            SimpleCallback callback = params[0].callback;
            ApiResult result = connect(BASE_URL + "user/login", "POST", params[0]);

            if (result.successfull) {
                try {
                    Log.d(Const.TAG, "LOGIN");
                    // 1. case: user logged in for the first time so a new row is created with his id
                    // 2. case: user logged in and already has a row in the database so we just update the api key
                    db.addOrUpdateUser(new User(
                            result.json.getInt("id"),
                            result.json.getString("apiKey"),
                            null,
                            null,
                            null
                    ));

                } catch (JSONException e) {
                    result.successfull = false;
                    result.message = e.getMessage();
                    return result;
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(ApiResult apiResult) {
            if (apiResult.successfull) {
                apiResult.callback.onSuccess();
            } else {
                apiFailureHandler.HandleFailure(apiResult.code, apiResult.message, apiResult.callback);
            }
        }
    }

    private class getUser extends AsyncTask<ApiParams, Void, Void> {
        @Override
        protected Void doInBackground(ApiParams... params) {

            params[0].callback.onSuccess();

            return null;
        }
    }

    private ApiResult connect(String address, String method, ApiParams params) {

        Log.d(Const.TAG, "Connecting...");

        URL url;
        HttpURLConnection urlConnection = null;
        JSONObject json = null;

        int code = 1000;
        String message = null;
        boolean successfull = true;

        try {
            // Create URL connection
            url = new URL(address);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestMethod(method);
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            // Send JSON data
            if (params.jsonToSend != null) {
                Log.d(Const.TAG, "jsonToSEnd is set:" + params.jsonToSend.toString());
                DataOutputStream printout = new DataOutputStream(urlConnection.getOutputStream());
                printout.writeBytes(params.jsonToSend.toString());
                printout.flush ();
                printout.close ();
            }
            // Get response
            code = urlConnection.getResponseCode();
            InputStream in;
            if (code < 300) {
                in = urlConnection.getInputStream();
            } else {
                in = urlConnection.getErrorStream();
            }
            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            String result = "";
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                result += current;
            }

            Log.d(Const.TAG, "Result: "+result);
            // Convert response to JSON
            json = new JSONObject(result);

            // Get error message
            if (code >= 300) {
                successfull = false;
                message = json.getString("message");
            }

        } catch (Exception e) {
            successfull = false;
            message = e.getMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        ApiResult result = new ApiResult();
        result.code = code;
        result.message = message;
        result.json = json;
        result.callback = params.callback;
        result.successfull = successfull;

        return result;
    }


}
