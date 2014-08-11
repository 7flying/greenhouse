package com.sevenflying.greenhouseclient.app.statustab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.Actuator;
import com.sevenflying.greenhouseclient.domain.MonitoringItem;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.domain.SensorType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** This fragment shows the status of the items that are monitored
 *  and the actuators that can be applied.
 * Created by 7flying on 25/06/2014.
 */
public class StatusFragment extends Fragment {
    private ListView moniList;
    private ListView actuatorList;
    private List<MonitoringItem> monitoringItems;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        if(container == null)
            return null;
        else {
            View view =  inflater.inflate(R.layout.fragment_status, container, false);
            actuatorList = (ListView) view.findViewById(R.id.list_actuators);
            moniList = (ListView) view.findViewById(R.id.list_items);

            ArrayList<Actuator> tempActuator = new ArrayList<Actuator>();
            for(int i = 0 ; i < 3; i++){
                Actuator a = new Actuator("Water pump " + i, "D0" + i);
                tempActuator.add(a);
            }
            ActuatorAdapter actuatorAdapter = new ActuatorAdapter(getActivity(),
                    R.layout.actuator_row, tempActuator);
            actuatorList.setAdapter(actuatorAdapter);

            monitoringItems = new ArrayList<MonitoringItem>();
            boolean enabled = true;
            for(int i = 0; i < 2; i++) {
                MonitoringItem item = new MonitoringItem("Greenhouse " + i );
                item.setWarningEnabled(enabled);
                item.addSensor(new Sensor("Sensor " + i,"A" + i, SensorType.LIGHT, 555, 45.5, "blabla"));
                item.addSensor(new Sensor("Sensor " + i + "i", "D" + i, SensorType.TEMPERATURE, 555, 45.7, "blabla"));
                monitoringItems.add(item);
                enabled = !enabled;
            }
            MonitoringItemAdapter adapter = new MonitoringItemAdapter(getActivity(),
                    R.layout.monitoring_item_row, monitoringItems);
            moniList.setAdapter(adapter);
            moniList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                // Open MonItemStatusActivity
                public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                    Intent intent = new Intent(StatusFragment.this.getActivity(),
                            MonItemStatusActivity.class);
                    intent.putExtra("moni-item", monitoringItems.get(index));
                    startActivity(intent);
                }
            });
            return view;
        }
    }
}
