package cz.petrkubes.payuback.Api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import cz.petrkubes.payuback.Activities.LoginActivity;
import cz.petrkubes.payuback.Activities.MainActivity;
import cz.petrkubes.payuback.Const;
import cz.petrkubes.payuback.Database.DatabaseHandler;

/**
 * Created by petr on 11.12.16.
 */

class ApiFailureHandler {

    private static final int UNAUTHORIZED = 401;

    private Context context;
    private DatabaseHandler db;

    ApiFailureHandler(Context context, DatabaseHandler db) {
        this.db = db;
        this.context = context;
    }

    void HandleFailure(int StatusCode, String message, final SimpleCallback callback) {

        Log.d(Const.TAG, "API FAILURE: " + message );
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        // Log user out when the api keys don't match
        if (StatusCode == UNAUTHORIZED) {

            if(context instanceof MainActivity){ // this should be true every time, because ApiRestClient is created only in that Activity
                ((MainActivity) context).logOut();
            }

        } else {
            callback.onFailure();
        }

    }

}
