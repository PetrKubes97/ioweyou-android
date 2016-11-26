package cz.petrkubes.payuback.Activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import cz.petrkubes.payuback.Adapters.FragmentsAdapter;
import cz.petrkubes.payuback.Api.ApiRestClient;
import cz.petrkubes.payuback.Api.SimpleCallback;
import cz.petrkubes.payuback.Const;
import cz.petrkubes.payuback.Database.DatabaseHandler;
import cz.petrkubes.payuback.R;
import cz.petrkubes.payuback.Pojos.User;

/**
 * Created by petr on 16.10.16.
 */

public class MainActivity extends AppCompatActivity {

    public static final int ADD_DEBT_REQUEST = 0;
    public static final String MY_DEBT = "myDebt";

    private FloatingActionButton btnAddDebt;
    private DatabaseHandler db;
    private FragmentsAdapter pageAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private ApiRestClient apiClient;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        // Setup actionbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Setup views and buttons
        btnAddDebt = (FloatingActionButton) findViewById(R.id.btn_add_debt);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Setup tabs
        pageAdapter = new FragmentsAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // Setup database
        db = new DatabaseHandler(getApplicationContext());

        // Setup api client for synchronization
        apiClient = new ApiRestClient(getApplicationContext());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(Const.TAG, "Returned to the main activity " + String.valueOf(requestCode) + String.valueOf(resultCode));

        if (requestCode == ADD_DEBT_REQUEST && resultCode == RESULT_OK) {
            updateDebts();
        }
    }

    public void updateDebts() {
        // Refresh offline changes
        pageAdapter.notifyDataSetChanged();

        apiClient.updateAllDebts(user.apiKey, new SimpleCallback() {
            @Override
            public void onSuccess() {
                // Refresh online changes
                pageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    // Handle action bar actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_refresh:
                // Refresh everything
                if (user != null) {
                    apiClient.updateAll(user.apiKey, new SimpleCallback() {
                        @Override
                        public void onSuccess() {
                            pageAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(getApplicationContext(), "Something went wrong. :(", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Neni", Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    // Inflate action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

}
