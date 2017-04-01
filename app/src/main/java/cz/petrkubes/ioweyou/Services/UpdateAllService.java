package cz.petrkubes.ioweyou.Services;

import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.stetho.inspector.protocol.module.Database;

import cz.petrkubes.ioweyou.Api.Api;
import cz.petrkubes.ioweyou.Api.SimpleCallback;
import cz.petrkubes.ioweyou.Database.DatabaseHandler;
import cz.petrkubes.ioweyou.Pojos.ApiParams;
import cz.petrkubes.ioweyou.Tools.Const;

/**
 * Service that is started by the jobsScheduler and every few hours updates the local database
 *
 * @author Petr Kubes
 */
public class UpdateAllService extends JobService {
    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.i(Const.TAG, "on start job: " + params.getJobId());

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        // There can be an update which logs user out
        // In that case, it is necessary to stop the background job and properly log him out
        if (db.getUser() == null) {
            // Logout from facebook
            if (!FacebookSdk.isInitialized()) {
                FacebookSdk.sdkInitialize(getApplicationContext());
            }
            LoginManager.getInstance().logOut();

            // Truncate db
            db.truncate();

            // Cancel all background jobs
            JobScheduler jobScheduler = (JobScheduler) getApplicationContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.cancelAll();

            return false;

        } else {
            Api api = new Api(getApplicationContext());

            ApiParams apiParams = new ApiParams();
            apiParams.callback = new SimpleCallback() {
                @Override
                public void onSuccess(int apiMethodCode) {
                    jobFinished(params, false);
                }

                @Override
                public void onFailure(String message) {
                    jobFinished(params, false);
                }
            };

            api.download(Api.API_UPDATE_ALL, apiParams);

            return true;
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(Const.TAG, "on stop job: " + params.getJobId());
        Toast.makeText(getApplicationContext(), "IOweYou background service stopped working. :'(", Toast.LENGTH_SHORT).show();
        return true;
    }
}
