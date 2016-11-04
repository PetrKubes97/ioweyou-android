package cz.petrkubes.payuback.Api;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.petrkubes.payuback.Const;
import cz.petrkubes.payuback.Database.DatabaseHandler;
import cz.petrkubes.payuback.Structs.Currency;
import cz.petrkubes.payuback.Structs.Debt;
import cz.petrkubes.payuback.Structs.Friend;
import cz.petrkubes.payuback.Structs.User;

public class ApiRestClient {

    private static final String BASE_URL = "http://192.168.2.71/payuback-api/www/api/";
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
                Toast.makeText(context, response.toString() + String.valueOf(statusCode), Toast.LENGTH_LONG).show();

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
                        try {
                            db.addFriend(friend);
                        } catch (Exception e) {
                            Log.d(Const.TAG, e.getMessage());
                        }
                    }

                    // It is necessary to convert date string to Date class
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date registeredAt = null;
                    try {
                        registeredAt = df.parse(response.getString("registeredAt"));
                    } catch (ParseException e) {
                        Log.d(Const.TAG, e.getMessage());
                    }

                    User user = new User(
                            response.getInt("id"),
                            null,
                            response.getString("email"),
                            response.getString("name"),
                            registeredAt);

                    db.addOrUpdateUser(user);

                } catch (JSONException e) {
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
                Toast.makeText(context, response.toString() + String.valueOf(statusCode), Toast.LENGTH_SHORT).show();

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
                Toast.makeText(context, response.toString() + String.valueOf(statusCode), Toast.LENGTH_LONG).show();

                try {
                    // Go through every currency and add it to the database
                    JSONArray friendsJson = response.getJSONArray("currencies");

                    for (int i=0;i<friendsJson.length();i++) {

                        JSONObject currencyJson = friendsJson.getJSONObject(i);

                        Currency currency = new Currency(
                                currencyJson.getInt("id"),
                                currencyJson.getString("symbol"));

                        try {
                            db.addCurrency(currency);
                        } catch (Exception e) {
                            Log.d(Const.TAG, e.getMessage());
                        }
                    }

                    callback.onSuccess();

                } catch (JSONException e) {
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

    public void addDebt(String apiKey, Debt debt, final SimpleCallback callback) {

        client.addHeader("api-key", apiKey);

        RequestParams params = new RequestParams();
        params.put("creditorId", debt.creatorId);
        params.put("debtorId", debt.debtorId);
        params.put("customFriendName", debt.customFriendName);
        params.put("amount", debt.amount);
        params.put("currencyId", debt.currencyId);
        params.put("thingName", debt.thingName);
        params.put("note", debt.note);

        client.post(getAbsoluteUrl("debts/new"), params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
