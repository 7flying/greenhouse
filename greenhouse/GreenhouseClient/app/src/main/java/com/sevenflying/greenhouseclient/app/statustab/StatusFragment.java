package com.sevenflying.greenhouseclient.app.statustab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.Actuator;
import com.sevenflying.greenhouseclient.domain.MoniItemManager;
import com.sevenflying.greenhouseclient.domain.MonitoringItem;

import java.util.ArrayList;
import java.util.List;

/** This fragment shows the status of the items that are monitored
 *  and the actuators that can be applied.
 * Created by 7flying on 25/06/2014.
 */
public class StatusFragment extends Fragment {

    private List<MonitoringItem> monitoringItems;
    private static final int CODE_NEW_MONI_ITEM = 1;
    private MoniItemManager moniManager;
    private ActuatorAdapter actuatorAdapter;
    private MonitoringItemAdapter moniAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        if(container == null)
            return null;
        else {
            View view =  inflater.inflate(R.layout.fragment_status, container, false);
            ListView actuatorList = (ListView) view.findViewById(R.id.list_actuators);
            ListView moniList = (ListView) view.findViewById(R.id.list_items);

            ArrayList<Actuator> tempActuator = new ArrayList<Actuator>();
            for(int i = 0 ; i < 3; i++){
                Actuator a = new Actuator("Water pump " + i, "D0" + i);
                tempActuator.add(a);
            }
             actuatorAdapter= new ActuatorAdapter(getActivity(),
                    R.layout.actuator_row, tempActuator);
            actuatorList.setAdapter(actuatorAdapter);

            monitoringItems = new ArrayList<MonitoringItem>();
            moniManager = MoniItemManager.getInstance(getActivity().getApplicationContext());
            monitoringItems = moniManager.getItems();
            /*
            boolean enabled = true;
            for(int i = 0; i < 5; i++) {
                MonitoringItem item = new MonitoringItem("Greenhouse " + i );
                item.setWarningEnabled(enabled);
                item.addSensor(new Sensor("Sensor " + i,"A" + i, SensorType.LIGHT, 555, 45.5, "blabla"));
                item.addSensor(new Sensor("Sensor " + i + "i", "D" + i, SensorType.TEMPERATURE, 555, 45.7, "blabla"));
                monitoringItems.add(item);
                enabled = !enabled;
            }
            */
            moniAdapter = new MonitoringItemAdapter(getActivity(),
                    R.layout.monitoring_item_row, monitoringItems);
            moniList.setAdapter(moniAdapter);
            // Listener on Monitoring Items' list
            moniList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                // Open MonItemStatusActivity
                public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                    Intent intent = new Intent(StatusFragment.this.getActivity(),
                            MonItemStatusActivity.class);
                    intent.putExtra("moni-item", monitoringItems.get(index));
                    startActivity(intent);
                }
            });
            // Listener on "add item" button
            Button buttonAdd = (Button) view.findViewById(R.id.button_add_item);
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(new Intent(StatusFragment.this.getActivity(),
                            MoniItemCreationActivity.class), CODE_NEW_MONI_ITEM);
                }
            });
            return view;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CODE_NEW_MONI_ITEM) {
            // Callback from MoniItemCreationActivity
            if(resultCode == Activity.RESULT_OK) {
                MonitoringItem item = (MonitoringItem) data.getExtras().getSerializable("moni-item");
                moniManager.addItem(item);
                moniManager.commit();
                monitoringItems.remove(item);
                monitoringItems.add(item);
                actuatorAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity().getApplicationContext(),
                        getString(R.string.item_created), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        actuatorAdapter.notifyDataSetChanged();
        moniAdapter.notifyDataSetChanged();
    }
}
