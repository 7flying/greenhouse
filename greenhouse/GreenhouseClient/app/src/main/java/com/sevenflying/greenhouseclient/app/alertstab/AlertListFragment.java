package com.sevenflying.greenhouseclient.app.alertstab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundle) {
        if(container == null)
            return null;
        else{
            setHasOptionsMenu(true);
            setMenuVisibility(true);

            View view =  inflater.inflate(R.layout.fragment_alert_list, container, false);
            manager = AlertManager.getInstance(this.getActivity().getApplicationContext());
            listView = (ListView) view.findViewById(R.id.alertsListView);
            layoutNoAlerts= (LinearLayout) view.findViewById(R.id.layout_no_alerts);
            // Add previously created alerts if any
            alertList = manager.getAlerts();
            adapter = new AlertAdapter(getActivity(), R.layout.alert_list_row, alertList);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            checkLayoutVisibility();
            // TODO Delete action
            registerForContextMenu(listView);

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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_alert, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.edit_alert_context_menu:
                // go to edit
                return true;
            case R.id.delete_alert_context_menu:
                // TODO
                adapter.remove(adapter.getItem(info.position));
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
        }
    }
}
