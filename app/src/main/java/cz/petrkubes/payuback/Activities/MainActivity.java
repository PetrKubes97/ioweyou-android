package cz.petrkubes.payuback.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.petrkubes.payuback.Api.ApiRestClient;
import cz.petrkubes.payuback.Datatabase.DatabaseHandler;
import cz.petrkubes.payuback.R;

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
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.btn_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            facebookId = extras.getString("facebookId");
            facebookToken = extras.getString("facebookToken");
        }

        textView.setText("Facebook Id: " + facebookId + "\n" + "Facebook token: " + facebookToken);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserInfo(facebookId, facebookToken);
            }
        });

        db = new DatabaseHandler(getApplicationContext());


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
