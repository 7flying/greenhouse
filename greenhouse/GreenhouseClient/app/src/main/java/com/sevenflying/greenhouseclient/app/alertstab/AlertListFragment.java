package com.sevenflying.greenhouseclient.app.alertstab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.Alert;
import com.sevenflying.greenhouseclient.domain.AlertManager;

import java.util.List;


/** Shows a list of Alerts
 * Created by 7flying on 25/06/2014.
 */
public class AlertListFragment extends Fragment {

    private List<Alert> alertList;
    private AlertAdapter adapter;
    private LinearLayout layoutNoAlerts;
    private ListView listView;
    private AlertManager manager;
    private static final int CODE_CREATE_NEW_ALERT = 1;
    private static final int CODE_EDIT_ALERT = 2;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundle) {
        if(container == null)
            return null;
        else{
            setHasOptionsMenu(true);
            setMenuVisibility(true);

            View view =  inflater.inflate(R.layout.fragment_alert_list, container, false);
            manager = AlertManager.getInstance(this.getActivity().getApplicationContext());
            listView = (ListView) view.findViewById(R.id.alertsListView);
            layoutNoAlerts = (LinearLayout) view.findViewById(R.id.layout_no_alerts);
            // Add previously created alerts if any
            alertList = manager.getAlerts();
            adapter = new AlertAdapter(getActivity(), R.layout.alert_list_row, alertList);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            checkLayoutVisibility();
            // Context menu stuff
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int listPosition, long l) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(adapter.getItem(listPosition).getSensorPinId())
                            .setItems(R.array.edit_delete_array, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int position) {
                                    switch (position) {
                                        case 0: // Edit
                                            Intent intent = new Intent(AlertListFragment.this
                                                    .getActivity(), AlertCreationActivity.class);
                                            intent.putExtra("alert-to-edit", alertList.get(listPosition));
                                            startActivityForResult(intent, CODE_EDIT_ALERT);
                                            break;
                                        case 1: // Delete
                                            manager.removeAlert(alertList.get(listPosition));
                                            alertList.remove(alertList.get(listPosition));
                                            adapter.notifyDataSetChanged();
                                            break;
                                    }
                                    manager.commit();
                                    checkLayoutVisibility();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });

            return  view;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        setMenuVisibility(true);
        inflater.inflate(R.menu.alert_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_new) {
            // Start new Activity, AlertCreationActivity
            Intent intent = new Intent(AlertListFragment.this.getActivity(), AlertCreationActivity.class);
            startActivityForResult(intent, CODE_CREATE_NEW_ALERT);
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    public void checkLayoutVisibility() {
        if(alertList.size() > 0)
            layoutNoAlerts.setVisibility(View.GONE);
        else
            layoutNoAlerts.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        checkLayoutVisibility();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode ==  CODE_CREATE_NEW_ALERT) {
            // Callback from AlertCreationActivity
            if(resultCode == Activity.RESULT_OK) {
                Alert a = (Alert) data.getSerializableExtra("alert");
                alertList.add(a);
                adapter.notifyDataSetChanged();
                checkLayoutVisibility();
                manager.addAlert(a);
                manager.commit();
            }
        } else {
            if(requestCode == CODE_EDIT_ALERT) {
                // Callback from AlertCreationActivity on Edit mode
                if(resultCode == Activity.RESULT_OK) {
                    Alert a = (Alert) data.getSerializableExtra("alert");
                    manager.removeAlert(a);
                    alertList.remove(a);
                    manager.addAlert(a);
                    alertList.add(a);
                    manager.commit();
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
