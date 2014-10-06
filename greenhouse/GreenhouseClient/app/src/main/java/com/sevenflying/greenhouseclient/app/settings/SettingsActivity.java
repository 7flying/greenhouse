package com.sevenflying.greenhouseclient.app.settings;

import android.app.Activity;
import android.os.Bundle;

/** This activity is used to provide advanced options.
 * Created by 7flying on 18/09/2014.
 */
public class SettingsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
