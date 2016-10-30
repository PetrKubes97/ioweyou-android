package cz.petrkubes.payuback.Activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.petrkubes.payuback.Adapters.FragmentsAdapter;
import cz.petrkubes.payuback.Api.ApiRestClient;
import cz.petrkubes.payuback.Database.DatabaseHandler;
import cz.petrkubes.payuback.R;
import cz.petrkubes.payuback.Structs.Friend;
import cz.petrkubes.payuback.Structs.User;

/**
 * Created by petr on 16.10.16.
 */

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Button button;

    private FloatingActionButton btnAddDebt;
    private String facebookId;
    private String facebookToken;
    private DatabaseHandler db;
    private FragmentsAdapter pageAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);

        // Setup views and buttons
        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.btn_main);
        btnAddDebt = (FloatingActionButton) findViewById(R.id.btn_add_debt);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Setup tabs
        pageAdapter = new FragmentsAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // Setup database
        db = new DatabaseHandler(getApplicationContext());

        // Get facebookId and token from the login activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            facebookId = extras.getString("facebookId");
            facebookToken = extras.getString("facebookToken");
            textView.setText("Facebook Id: " + facebookId + "\n" + "Facebook token: " + facebookToken);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(facebookId, facebookToken);
                getUser("higq5Qi5803XyOXdDCmiexXz07bbaOWxM9bTEXqj9QnSpYLYqDlHKgBtvhgsany");
            }
        });

        // Start a new activity, in which user adds debts
        btnAddDebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DebtActivity.class);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){

                    ActivityOptions options = ActivityOptions.
                            makeSceneTransitionAnimation(MainActivity.this, btnAddDebt, getString(R.string.transition_button));
                    startActivity(intent, options.toBundle());
                } else{
                    startActivity(intent);
                }
            }
        });


    }

    public void getUser(String apiKey) {
        ArrayList<cz.petrkubes.payuback.Structs.Header> headers = new ArrayList<>();
        headers.add(new cz.petrkubes.payuback.Structs.Header("api-key", apiKey));

        ApiRestClient.get("user/", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getApplicationContext(), response.toString() + String.valueOf(statusCode), Toast.LENGTH_LONG).show();

                try {
                    // Go through every friend and add him to database
                    JSONArray friendsJson = response.getJSONArray("friends");

                    for (int i=0;i<friendsJson.length();i++) {
                        JSONObject friendJson = friendsJson.getJSONObject(i);
                        Friend friend = new Friend(
                                friendJson.getInt("id"),
                                friendJson.getString("name"),
                                friendJson.getString("email"),
                                friendJson.getString("facebookId")
                        );
                        try {
                            db.addFriend(friend);
                        } catch (Exception e) {
                            Log.d("fdsa", e.getMessage());
                        }

                    }


                    // It is necessary to convert date string to Date class
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    User user = new User(
                            response.getInt("id"),
                            response.getString("email"),
                            response.getString("name"),
                            response.getString("facebookId"),
                            response.getString("facebookToken"),
                            null,
                            null);

                    try {
                        db.addUser(user);
                    } catch (Exception e) {
                        Log.d("fdsaa", e.getMessage());
                    }


                } catch (JSONException e) {
                    Log.d("fdsaaa", e.getMessage());
                }
            }
        }, headers);

    }

    public void loginUser(String facebookId, String facebookToken) {

        RequestParams params = new RequestParams();
        params.put("facebookToken", facebookToken);
        params.put("facebookId", facebookId);

        ApiRestClient.post("user/login", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getApplicationContext(), response.toString() + String.valueOf(statusCode), Toast.LENGTH_SHORT).show();
                // TODO get api key and update database

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(), responseString + String.valueOf(statusCode), Toast.LENGTH_LONG).show();
                Log.d("debug", responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getApplicationContext(), errorResponse.toString() + String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }

        });
    }


}
