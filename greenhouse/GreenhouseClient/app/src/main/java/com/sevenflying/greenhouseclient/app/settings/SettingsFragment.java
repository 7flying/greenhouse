package com.sevenflying.greenhouseclient.app.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.sevenflying.greenhouseclient.app.R;

/** Fragment linked to the SettingsActivity.
 * Created by 7flying on 07/10/2014.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
