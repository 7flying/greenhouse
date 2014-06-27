package com.sevenflying.greenhouseclient.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sevenflying.greenhouseclient.app.sensortab.SensorsFragment;

/**
 * Created by 7flying on 25/06/2014.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new StatusFragment();
            case 1:
                return new StatusFragment();
            case 2:
                return new AlertsFragment();
            default:
                return null;
        }
    }

    public int getCount() {
        return  3;
    }
}