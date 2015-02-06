package com.sevenflying.greenhouseclient.app;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sevenflying.greenhouseclient.app.actuatorstab.ActuatorListFragment;
import com.sevenflying.greenhouseclient.app.alertstab.AlertListFragment;
import com.sevenflying.greenhouseclient.app.sensortab.SensorsListFragment;
import com.sevenflying.greenhouseclient.app.statustab.StatusFragment;

/** Manages the tabs
 * Created by 7flying on 25/06/2014.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    private Activity reference;
    private final int[] tabNames = {R.string.tab_status, R.string.tab_sensors, R.string.tab_alerts, R.string.tab_actuators };
    private FragmentManager fragmentManager;
    private StatusFragment statusFragment;
    private SensorsListFragment sensorsListFragment;
    private AlertListFragment alertListFragment;
    private ActuatorListFragment actuatorListFragment;

    public TabsPagerAdapter(Activity reference, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.reference = reference;
        this.fragmentManager = fragmentManager;
    }

    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                statusFragment = new StatusFragment();
                return statusFragment;
            case 1:
                sensorsListFragment = new SensorsListFragment();
                return sensorsListFragment;
            case 2:
                alertListFragment = new AlertListFragment();
                return alertListFragment;
            case 3:
                actuatorListFragment = new ActuatorListFragment();
                return actuatorListFragment;
            default:
                return null;
        }
    }

    public int getCount() {
        return  tabNames.length;
    }

    public void update(int index) {
        switch (index) {
            case 0:
                statusFragment.update();
                break;
            case 1:
                sensorsListFragment.update();
                break;
            case 2:
                alertListFragment.update();
                break;
            case 3:
                actuatorListFragment.update();
                break;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return reference.getResources().getString(R.string.tab_status);
            case 1:
                return reference.getResources().getString(R.string.tab_sensors);
            case 2:
                return reference.getResources().getString(R.string.tab_alerts);
            case 3:
                return reference.getResources().getString(R.string.tab_actuators);
            default:
                return "Tab";
        }
    }
}
