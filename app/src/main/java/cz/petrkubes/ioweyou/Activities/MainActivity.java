package cz.petrkubes.ioweyou.Activities;

import android.app.ActivityOptions;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.stetho.Stetho;

import cz.petrkubes.ioweyou.Adapters.FragmentsAdapter;
import cz.petrkubes.ioweyou.Api.Api;
import cz.petrkubes.ioweyou.Api.ApiRestClient;
import cz.petrkubes.ioweyou.Api.SimpleCallback;
import cz.petrkubes.ioweyou.Const;
import cz.petrkubes.ioweyou.Database.DatabaseHandler;
import cz.petrkubes.ioweyou.Pojos.ApiParams;
import cz.petrkubes.ioweyou.R;
import cz.petrkubes.ioweyou.Pojos.User;

/**
 * Created by petr on 16.10.16.
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
    private ProgressBar toolbarPragressBar;
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;

    private ApiRestClient apiClient;
    private User user;
    private Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        // Setup actionbar
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbarPragressBar = (ProgressBar) findViewById(R.id.toolbar_progress_bar);
        toolbarPragressBar.setVisibility(View.GONE);

        // Parent view for snackbar
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        // Setup views and buttons
        btnAddDebt = (FloatingActionButton) findViewById(R.id.btn_add_debt);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Setup tabs
        pageAdapter = new FragmentsAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        pageAdapter.notifyDataSetChanged();

        // Setup database
        db = new DatabaseHandler(getApplicationContext());

        // Setup api client for synchronization
        apiClient = new ApiRestClient(this);
        api = new Api(this);
        user = db.getUser();

        // Start a new activity in which user adds debts
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

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, btnAddDebt, getString(R.string.transition_button));
                    startActivityForResult(intent, ADD_DEBT_REQUEST, options.toBundle());
                } else{
                    startActivityForResult(intent, ADD_DEBT_REQUEST);
                }
            }
        });
    }

    /**
     * Synces debts after user adds/updates a debt
     * @param requestCode
     * @param resultCode
     * @param data
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
     * @param item
     * @return
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
                    Toast.makeText(getApplicationContext(), "Neni", Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Inflate action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
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

    public void updateDebtsAndActions() {
        pageAdapter.notifyDataSetChanged();
        toggleLoading();

        apiClient.updateDebtsAndActions(user.apiKey, new SimpleCallback() {
            @Override
            public void onSuccess() {
                toggleLoading();
                pageAdapter.notifyDataSetChanged();
                Snackbar.make(coordinatorLayout, getString(R.string.changes_synced), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                toggleLoading();
                Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void updateAll() {

        ApiParams params = new ApiParams();
        params.callback = new SimpleCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(String message) {

            }
        };
        api.download(Api.API_GET_USER, params);


        toggleLoading();

        apiClient.updateAll(user.apiKey, new SimpleCallback() {
            @Override
            public void onSuccess() {
                toggleLoading();
                pageAdapter.notifyDataSetChanged();
                Snackbar.make(coordinatorLayout, getString(R.string.changes_synced), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                toggleLoading();
                Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
            }
        });
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

        // Truncate db
        db.truncate();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }


    private void toggleLoading() {
        final MenuItem item = toolbar.getMenu().getItem(0);

        if (item.isVisible()) {
            item.setVisible(false);
            toolbarPragressBar.setVisibility(View.VISIBLE);
        } else {
            item.setVisible(true);
            toolbarPragressBar.setVisibility(View.GONE);
        }
    }
}
