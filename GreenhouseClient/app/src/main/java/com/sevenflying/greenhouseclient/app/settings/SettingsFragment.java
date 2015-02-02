package com.sevenflying.greenhouseclient.app.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

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
        PreferenceManager.setDefaultValues(this.getActivity().getApplicationContext(),
                   R.xml.preferences, false);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (key.equals(PREF_SERVER_IP)) {
            findPreference(key).setSummary(sharedPreferences.getString(key, ""));
            editor.putString(sharedPreferences.getString(key, ""), PREF_SERVER_IP);
        } else if (key.equals(PREF_SERVER_PORT)) {
            findPreference(key).setSummary(sharedPreferences.getString(key, ""));
            editor.putString(sharedPreferences.getString(key, ""), PREF_SERVER_PORT);
        }
        editor.apply();
    }
}
