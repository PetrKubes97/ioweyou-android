package cz.petrkubes.payuback.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.petrkubes.payuback.Api.ApiRestClient;
import cz.petrkubes.payuback.Database.DatabaseHandler;
import cz.petrkubes.payuback.R;
import cz.petrkubes.payuback.Structs.Friend;
import cz.petrkubes.payuback.Structs.User;

/**
 * Created by petr on 16.10.16.
 */

public class MainActivity extends Activity {

    private TextView textView;
    private Button button;
    private String facebookId;
    private String facebookToken;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.btn_main);

        db = new DatabaseHandler(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            facebookId = extras.getString("facebookId");
            facebookToken = extras.getString("facebookToken");
            textView.setText("Facebook Id: " + facebookId + "\n" + "Facebook token: " + facebookToken);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserInfo(facebookId, facebookToken);
            }
        });
    }

    public void getUserInfo(String facebookId, String facebookToken) {

        RequestParams params = new RequestParams();
        params.put("facebookToken", facebookToken);
        params.put("facebookId", facebookId);

        ApiRestClient.post("user/login", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getApplicationContext(),response.toString() + String.valueOf(statusCode), Toast.LENGTH_LONG).show();

                try {
                    // TODO Go through every friend and add him to database

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

                    db.addUser(user);

                } catch (Exception e) {
                    Log.d("PAYUBACK", e.getMessage());
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(),responseString + String.valueOf(statusCode), Toast.LENGTH_LONG).show();
                Log.d("debug",responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getApplicationContext(),errorResponse.toString() + String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }

        });
    }



}
