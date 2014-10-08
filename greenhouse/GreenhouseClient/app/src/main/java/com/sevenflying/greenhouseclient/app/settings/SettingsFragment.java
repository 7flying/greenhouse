package com.sevenflying.greenhouseclient.app.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.sevenflying.greenhouseclient.app.R;

/** Fragment linked to the SettingsActivity.
 * Created by 7flying on 07/10/2014.
 */
public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener
{

    public static final String PREF_SERVER_IP = "preference_server_ip";
    public static final String PREF_SERVER_PORT = "preference_server_port";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
      }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(PREF_SERVER_IP)) {
            findPreference(s).setSummary("Whatever");
        } else if (s.equals(PREF_SERVER_PORT)) {
            findPreference(s).setSummary(sharedPreferences.getString(s, ""));
        }
    }
}
