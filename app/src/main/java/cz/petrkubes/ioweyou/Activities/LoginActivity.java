package cz.petrkubes.ioweyou.Activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import cz.petrkubes.ioweyou.Api.Api;
import cz.petrkubes.ioweyou.Api.SimpleCallback;
import cz.petrkubes.ioweyou.Database.DatabaseHandler;
import cz.petrkubes.ioweyou.Pojos.ApiParams;
import cz.petrkubes.ioweyou.R;

/**
 * Login screen with facebook login button
 *
 * @author Petr Kubes
 */
public class LoginActivity extends AppCompatActivity {

    // Widgets
    private LoginButton loginButton;
    private ProgressBar prgLoader;
    private TextView txtLoadingDescription;
    private TextView txtVersion;

    private Api api;
    private DatabaseHandler db;
    private CallbackManager callbackManager;

    @Override
    protected void onResume() {
        super.onResume();
        // Starts application if the user is already logged in
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            if (db.getUser() != null) {
                startMainActivity();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        // widgets
        loginButton = (LoginButton) this.findViewById(R.id.login_button);
        prgLoader = (ProgressBar) this.findViewById(R.id.prg_loader);
        txtLoadingDescription = (TextView) this.findViewById(R.id.txt_loading_description);
        txtVersion = (TextView) this.findViewById(R.id.txt_version);

        loginButton.setReadPermissions("user_friends", "email");

        loginButton.setVisibility(View.VISIBLE);
        txtLoadingDescription.setVisibility(View.GONE);
        prgLoader.setVisibility(View.GONE);

        api = new Api(getApplicationContext());
        db = new DatabaseHandler(getApplicationContext());

        // Facebook login callback
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Starts loading tasks
                loginUser(loginResult.getAccessToken().getUserId(), loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                // do nothing
            }

            @Override
            public void onError(FacebookException exception) {
                // do nothing
            }
        });


        // Get version code and display it on the login screen
        String version = "Unknown";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        txtVersion.setText(version);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Facebook callback
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Displays progress bar, logs user in, and starts main activity
     *
     * @param facebookId    user's facebook id
     * @param facebookToken user's facebook token
     */
    private void loginUser(String facebookId, final String facebookToken) {

        // Show loading widgets and hide others
        loginButton.setVisibility(View.GONE);
        prgLoader.setVisibility(View.VISIBLE);
        txtLoadingDescription.setVisibility(View.VISIBLE);
        txtLoadingDescription.setText(getResources().getString(R.string.loading_login));

        // Create a JSON with facebook credentials and send it to the server
        JSONObject jsonWithFbCredentials = new JSONObject();
        try {
            jsonWithFbCredentials.put("facebookId", facebookId);
            jsonWithFbCredentials.put("facebookToken", facebookToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Send http request
        ApiParams params = new ApiParams();
        params.jsonToSend = jsonWithFbCredentials;
        params.callback = new SimpleCallback() {
            @Override
            public void onSuccess(int apiMethodCode) {
                // Starts main activity and hides loading widgets
                prgLoader.setVisibility(View.GONE);
                txtLoadingDescription.setVisibility(View.GONE);
                startMainActivity();
            }

            @Override
            public void onFailure(String message) {
                // Logs out from facebook when the server login does not succeed
                LoginManager.getInstance().logOut();
                loginButton.setVisibility(View.VISIBLE);
                txtLoadingDescription.setVisibility(View.GONE);
                prgLoader.setVisibility(View.GONE);

            }
        };
        api.download(Api.API_LOGIN_AND_UPDATE_ALL, params);

    }

    /**
     * Starts main activity
     */
    private void startMainActivity() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
