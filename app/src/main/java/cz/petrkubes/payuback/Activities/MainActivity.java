package cz.petrkubes.payuback.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.petrkubes.payuback.Api.ApiRestClient;
import cz.petrkubes.payuback.R;

/**
 * Created by petr on 16.10.16.
 */

public class MainActivity extends Activity {

    private TextView textView;
    private String facebookId;
    private String facebookToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            facebookId = extras.getString("facebookId");
            facebookToken = extras.getString("facebookToken");
        }

        textView.setText("Facebook Id: " + facebookId + "\n" + "Facebook token: " + facebookToken);

        try {
            getUserInfo();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getUserInfo() throws JSONException {
        ApiRestClient.get("user/me", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getApplicationContext(),response.toString() + String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });
    }

}
