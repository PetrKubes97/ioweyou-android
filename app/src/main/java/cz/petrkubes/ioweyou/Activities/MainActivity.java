package cz.petrkubes.ioweyou.Activities;

import android.app.ActivityOptions;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import cz.petrkubes.ioweyou.Adapters.FragmentsAdapter;
import cz.petrkubes.ioweyou.Api.Api;
import cz.petrkubes.ioweyou.Api.SimpleCallback;
import cz.petrkubes.ioweyou.Database.DatabaseHandler;
import cz.petrkubes.ioweyou.Pojos.ApiParams;
import cz.petrkubes.ioweyou.Pojos.User;
import cz.petrkubes.ioweyou.R;
import cz.petrkubes.ioweyou.Services.UpdateAllService;

/**
 * Main activity includes all fragments and a viewPager, which displays the fragments
 *
 * @author Petr Kubes
 */

public class MainActivity extends AppCompatActivity {

    public static final int ADD_DEBT_REQUEST = 0;
    public static final String MY_DEBT = "myDebt";

    // Widgets
    private FloatingActionButton btnAddDebt;
    private DatabaseHandler db;
    private FragmentsAdapter pageAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ProgressBar toolbarProgressBar;
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;

    private User user;
    private Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Library only for testing purposes
        //Stetho.initializeWithDefaults(this);

        setContentView(R.layout.activity_main);

        // Setup actionbar
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbarProgressBar = (ProgressBar) findViewById(R.id.toolbar_progress_bar);
        toolbarProgressBar.setVisibility(View.GONE);

        // Parent view for snackbar
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        // Setup views and buttons
        btnAddDebt = (FloatingActionButton) findViewById(R.id.btn_add_debt);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Setup tabs
        pageAdapter = new FragmentsAdapter(getSupportFragmentManager(), getApplicationContext());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pageAdapter);
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(viewPager);
        pageAdapter.notifyDataSetChanged();

        // Setup database
        db = new DatabaseHandler(getApplicationContext());

        // Setup api client for synchronization
        api = new Api(this);
        user = db.getUser();

        // Start a new activity in which user adds debts when user click the plus button
        btnAddDebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DebtActivity.class);

                // put extra boolean to the intent, so that DebtActivity knows which radio button to check
                boolean myDebt = true;
                if (viewPager.getCurrentItem() == 1) {
                    myDebt = false;
                }

                intent.putExtra(MY_DEBT, myDebt);

                // Make transition only on newer android version
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, btnAddDebt, getString(R.string.transition_button));
                    startActivityForResult(intent, ADD_DEBT_REQUEST, options.toBundle());
                } else {
                    startActivityForResult(intent, ADD_DEBT_REQUEST);
                }
            }
        });

        // Start a background job
        startBackgroundJob();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pageAdapter.notifyDataSetChanged();

        if (user == null) {
            logOut();
        }
    }

    /**
     * Syncs debts after user adds/updates a debt
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_DEBT_REQUEST && resultCode == RESULT_OK) {
            updateDebtsAndActions();
        }
    }

    /**
     * Handles toolbar actions
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:

                logOut();
                return true;

            case R.id.action_refresh:

                // Refresh everything
                if (user != null) {
                    updateAll();
                } else {
                    // This should not happen during normal app usage. Used for testing.
                    Toast.makeText(getApplicationContext(), "User is not logged in", Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Inflates actionbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * This is kind of a hacky solution. Toolbar progressbar cannot be updated before the menu is created, so I can't update debts onStart or onCreate of the Actrivity
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Updates debts and actions when the user launches the app
        // This is necessary, because JobScheduler job can be turned off by Android system
        updateDebtsAndActions();

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Show an alertDialog on pressing a back button
     */
    @Override
    public void onBackPressed() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(getString(R.string.are_you_sure_you_want_to_exit));
        dialogBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.no), null);
        dialogBuilder.show();
    }

    /**
     * Calls Api download method to update debts and actions and processes callback
     */
    public void updateDebtsAndActions() {
        pageAdapter.notifyDataSetChanged();
        toggleLoading();

        ApiParams params = new ApiParams();
        params.callback = new SimpleCallback() {
            @Override
            public void onSuccess(int apiMethodCode) {
                toggleLoading();
                pageAdapter.notifyDataSetChanged();
                Snackbar.make(coordinatorLayout, getString(R.string.changes_synced), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                toggleLoading();
                Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
            }
        };

        api.download(Api.API_UPDATE_DEBTS_AND_ACTIONS, params);
    }

    /**
     * Calls Api download method to update everything and processes callback
     */
    public void updateAll() {
        toggleLoading();

        ApiParams params = new ApiParams();
        params.callback = new SimpleCallback() {
            @Override
            public void onSuccess(int apiMethodCode) {
                toggleLoading();
                pageAdapter.notifyDataSetChanged();
                Snackbar.make(coordinatorLayout, getString(R.string.changes_synced), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                toggleLoading();
                Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
            }
        };

        api.download(Api.API_UPDATE_ALL, params);
    }

    /**
     * Logs user out of facebook, truncates database and launches Login activity
     */
    public void logOut() {
        // Logout from facebook
        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(getApplicationContext());
        }
        LoginManager.getInstance().logOut();

        // TODO empty api key on server

        // Cancel all background jobs
        JobScheduler jobScheduler = (JobScheduler) getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();

        // Truncate db
        db.truncate();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }


    /**
     * Toggles loading in the toolbar
     */
    private void toggleLoading() {

        final MenuItem item = toolbar.getMenu().getItem(0);

        if (item.isVisible()) {
            item.setVisible(false);
            toolbarProgressBar.setVisibility(View.VISIBLE);
        } else {
            item.setVisible(true);
            toolbarProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Starts a JobScheduler, which launches UpdateAllService to update the database every few hours
     * First checks if job doesn't already exist, if yes, does not create any new jobs
     */
    public void startBackgroundJob() {
        JobScheduler jobScheduler = (JobScheduler) getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler.getAllPendingJobs().size() < 1) {
            ComponentName mServiceComponent = new ComponentName(this, UpdateAllService.class);
            JobInfo jobInfo = new JobInfo.Builder(0, mServiceComponent)
                    .setPeriodic(4 * 60 * 60 * 1000) // 4 hours
                    .setRequiresCharging(false)
                    .setPersisted(true)
                    .build();
            jobScheduler.cancelAll();
            jobScheduler.schedule(jobInfo);
        }
    }
}
