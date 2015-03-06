package com.sevenflying.greenhouseclient.app.alertstab;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.sevenflying.greenhouseclient.database.DBManager;
import com.sevenflying.greenhouseclient.app.utils.Codes;
import com.sevenflying.greenhouseclient.app.utils.Extras;
import com.sevenflying.greenhouseclient.domain.Alert;
import com.sevenflying.greenhouseclient.domain.BootReceiver;

import java.util.List;


/** Shows a list of Alerts
 * Created by 7flying on 25/06/2014.
 */
public class AlertListFragment extends Fragment implements Updateable {

    private List<Alert> alertList;
    private AlertAdapter adapter;
    private LinearLayout layoutNoAlerts;
    private ListView listView;
    private DBManager manager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundle) {
        if(container == null)
            return null;
        else{
            setHasOptionsMenu(true);
            setMenuVisibility(true);

            View view =  inflater.inflate(R.layout.fragment_alert_list, container, false);
            manager = new DBManager(getActivity().getApplicationContext());
            listView = (ListView) view.findViewById(R.id.alertsListView);
            layoutNoAlerts = (LinearLayout) view.findViewById(R.id.layout_no_alerts);
            // Add previously created alerts if any
            alertList = manager.getAlerts();
            adapter = new AlertAdapter(getActivity(), R.layout.alert_list_row, alertList);
            // To prevent weird "java.lang.IllegalArgumentException: Can't have a viewTypeCount < 1"
            if(alertList.size() > 0)
                listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            checkLayoutVisibility();
            // Context menu stuff
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> adapterView, View view,
                                               final int listPosition, long l)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getResources().getString(R.string.string_alert))
                            .setItems(R.array.edit_delete_array,
                                    new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialogInterface, int position) {
                                    switch (position) {
                                        case 0: // Edit
                                            Intent intent = new Intent(AlertListFragment.this
                                                    .getActivity(), AlertCreationActivity.class);
                                            intent.putExtra(Extras.EXTRA_ALERT_EDIT,
                                                    alertList.get(listPosition));
                                            getActivity().startActivityForResult(intent,
                                                    Codes.CODE_EDIT_ALERT);
                                            break;
                                        case 1: // Delete
                                            manager.removeAlert(alertList.get(listPosition));
                                            alertList.remove(alertList.get(listPosition));
                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(getActivity().getBaseContext(),
                                                    getResources().getString(R.string.alert_deleted),
                                                    Toast.LENGTH_SHORT).show();
                                            if (alertList.size() == 0) {
                                                // Disable the notification when the device boots
                                                ComponentName receiver = new ComponentName(
                                                        getActivity().getApplicationContext(),
                                                        BootReceiver.class);
                                                PackageManager pm = getActivity()
                                                        .getApplicationContext()
                                                        .getPackageManager();

                                                pm.setComponentEnabledSetting(receiver,
                                                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                                        PackageManager.DONT_KILL_APP);
                                            }
                                            break;
                                    }
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

    public void checkLayoutVisibility() {
        if(alertList.size() > 0)
            layoutNoAlerts.setVisibility(View.GONE);
        else
            layoutNoAlerts.setVisibility(View.VISIBLE);
    }

    public void update() {
        manager = new DBManager(getActivity().getApplicationContext());
        alertList = manager.getAlerts();
        adapter = new AlertAdapter(getActivity(), R.layout.alert_list_row, alertList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        checkLayoutVisibility();
    }
}
