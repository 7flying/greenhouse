package com.sevenflying.greenhouseclient.app.sensortab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.Sensor;

import java.util.ArrayList;

/**
 * Created by user on 25/06/2014.
 */
public class SensorsFragment extends Fragment {

	private ListView listView;
	private ArrayList<Sensor> sensorList;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundle) {
        
		if(container == null)
			return null;
		else {
			View view = inflater.inflate(R.layout.fragment_sensors, container, false);
			listView = (ListView) view.findViewById(R.id.sensorsListView);
            ArrayList<Sensor> sensorList = new ArrayList<Sensor>();
            sensorList.add(new Sensor("Temperature", "25.6", "Cº"));
        	sensorList.add(new Sensor("Humidity", "40", "ux"));
        	listView.setAdapter(new SensorAdapter(getActivity(),R.layout.sensor_list_row,sensorList));
        	return view;
		}
        //return  inflater.inflate(R.layout.fragment_alerts, container, false);

    }

/*
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View v = super.onCreateView(inflater, container, savedInstance);
        ArrayList<Sensor> list = new ArrayList<Sensor>();

        list.add(new Sensor("Temperature", "25.6", "Cº"));
        list.add(new Sensor("Humidity", "40", "ux"));
        setListAdapter(new SensorAdapter(getActivity(), list));
        return v;
    }
*/
}
