package cz.petrkubes.payuback.Api;

import android.content.Context;
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
import cz.petrkubes.payuback.Const;
import cz.petrkubes.payuback.Database.DatabaseHandler;
import cz.petrkubes.payuback.Pojos.Action;
import cz.petrkubes.payuback.Pojos.Currency;
import cz.petrkubes.payuback.Pojos.Debt;
import cz.petrkubes.payuback.Pojos.Friend;
import cz.petrkubes.payuback.Pojos.User;

public class ApiRestClient {

    private static final String BASE_URL = "http://34.194.114.99/payuback-api/www/api/";
    private AsyncHttpClient client;
    private DatabaseHandler db;
    private Context context;

    public ApiRestClient(Context context) {
        this.client = new AsyncHttpClient();
        this.db = new DatabaseHandler(context);
        this.context = context;
    }

    public void getUser(String apiKey, final SimpleCallback callback) {

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
                    callback.onFailure();
                }
                callback.onSuccess();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                callback.onFailure();
            }
        });
    }

    public void login(String facebookId, String facebookToken, final SimpleCallback callback) {
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
                    e.printStackTrace();
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                callback.onFailure();
            }
        });
    }

    public void getCurrencies(String apiKey, final SimpleCallback callback) {

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
                    Log.d(Const.TAG, e.getMessage());
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                callback.onFailure();
            }
        });
    }

    public void updateAllDebts (String apiKey, final SimpleCallback callback) {
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
            e.printStackTrace();
        }

        Log.d(Const.TAG, "Sending: " + debtsJson.toString());

        client.post(context, getAbsoluteUrl("debts/update"), entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d(Const.TAG, "Receiving: " + response.toString());
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
                    e.printStackTrace();
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Log.d(Const.TAG, errorResponse.toString());
                callback.onFailure();
            }
        });

    }

    public void getActions(String apiKey, final SimpleCallback callback) {

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
                    callback.onFailure();
                }
                callback.onSuccess();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                callback.onFailure();
            }
        });

    }

    public void updateAll(final String apiKey, final SimpleCallback callback) {

        getUser(apiKey, new SimpleCallback() {
            @Override
            public void onSuccess() {
                getCurrencies(apiKey, new SimpleCallback() {
                    @Override
                    public void onSuccess() {

                        getActions(apiKey, new SimpleCallback() {
                            @Override
                            public void onSuccess() {
                                updateAllDebts(apiKey, new SimpleCallback() {
                                    @Override
                                    public void onSuccess() {
                                        callback.onSuccess();
                                    }

                                    @Override
                                    public void onFailure() {
                                        callback.onFailure();
                                    }
                                });
                            }

                            @Override
                            public void onFailure() {
                                callback.onFailure();
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        callback.onFailure();
                    }
                });
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
