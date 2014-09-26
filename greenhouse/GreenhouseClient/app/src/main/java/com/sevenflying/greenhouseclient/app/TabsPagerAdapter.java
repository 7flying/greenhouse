package com.sevenflying.greenhouseclient.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sevenflying.greenhouseclient.app.alertstab.AlertListFragment;
import com.sevenflying.greenhouseclient.app.sensortab.SensorsListFragment;
import com.sevenflying.greenhouseclient.app.statustab.StatusFragment;

/**
 * Created by 7flying on 25/06/2014.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;
    private StatusFragment statusFragment;
    private SensorsListFragment sensorsListFragment;
    private AlertListFragment alertListFragment;

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentManager = fm;
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
            default:
                return null;
        }
    }

    public int getCount() {
        return  3;
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
        }
    }
}
