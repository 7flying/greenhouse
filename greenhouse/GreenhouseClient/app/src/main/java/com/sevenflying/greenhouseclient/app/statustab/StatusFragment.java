package com.sevenflying.greenhouseclient.app.statustab;

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
import android.widget.ListView;
import android.widget.Toast;

import com.sevenflying.greenhouseclient.app.ActivityResultHandler;
import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.Updateable;
import com.sevenflying.greenhouseclient.app.database.DBManager;
import com.sevenflying.greenhouseclient.app.utils.Codes;
import com.sevenflying.greenhouseclient.domain.Actuator;
import com.sevenflying.greenhouseclient.domain.MonitoringItem;

import java.util.ArrayList;
import java.util.List;

/** This fragment shows the status of the items that are monitored
 *  and the actuators that can be applied.
 * Created by 7flying on 25/06/2014.
 */
public class StatusFragment extends Fragment implements Updateable {

    private List<MonitoringItem> monitoringItems;
    private DBManager manager;
    private ActuatorAdapter actuatorAdapter;
    private MonitoringItemAdapter moniAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        if(container == null)
            return null;
        else {
            View view =  inflater.inflate(R.layout.fragment_status, container, false);
            ListView actuatorList = (ListView) view.findViewById(R.id.list_actuators);
            final ListView moniList = (ListView) view.findViewById(R.id.list_items);

            ArrayList<Actuator> tempActuator = new ArrayList<Actuator>();
            for(int i = 0 ; i < 3; i++){
                Actuator a = new Actuator("Water pump " + i, "D0" + i);
                tempActuator.add(a);
            }
             actuatorAdapter= new ActuatorAdapter(getActivity(),
                    R.layout.actuator_row, tempActuator);
            actuatorList.setAdapter(actuatorAdapter);

            monitoringItems = new ArrayList<MonitoringItem>();
            manager = new DBManager(getActivity().getApplicationContext());
            monitoringItems = manager.getItems();
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
            moniList.setLongClickable(true);
            moniList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view,
                                               final int listPosition, long l)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getResources().getString(R.string.item))
                            .setItems(R.array.edit_delete_array, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int position) {
                                    switch (position) {
                                        case 0: // Edit
                                            Intent intent = new Intent(StatusFragment.this.getActivity(),
                                                    MoniItemCreationActivity.class);
                                            MonitoringItem extraInput = monitoringItems
                                                    .get(listPosition);
                                            intent.putExtra("moni-to-edit", extraInput);
                                            startActivityForResult(intent, Codes.CODE_EDIT_MONI_ITEM);
                                            break;
                                        case 1: // Delete
                                            manager.deleteItem(monitoringItems.get(listPosition));
                                            monitoringItems.remove(monitoringItems.get(listPosition));
                                            moniAdapter.notifyDataSetChanged();
                                            Toast.makeText(getActivity().getApplicationContext(),
                                                    getResources().getString(R.string.item_deleted),
                                                    Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return false;
                }
            });
            return view;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Codes.CODE_NEW_MONI_ITEM) {
            // Callback from MoniItemCreationActivity
            if(resultCode == Activity.RESULT_OK) {
                ActivityResultHandler.handleCreateNewMoniItem(getActivity().getApplicationContext(),
                        data);
                actuatorAdapter.notifyDataSetChanged();
            }
        } else {
            if(requestCode == Codes.CODE_EDIT_MONI_ITEM) {
                if(resultCode == Activity.RESULT_OK) {
                    // TODO
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        actuatorAdapter.notifyDataSetChanged();
        moniAdapter.notifyDataSetChanged();
    }

    @Override
    public void update() {
        monitoringItems = manager.getItems();
        moniAdapter.notifyDataSetChanged();
        // actuatorList = managet.getActuators();
        actuatorAdapter.notifyDataSetChanged();
    }
}
