package cz.petrkubes.ioweyou.Api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.petrkubes.ioweyou.Const;
import cz.petrkubes.ioweyou.Database.DatabaseHandler;
import cz.petrkubes.ioweyou.Pojos.Action;
import cz.petrkubes.ioweyou.Pojos.Currency;
import cz.petrkubes.ioweyou.Pojos.Debt;
import cz.petrkubes.ioweyou.Pojos.Friend;
import cz.petrkubes.ioweyou.Pojos.User;
import cz.petrkubes.ioweyou.R;

public class ApiRestClient {

    private static final String BASE_URL = "http://34.194.114.99/payuback-api/www/api/";
    private AsyncHttpClient client;
    private DatabaseHandler db;
    private Context context;
    private ApiFailureHandler apiFailureHandler;

    public ApiRestClient(Context context) {
        this.client = new AsyncHttpClient();
        this.db = new DatabaseHandler(context);
        this.context = context;
        this.apiFailureHandler = new ApiFailureHandler(context, this.db);
    }

    public void getUser(String apiKey, final SimpleCallback callback) {

        Log.d(Const.TAG, "Api: getUser");

        if (!isConnected()) {
            apiFailureHandler.HandleFailure(600, context.getString(R.string.no_internet), callback);
            return;
        }

        client.addHeader("api-key",apiKey);

        client.get(getAbsoluteUrl("user/"), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    // Go through every friend and add him to database
                    JSONArray friendsJson = response.getJSONArray("friends");

                    for (int i=0;i<friendsJson.length();i++) {
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

                    registeredAt = df.parse(response.getString("registeredAt"));

                    User user = new User(
                            response.getInt("id"),
                            null,
                            response.getString("email"),
                            response.getString("name"),
                            registeredAt);

                    db.addOrUpdateUser(user);

                } catch (Exception e) {
                    apiFailureHandler.HandleFailure(statusCode, e.getMessage(), callback);
                }
                callback.onSuccess();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (errorResponse != null) {
                    apiFailureHandler.HandleFailure(statusCode, errorResponse.toString(), callback);
                } else {
                    apiFailureHandler.HandleFailure(statusCode, null, callback);
                }
            }
        });
    }

    public void login(String facebookId, String facebookToken, final SimpleCallback callback) {
        Log.d(Const.TAG, "Api: login");

        if (!isConnected()) {
            apiFailureHandler.HandleFailure(600, context.getString(R.string.no_internet), callback);
            return;
        }

        RequestParams params = new RequestParams();
        params.put("facebookToken", facebookToken);
        params.put("facebookId", facebookId);

        client.post(getAbsoluteUrl("user/login"), params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    // 1. case: user logged in for the first time so a new row is created with his id
                    // 2. case: user logged in and already has a row in the database so we just update the api key
                    db.addOrUpdateUser(new User(
                            response.getInt("id"),
                            response.getString("apiKey"),
                            null,
                            null,
                            null
                    ));

                    callback.onSuccess();

                } catch (JSONException e) {
                    apiFailureHandler.HandleFailure(statusCode, e.getMessage(), callback);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (errorResponse != null) {
                    apiFailureHandler.HandleFailure(statusCode, errorResponse.toString(), callback);
                } else {
                    apiFailureHandler.HandleFailure(statusCode, null, callback);
                }
            }
        });
    }

    public void getCurrencies(String apiKey, final SimpleCallback callback) {
        Log.d(Const.TAG, "Api: getCurrencies");
        if (!isConnected()) {
            apiFailureHandler.HandleFailure(600, context.getString(R.string.no_internet), callback);
            return;
        }

        client.addHeader("api-key", apiKey);

        client.get(getAbsoluteUrl("currencies/"), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    // Go through every currency and add it to the database
                    JSONArray currenciesJson = response.getJSONArray("currencies");

                    for (int i=0;i<currenciesJson.length();i++) {

                        JSONObject currencyJson = currenciesJson.getJSONObject(i);

                        Currency currency = new Currency(
                                currencyJson.getInt("id"),
                                currencyJson.getString("symbol"));

                        db.addCurrency(currency);
                    }

                    callback.onSuccess();

                } catch (Exception e) {
                    apiFailureHandler.HandleFailure(statusCode, e.getMessage(), callback);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (errorResponse != null) {
                    apiFailureHandler.HandleFailure(statusCode, errorResponse.toString(), callback);
                } else {
                    apiFailureHandler.HandleFailure(statusCode, null, callback);
                }
            }
        });
    }

    public void updateAllDebts (String apiKey, final SimpleCallback callback) {
        Log.d(Const.TAG, "Api: updateAllDebts");

        if (!isConnected()) {
            apiFailureHandler.HandleFailure(600, context.getString(R.string.no_internet), callback);
            return;
        }

        // Login
        client.addHeader("api-key", apiKey);

        // Create a json with all offline debts
        JSONObject debtsJson = new JSONObject();
        StringEntity entity = null;

        try {

            ArrayList<Debt> offlineDebts = db.getDebts();
            JSONArray offlineDebtsJson = new JSONArray();

            for (Debt debt : offlineDebts) {
                offlineDebtsJson.put(debt.toJson());
            }

            debtsJson.put("debts", offlineDebtsJson);
            entity = new StringEntity(debtsJson.toString(), "UTF-8");

        } catch (Exception e) {
            apiFailureHandler.HandleFailure(0, e.getMessage(), callback);
        }

        client.post(context, getAbsoluteUrl("debts/update"), entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                // Go through every currency and add it to the database
                try {

                    JSONArray onlineDebtsArr = response.getJSONArray("debts");

                    // Update only if we receive correct number of debts
                    if (onlineDebtsArr.length() >= onlineDebtsArr.length()) {
                        // Remove all old debts
                        db.removeOfflineDebts();
                    }

                    for (int i=0;i<onlineDebtsArr.length();i++) {

                        JSONObject onlineDebtJson = onlineDebtsArr.getJSONObject(i);

                        Debt onlineDebt = Debt.fromJson(onlineDebtJson);
                        // Add every updated debt
                        db.addOrUpdateDebt(onlineDebt.id, onlineDebt);
                    }

                    callback.onSuccess();

                } catch (Exception e) {
                    apiFailureHandler.HandleFailure(statusCode, e.getMessage(), callback);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (errorResponse != null) {
                    apiFailureHandler.HandleFailure(statusCode, errorResponse.toString(), callback);
                } else {
                    apiFailureHandler.HandleFailure(statusCode, null, callback);
                }
            }
        });

    }

    public void getActions(String apiKey, final SimpleCallback callback) {
        Log.d(Const.TAG, "Api: getActions");

        if (!isConnected()) {
            apiFailureHandler.HandleFailure(600, context.getString(R.string.no_internet), callback);
            return;
        }

        // Login
        client.addHeader("api-key", apiKey);

        client.get(getAbsoluteUrl("actions/"), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    // Go through every friend and add him to database
                    JSONArray actionsJson = response.getJSONArray("actions");

                    for (int i=0;i<actionsJson.length();i++) {
                        JSONObject actionJson = actionsJson.getJSONObject(i);
                        Action action = Action.fromJson(actionJson);
                        db.addAction(action);
                    }

                } catch (Exception e) {
                    apiFailureHandler.HandleFailure(statusCode, e.getMessage(), callback);
                }
                callback.onSuccess();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (errorResponse != null) {
                    apiFailureHandler.HandleFailure(statusCode, errorResponse.toString(), callback);
                } else {
                    apiFailureHandler.HandleFailure(statusCode, null, callback);
                }
            }
        });

    }

    // ------------------- Functions below only run multiple of functions above ------------------- //

    public void updateDebtsAndActions(final String apiKey, final SimpleCallback callback) {
        Log.d(Const.TAG, "Api: updateDebtsAndActions");

        if (!isConnected()) {
            apiFailureHandler.HandleFailure(600, context.getString(R.string.no_internet), callback);
            return;
        }

        updateAllDebts(apiKey, new SimpleCallback() {
            @Override
            public void onSuccess() {
                getActions(apiKey, new SimpleCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(String message) {
                        callback.onFailure(message);
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                callback.onFailure(message);
            }
        });
    }

    public void updateAll(final String apiKey, final SimpleCallback callback) {
        Log.d(Const.TAG, "Api: updateAll");

        if (!isConnected()) {
            apiFailureHandler.HandleFailure(600, context.getString(R.string.no_internet), callback);
            return;
        }

        getUser(apiKey, new SimpleCallback() {
            @Override
            public void onSuccess() {
                getCurrencies(apiKey, new SimpleCallback() {
                    @Override
                    public void onSuccess() {

                        updateAllDebts(apiKey, new SimpleCallback() {
                            @Override
                            public void onSuccess() {
                                getActions(apiKey, new SimpleCallback() {
                                    @Override
                                    public void onSuccess() {
                                        callback.onSuccess();
                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        callback.onFailure(message);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(String message) {
                                callback.onFailure(message);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String message) {
                        callback.onFailure(message);
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                callback.onFailure(message);
            }
        });
    }

    // ----------------- Other functions --------------------- //

    /**
     * Checks if user is connected to wifi or has data connection enabled.
     * Does not necessarily mean that the user has internet connection.
     * @return bool
     */
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
