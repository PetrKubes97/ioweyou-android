package cz.petrkubes.ioweyou.Api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.petrkubes.ioweyou.Const;
import cz.petrkubes.ioweyou.Database.DatabaseHandler;
import cz.petrkubes.ioweyou.Pojos.Action;
import cz.petrkubes.ioweyou.Pojos.ApiParams;
import cz.petrkubes.ioweyou.Pojos.ApiResult;
import cz.petrkubes.ioweyou.Pojos.Currency;
import cz.petrkubes.ioweyou.Pojos.Debt;
import cz.petrkubes.ioweyou.Pojos.Friend;
import cz.petrkubes.ioweyou.Pojos.User;
import cz.petrkubes.ioweyou.R;

/**
 * Created by petr on 3.1.17.
 */

public class Api {

    public static final int API_GET_USER = 1;
    public static final int API_LOGIN = 2;
    public static final int API_GET_CURENCIES = 3;
    public static final int API_GET_ACTIONS = 4;
    public static final int API_UPDATE_DEBTS = 5;
    public static final int API_UPDATE_DEBTS_AND_ACTIONS = 6;
    public static final int API_UPDATE_ALL = 7;
    public static final int API_LOGIN_AND_UPDATE_ALL = 8;

    private static final String BASE_URL = "http://34.194.114.99/payuback-api/www/api/";
    private DatabaseHandler db;
    private Context context;
    private ApiFailureHandler apiFailureHandler;

    public Api(Context context) {
        this.db = new DatabaseHandler(context);
        this.context = context;
        this.apiFailureHandler = new ApiFailureHandler(context, this.db);
    }

    public void download(int type, ApiParams params) {

        // Don't try to getResult when the user is clearly not online
        if (!isConnected()) {
            apiFailureHandler.HandleFailure(600, context.getString(R.string.no_internet), params.callback);
            return;
        }

        switch (type) {
            case API_GET_USER:
                new GetUser().execute(params);
                break;
            case API_LOGIN:
                new Login().execute(params);
                break;
            case API_GET_CURENCIES:
                new GetCurrencies().execute(params);
                break;
            case API_GET_ACTIONS:
                new GetActions().execute(params);
                break;
            case API_UPDATE_DEBTS:
                new UpdateDebts().execute(params);
                break;
            case API_UPDATE_DEBTS_AND_ACTIONS:
                updateDebtsAndActions(params);
                break;
            case API_UPDATE_ALL:
                updateAll(params);
                break;
            case API_LOGIN_AND_UPDATE_ALL:
                loginAndUpdateAll(params);
                break;
        }

    }

    // --------------------------------------- Api calls ------------------------------------------ //

    /**
     * Makes an API call to the server
     * Saves user's is and apiKey to the database
     */
    private class Login extends AsyncTask<ApiParams, Void, ApiResult> {
        @Override
        protected ApiResult doInBackground(ApiParams... params) {
            Log.d(Const.TAG, "Api: login");
            ApiResult result = getResult(BASE_URL + "user/login", "POST", params[0], null);

            if (result.successfull) {
                try {
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
                apiResult.callback.onSuccess(API_LOGIN);
            } else {
                apiFailureHandler.HandleFailure(apiResult.code, apiResult.message, apiResult.callback);
            }
        }
    }

    /**
     * Downloads and saves info about the user
     */
    private class GetUser extends AsyncTask<ApiParams, Void, ApiResult> {
        @Override
        protected ApiResult doInBackground(ApiParams... params) {
            Log.d(Const.TAG, "API: getUser");

            User currentUser = db.getUser();
            ApiResult result = getResult(BASE_URL + "user/", "GET", params[0], currentUser.apiKey);

            if (result.successfull) {
                try {
                    // Go through every friend and add him to database
                    JSONArray friendsJson = result.json.getJSONArray("friends");

                    for (int i = 0; i < friendsJson.length(); i++) {
                        JSONObject friendJson = friendsJson.getJSONObject(i);
                        Friend friend = new Friend(
                                friendJson.getInt("id"),
                                friendJson.getString("name"),
                                friendJson.getString("email")
                        );

                        db.addFriend(friend);

                    }

                    // It is necessary to convert date string to Date class
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date registeredAt = null;

                    registeredAt = df.parse(result.json.getString("registeredAt"));

                    User user = new User(
                            result.json.getInt("id"),
                            null,
                            result.json.getString("email"),
                            result.json.getString("name"),
                            registeredAt);

                    db.addOrUpdateUser(user);

                } catch (Exception e) {
                    result.successfull = false;
                    result.message = e.getMessage();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(ApiResult apiResult) {
            if (apiResult.successfull) {
                apiResult.callback.onSuccess(API_GET_USER);
            } else {
                apiFailureHandler.HandleFailure(apiResult.code, apiResult.message, apiResult.callback);
            }
        }
    }

    /**
     * Downloads and saves currencies
     */
    private class GetCurrencies extends AsyncTask<ApiParams, Void, ApiResult> {
        @Override
        protected ApiResult doInBackground(ApiParams... params) {
            Log.d(Const.TAG, "API: getCurrencies");

            User currentUser = db.getUser();
            ApiResult result = getResult(BASE_URL + "currencies/", "GET", params[0], currentUser.apiKey);

            if (result.successfull) {
                try {
                    // Go through every currency and add it to the database
                    JSONArray currenciesJson = result.json.getJSONArray("currencies");

                    for (int i=0;i<currenciesJson.length();i++) {

                        JSONObject currencyJson = currenciesJson.getJSONObject(i);

                        Currency currency = new Currency(
                                currencyJson.getInt("id"),
                                currencyJson.getString("symbol"));

                        db.addCurrency(currency);
                    }

                } catch (Exception e) {
                    result.successfull = false;
                    result.message = e.getMessage();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(ApiResult apiResult) {
            if (apiResult.successfull) {
                apiResult.callback.onSuccess(API_GET_CURENCIES);
            } else {
                apiFailureHandler.HandleFailure(apiResult.code, apiResult.message, apiResult.callback);
            }
        }
    }

    /**
     * Downloads and saves actions
     */
    private class GetActions extends AsyncTask<ApiParams, Void, ApiResult> {
        @Override
        protected ApiResult doInBackground(ApiParams... params) {
            Log.d(Const.TAG, "API: getActions");

            User currentUser = db.getUser();
            ApiResult result = getResult(BASE_URL + "actions/", "GET", params[0], currentUser.apiKey);

            if (result.successfull) {
                try {
                    // Go through every friend and add him to database
                    JSONArray actionsJson = result.json.getJSONArray("actions");

                    for (int i=0;i<actionsJson.length();i++) {
                        JSONObject actionJson = actionsJson.getJSONObject(i);
                        Action action = Action.fromJson(actionJson);
                        db.addAction(action);
                    }

                } catch (Exception e) {
                    result.message = e.getMessage();
                    result.successfull = false;
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(ApiResult apiResult) {
            if (apiResult.successfull) {
                apiResult.callback.onSuccess(API_GET_ACTIONS);
            } else {
                apiFailureHandler.HandleFailure(apiResult.code, apiResult.message, apiResult.callback);
            }
        }
    }

    /**
     * Sends debts to the server
     * Downloads all most current debts
     * Deletes all old debts and saves the new ones into the local database
     */
    private class UpdateDebts extends AsyncTask<ApiParams, Void, ApiResult> {
        @Override
        protected ApiResult doInBackground(ApiParams... params) {
            Log.d(Const.TAG, "API: updateDebts");

            User currentUser = db.getUser();

            // Create a json with all offline debts
            JSONObject debtsJson = new JSONObject();

            try {
                ArrayList<Debt> offlineDebts = db.getDebts();
                JSONArray offlineDebtsJson = new JSONArray();

                for (Debt debt : offlineDebts) {
                    offlineDebtsJson.put(debt.toJson());
                }

                debtsJson.put("debts", offlineDebtsJson);
            } catch (Exception e) {
                ApiResult result = new ApiResult();
                result.successfull = false;
                result.message = e.getMessage();
                return result;
            }

            params[0].jsonToSend = debtsJson;

            ApiResult result = getResult(BASE_URL + "debts/update", "POST", params[0], currentUser.apiKey);

            if (result.successfull) {
                try {
                    JSONArray onlineDebtsArr = result.json.getJSONArray("debts");

                    // Update only if we receive correct number of debts
                    if (onlineDebtsArr.length() >= onlineDebtsArr.length()) {
                        // Remove all old debts
                        db.removeOfflineDebts();
                    } else {
                        result.message = "Ok, this error message has no logical explanation AT ALL.";
                        result.successfull = false;
                    }

                    for (int i=0;i<onlineDebtsArr.length();i++) {

                        JSONObject onlineDebtJson = onlineDebtsArr.getJSONObject(i);

                        Debt onlineDebt = Debt.fromJson(onlineDebtJson);
                        // Add every updated debt
                        db.addOrUpdateDebt(onlineDebt.id, onlineDebt);
                    }

                } catch (Exception e) {
                    result.successfull = false;
                    result.message = e.getMessage();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(ApiResult apiResult) {
            if (apiResult.successfull) {
                apiResult.callback.onSuccess(API_UPDATE_DEBTS);
            } else {
                apiFailureHandler.HandleFailure(apiResult.code, apiResult.message, apiResult.callback);
            }
        }
    }

    // --------------------------------------- Functions below only run multiple of functions in a row ------------------------------------------ //

    private void updateDebtsAndActions(final ApiParams params) {

        ApiParams customParams = new ApiParams();
        customParams.callback = new SimpleCallback() {
            @Override
            public void onSuccess(int apiMethodCode) {
                new GetActions().execute(params);
            }

            @Override
            public void onFailure(String message) {
                params.callback.onFailure(message);
            }
        };

        new UpdateDebts().execute(customParams);
    }

    private void updateAll(final ApiParams params) {
        final ApiParams customParams = new ApiParams();

        customParams.callback = new SimpleCallback() {
            @Override
            public void onSuccess(int apiMethodCode) {
                switch (apiMethodCode) {
                    case API_GET_USER:
                        new GetCurrencies().execute(customParams);
                        break;
                    case API_GET_CURENCIES:
                        new UpdateDebts().execute(customParams);
                        break;
                    case API_UPDATE_DEBTS:
                        new GetActions().execute(params);
                        break;
                }
            }

            @Override
            public void onFailure(String message) {
                params.callback.onFailure(message);
            }
        };

        new GetUser().execute(customParams);
    }

    private void loginAndUpdateAll(final ApiParams params) {

        ApiParams customParams = new ApiParams();
        customParams.jsonToSend = params.jsonToSend;
        customParams.callback = new SimpleCallback() {
            @Override
            public void onSuccess(int apiMethodCode) {
                // Login is the only task, that requires jsonToSend data from the outside. Therefore, it needs to be deleted.
                params.jsonToSend = null;
                updateAll(params);
            }

            @Override
            public void onFailure(String message) {
                params.callback.onFailure(message);
            }
        };

        new Login().execute(customParams);
    }

    // --------------------------------------- Other private methods ------------------------------------------ //

    private ApiResult getResult(String address, String method, ApiParams params, String apiKey) {

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

            // Set api key
            if (apiKey != null) {
                urlConnection.setRequestProperty("api-key", apiKey);
            }

            // Send JSON data
            if (params.jsonToSend != null) {
                Log.d(Const.TAG, "jsonToSend is set:" + params.jsonToSend.toString());
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

    /**
     * Checks if user is connected to wifi or has data connection enabled.
     * Does not necessarily mean that the user has internet connection.
     * @return bool
     */
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
