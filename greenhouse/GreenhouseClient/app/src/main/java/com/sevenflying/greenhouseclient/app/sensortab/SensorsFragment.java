package com.sevenflying.greenhouseclient.app.sensortab;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sevenflying.greenhouseclient.app.R;

import java.util.ArrayList;

/**
 * Created by user on 25/06/2014.
 */
public class SensorsFragment extends ListFragment {

    private ListView listView;
    private ArrayList<Sensor> listSensors;
    private SensorsArrayAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundle) {
        View root =  inflater.inflate(R.layout.fragment_sensors, container, false);
        listView = (ListView) root.findViewById(R.id.list_sensors);
        return root;
    }

    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        listSensors.add(new Sensor("Temperature", "25.8", "Cº"));
        adapter = new SensorsArrayAdapter(getActivity(), android.R.id.list, listSensors);
        listView.setAdapter(adapter);
    }
}
