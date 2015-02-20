package com.sevenflying.greenhouseclient.app.actuatorstab;

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

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.Updateable;
import com.sevenflying.greenhouseclient.app.database.DBManager;
import com.sevenflying.greenhouseclient.domain.Actuator;
import com.sevenflying.greenhouseclient.net.tasks.ActuatorObtainerTask;

import java.util.List;

/** Shows the actuator list.
 * Created by flying on 26/01/15.
 */
public class ActuatorListFragment extends Fragment implements Updateable {

    private List<Actuator> actuatorList;
    private ActuatorAdapter actuatorAdapter;
    private ListView actuatorListView;
    private DBManager manager;
    private LinearLayout layoutNoActuators, layoutProgress;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, final Bundle savedInstanceState)
    {
        if (container == null)
            return null;
        else {
            View view = inflater.inflate(R.layout.fragment_actuator_list, container, false);
            layoutNoActuators = (LinearLayout) view.findViewById(R.id.layout_no_actuators);
            layoutProgress = (LinearLayout) view.findViewById(R.id.linear_layout_progress);
            actuatorListView = (ListView) view.findViewById(R.id.list_actuators);
            manager = new DBManager(getActivity().getApplicationContext());
            actuatorList = manager.getAllActuators();
            actuatorAdapter = new ActuatorAdapter(getActivity(), R.layout.actuator_row, actuatorList);
            actuatorListView.setAdapter(actuatorAdapter);
            actuatorAdapter.notifyDataSetChanged();
            // Click for further data display
            actuatorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Display actuator data
                    Intent intent = new Intent(ActuatorListFragment.this.getActivity(),
                            ActuatorStatusActivity.class);
                    intent.putExtra("actuator", actuatorList.get(position));
                    startActivity(intent);
                }
            });
            // Context menu on long click
            actuatorListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getResources().getString(R.string.actuator))
                        .setItems(R.array.edit_delete_array, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: // edit
                                        // TODO edit actuator
                                        break;
                                    case 1: // delete
                                        manager.deleteActuator(actuatorList.get(position));
                                        actuatorList.remove(position);
                                        actuatorAdapter.notifyDataSetChanged();
                                        Toast.makeText(ActuatorListFragment.this.getActivity(),
                                                getResources().getString(R.string.actuator_deleted),
                                                Toast.LENGTH_SHORT).show();
                                        break;
                                }
                                checkVisibility();
                            }
                        });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });
            update();
            return view;
        }
    }

    private void checkVisibility() {
        if (actuatorList.size() > 0)
            layoutNoActuators.setVisibility(View.GONE);
        else
            layoutNoActuators.setVisibility(View.VISIBLE);
    }

    @Override
    public void update() {
        ActuatorObtainerTask obtainerTask = new ActuatorObtainerTask(actuatorAdapter, layoutProgress,
                getActivity().getApplicationContext(), actuatorList);
        obtainerTask.execute();
        // actuatorList = manager.getActuators();
        actuatorAdapter = new ActuatorAdapter(getActivity(), R.layout.actuator_row, actuatorList);
        actuatorListView.setAdapter(actuatorAdapter);
        actuatorAdapter.notifyDataSetChanged();
        checkVisibility();
    }
}
