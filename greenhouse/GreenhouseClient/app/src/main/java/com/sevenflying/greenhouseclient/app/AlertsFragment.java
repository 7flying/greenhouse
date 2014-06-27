package com.sevenflying.greenhouseclient.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by user on 25/06/2014.
 */
public class AlertsFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundle) {
        return  inflater.inflate(R.layout.fragment_alerts, container, false);
    }
}
