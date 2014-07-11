package com.sevenflying.greenhouseclient.app.sensortab;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.net.SensorsValueUpdater;

import java.util.ArrayList;

/**
 * Created by 7flying on 25/06/2014.
 */
public class SensorsListFragment extends Fragment {

	private ListView listView;
	private ArrayList<Sensor> sensorList;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundle) {

        if(container == null)
			return null;
		else {
			View view = inflater.inflate(R.layout.fragment_sensors_list, container, false);
			listView = (ListView) view.findViewById(R.id.sensorsListView);
            sensorList = new ArrayList<Sensor>();
        	SensorAdapter adapter = new SensorAdapter(getActivity(),R.layout.sensor_list_row,sensorList);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                // display further sensor data
                public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                    SensorStatusFragment statusFragment = new SensorStatusFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("sensor", sensorList.get(index));
                    statusFragment.setArguments(args);
                   // FragmentTransaction transaction = TODO
                }
            });

            // populate list by updaters
            SensorsValueUpdater updater = new SensorsValueUpdater(adapter);
            updater.execute();
            return view;
		}


    }
}
