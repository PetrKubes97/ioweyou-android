package cz.petrkubes.ioweyou.Api;

import android.content.Context;
import android.util.Log;

import cz.petrkubes.ioweyou.Activities.MainActivity;
import cz.petrkubes.ioweyou.Database.DatabaseHandler;
import cz.petrkubes.ioweyou.Tools.Const;

/**
 * Class used for handling api error
 *
 * @author Petr Kubes
 */
class ApiFailureHandler {

    private static final int UNAUTHORIZED = 401;

    // Error codes made up by me
    private static final int NO_INTERNET = 600;

    private Context context;
    private DatabaseHandler db;

    ApiFailureHandler(Context context, DatabaseHandler db) {
        this.db = db;
        this.context = context;
    }

    /**
     * @param StatusCode ex. 400, 500
     * @param message    Error message
     * @param callback   callback
     */
    void HandleFailure(int StatusCode, String message, final SimpleCallback callback) {

        Log.d(Const.TAG, "API FAILURE: " + message);

        // Log user out when the api keys don't match
        if (StatusCode == UNAUTHORIZED) {

            if (context instanceof MainActivity) { // this should be true every time, because Api is created only in that Activity
                ((MainActivity) context).logOut();
            }

        } else {
            callback.onFailure(message);
        }

    }

}
