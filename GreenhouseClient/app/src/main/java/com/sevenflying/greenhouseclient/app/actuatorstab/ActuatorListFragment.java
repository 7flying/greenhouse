package com.sevenflying.greenhouseclient.app.actuatorstab;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.Updateable;
import com.sevenflying.greenhouseclient.database.DBManager;
import com.sevenflying.greenhouseclient.app.utils.Codes;
import com.sevenflying.greenhouseclient.app.utils.Extras;
import com.sevenflying.greenhouseclient.domain.Actuator;
import com.sevenflying.greenhouseclient.net.Communicator;
import com.sevenflying.greenhouseclient.net.Constants;
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
    private Communicator comm;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, final Bundle savedInstanceState)
    {
        if (container == null)
            return null;
        else {
            comm = Communicator.getInstance(getActivity().getBaseContext());
            View view = inflater.inflate(R.layout.fragment_actuator_list, container, false);
            layoutNoActuators = (LinearLayout) view.findViewById(R.id.layout_no_actuators);
            layoutProgress = (LinearLayout) view.findViewById(R.id.linear_layout_progress);
            actuatorListView = (ListView) view.findViewById(R.id.list_actuators);
            manager = new DBManager(getActivity().getApplicationContext());
            actuatorList = manager.getAllActuators();
            actuatorAdapter = new ActuatorAdapter(getActivity(), R.layout.actuator_row,
                    actuatorList);
            actuatorListView.setAdapter(actuatorAdapter);
            actuatorAdapter.notifyDataSetChanged();
            // Click for further data display
            actuatorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Display actuator data
                    Intent intent = new Intent(ActuatorListFragment.this.getActivity(),
                            ActuatorStatusActivity.class);
                    intent.putExtra(Extras.EXTRA_ACTUATOR, actuatorList.get(position));
                    startActivity(intent);
                }
            });
            // Context menu on long click
            actuatorListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
                                               long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getResources().getString(R.string.actuator))
                        .setItems(R.array.edit_delete_array, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(Constants.DEBUGTAG, " $ ActuatorList  Click on: " + position +
                                      "\n\t list has: " + actuatorList.toString());
                                switch (which) {
                                    case 0: // edit
                                        Intent intent = new Intent(ActuatorListFragment.this
                                                .getActivity(), ActuatorCreationActivity.class);
                                        intent.putExtra(Extras.EXTRA_ACTUATOR_EDIT, actuatorList
                                                .get(position));
                                        getActivity().startActivityForResult(intent,
                                                Codes.CODE_EDIT_ACTUATOR);
                                        break;
                                    case 1: // delete
                                        if (comm.testConnection()) {
                                            String result = comm.deleteActuator(actuatorList
                                                    .get(position).getPinId());
                                            if (result.equals(Constants.OK)) {
                                                manager.deleteActuator(actuatorList.get(position));
                                                actuatorList.remove(position);
                                                actuatorAdapter.notifyDataSetChanged();
                                                Toast.makeText(
                                                        ActuatorListFragment.this.getActivity(),
                                                        getResources()
                                                          .getString(R.string.actuator_deleted),
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(
                                                        ActuatorListFragment.this.getActivity(),
                                                        getResources().getString(
                                                                R.string.actuator_error_delete),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        } else
                                            comm.showNoConnectionDialog(getActivity()
                                                    .getApplicationContext());
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
        if (comm.testConnection()) {
            ActuatorObtainerTask obtainerTask = new ActuatorObtainerTask(actuatorAdapter,
                    layoutProgress, getActivity().getApplicationContext(), actuatorList);
            obtainerTask.execute();
            // actuatorList = manager.getActuators();
            actuatorAdapter = new ActuatorAdapter(getActivity(),
                    R.layout.actuator_row, actuatorList);
            actuatorListView.setAdapter(actuatorAdapter);
            actuatorAdapter.notifyDataSetChanged();
        }
        checkVisibility();
    }
}
