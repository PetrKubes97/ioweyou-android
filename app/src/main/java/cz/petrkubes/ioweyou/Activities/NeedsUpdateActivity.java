package cz.petrkubes.ioweyou.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import cz.petrkubes.ioweyou.R;

/**
 * This Activity shows up, when server's api is not compatible with the old version of the android app
 *
 * @author Petr Kubes
 */

public class NeedsUpdateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needs_update);
    }
}
