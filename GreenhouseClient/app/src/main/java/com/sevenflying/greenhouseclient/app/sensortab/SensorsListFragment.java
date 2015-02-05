package com.sevenflying.greenhouseclient.app.sensortab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sevenflying.greenhouseclient.app.ActivityResultHandler;
import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.Updateable;
import com.sevenflying.greenhouseclient.app.database.DBManager;
import com.sevenflying.greenhouseclient.app.utils.Codes;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.net.Communicator;
import com.sevenflying.greenhouseclient.net.SensorsValueUpdater;

import java.util.List;

/** This fragment shows the sensor list retrieved from the server.
 * Created by 7flying on 25/06/2014.
 */
public class SensorsListFragment extends Fragment implements Updateable {

	private List<Sensor> sensorList;
    private ListView listView;
    private LinearLayout layoutProgress;
    private LinearLayout layoutNoConnection;
    private SensorAdapter adapter;
    private DBManager manager;

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
            manager = new DBManager(getActivity().getApplicationContext());
            sensorList = manager.getSensors();
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
            // Context menu stuff
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int listPosition, long l) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getResources().getString(R.string.sensor))
                            .setItems(R.array.edit_delete_array, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int position) {
                                    switch (position) {
                                        case 0: // Edit
                                            Intent intent = new Intent(SensorsListFragment.this
                                                .getActivity(), SensorCreationActivity.class);
                                            intent.putExtra("sensor-to-edit",
                                                    sensorList.get(listPosition));
                                            getActivity().startActivityForResult(intent,
                                                    Codes.CODE_EDIT_SENSOR);
                                            break;
                                        case 1: // Delete
                                            int result = 0;
                                            try {
                                                Communicator com = new Communicator(getActivity()
                                                        .getApplicationContext());
                                                result = com.deleteSensor(
                                                        sensorList.get(listPosition).getPinId(),
                                                        Character.toString(sensorList.get(
                                                                listPosition).getType()
                                                                .getIdentifier()));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                result = -1;
                                            }
                                            if(result == 0) {
                                                manager.deleteSensor(sensorList.get(listPosition));
                                                sensorList.remove(listPosition);
                                                adapter.notifyDataSetChanged();
                                                Toast.makeText(SensorsListFragment.this.getActivity(),
                                                        getResources().getString(
                                                                R.string.sensor_deleted),
                                                                Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(SensorsListFragment.this.getActivity(),
                                                        getResources().getString(
                                                                R.string.sensor_error_delete),
                                                                Toast.LENGTH_SHORT).show();
                                            }
                                            break;
                                    }
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });
            // populate list by updaters
            updateSensors();
            return view;
		}
    }

    public void updateSensors() {
        SensorsValueUpdater updater = new SensorsValueUpdater(adapter, layoutProgress,
                layoutNoConnection, getActivity().getApplicationContext(), sensorList);
        updater.execute();
        adapter = new SensorAdapter(getActivity(), R.layout.sensor_list_row, sensorList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void update() {
        updateSensors();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
