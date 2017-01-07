package cz.petrkubes.ioweyou.Services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;
import android.widget.Toast;

import cz.petrkubes.ioweyou.Api.Api;
import cz.petrkubes.ioweyou.Api.SimpleCallback;
import cz.petrkubes.ioweyou.Const;
import cz.petrkubes.ioweyou.Pojos.ApiParams;

/**
 * Created by petr on 7.1.17.
 */

public class UpdateAllService extends JobService {
    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.i(Const.TAG, "on start job: " + params.getJobId());

        Api api = new Api(getApplicationContext());

        ApiParams apiParams = new ApiParams();
        apiParams.callback = new SimpleCallback() {
            @Override
            public void onSuccess(int apiMethodCode) {
                jobFinished(params, false);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getApplicationContext(), "IOweYou background service stopped working. :'(", Toast.LENGTH_SHORT).show();
            }
        };

        api.download(Api.API_UPDATE_ALL, apiParams);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(Const.TAG, "on stop job: " + params.getJobId());
        Toast.makeText(getApplicationContext(), "IOweYou background service stopped working. :'(", Toast.LENGTH_SHORT).show();
        return true;
    }
}
