package cz.petrkubes.ioweyou.Api;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import cz.petrkubes.ioweyou.Activities.LoginActivity;
import cz.petrkubes.ioweyou.Activities.MainActivity;
import cz.petrkubes.ioweyou.Activities.NeedsUpdateActivity;
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

    public static final String NEEDS_UPDATE = "needs_update";

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
        } else if (message.equals(NEEDS_UPDATE)) {
            callback.onFailure(message);
            needsUpdate();
        } else {
            callback.onFailure(message);
        }

    }

    /**
     * Logs user out of facebook, truncates database and launches needsUpdate activity
     */
    private void needsUpdate() {
        // Logout from facebook
        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(context);
        }
        LoginManager.getInstance().logOut();

        // Cancel all background jobs
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();

        // Truncate db
        db.truncate();
        Intent intent = new Intent(context, NeedsUpdateActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
