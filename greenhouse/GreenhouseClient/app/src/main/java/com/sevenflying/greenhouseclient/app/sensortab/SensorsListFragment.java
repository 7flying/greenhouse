package com.sevenflying.greenhouseclient.app.sensortab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.database.DBManager;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.net.SensorsValueUpdater;

import java.util.ArrayList;

/** This fragment shows the sensor list retrieved from the server.
 * Created by 7flying on 25/06/2014.
 */
public class SensorsListFragment extends Fragment {

	private ListView listView;
	private ArrayList<Sensor> sensorList;
    private LinearLayout layoutProgress;
    private LinearLayout layoutNoConnection;
    private SensorAdapter adapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundle) {

        if(container == null)
			return null;
		else {
            setMenuVisibility(true);
            setHasOptionsMenu(true);
			View view = inflater.inflate(R.layout.fragment_sensors_list, container, false);
			listView = (ListView) view.findViewById(R.id.sensorsListView);
            layoutProgress = (LinearLayout) view.findViewById(R.id.linear_layout_progress);
            layoutNoConnection = (LinearLayout) view.findViewById(R.id.linear_layout_connection);
            sensorList = (ArrayList<Sensor>) new DBManager(
                    getActivity().getApplicationContext()).getSensors();
        	adapter = new SensorAdapter(getActivity(),R.layout.sensor_list_row,sensorList);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                // display further sensor data
                public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                    // Arguments
                    Intent intent = new Intent(SensorsListFragment.this.getActivity(),
                            SensorStatusActivity.class);
                    intent.putExtra("sensor", sensorList.get(index));
                    startActivity(intent);
                }
            });

            // populate list by updaters
            updateSensors();
            return view;
		}
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_sensor_fragment, menu);
        setMenuVisibility(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                // TODO
                return true;
            case R.id.action_settings:
                // TODO
                return true;
            case R.id.action_refresh:
                updateSensors();
                return true;
            case R.id.action_new_sensor:
                startActivity(new Intent(SensorsListFragment.this.getActivity(),
                        SensorCreationActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateSensors(){
        SensorsValueUpdater updater = new SensorsValueUpdater(adapter, layoutProgress,
                layoutNoConnection, getActivity().getApplicationContext(), sensorList);
        updater.execute();
    }
}
