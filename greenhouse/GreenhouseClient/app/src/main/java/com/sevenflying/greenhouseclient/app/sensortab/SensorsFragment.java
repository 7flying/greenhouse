package com.sevenflying.greenhouseclient.app.sensortab;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.Sensor;

import java.util.ArrayList;

/**
 * Created by user on 25/06/2014.
 */
public class SensorsFragment extends ListFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View v = super.onCreateView(inflater, container, savedInstance);
        ArrayList<Sensor> list = new ArrayList<Sensor>();

        list.add(new Sensor("Temperature", "25.6", "Cº"));
        list.add(new Sensor("Humidity", "40", "ux"));
        setListAdapter(new SensorAdapter(getActivity(), list));
        return v;
    }
}
