package com.sevenflying.greenhouseclient.app.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/** This activity is used to provide advanced options.
 * Created by 7flying on 18/09/2014.
 */
public class SettingsActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
