package com.sevenflying.greenhouseclient.app.alertstab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.Alert;
import com.sevenflying.greenhouseclient.domain.AlertManager;

import java.util.ArrayList;


/** Shows a list of Alerts
 * Created by 7flying on 25/06/2014.
 */
public class AlertListFragment extends Fragment {

    private ListView listView;
    private AlertManager manager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundle) {
        if(container == null)
            return null;
        else{
            setHasOptionsMenu(true);
            View view =  inflater.inflate(R.layout.fragment_alert_list, container, false);
            manager = AlertManager.getInstance(this.getActivity().getApplicationContext());
            listView = (ListView) view.findViewById(R.id.alertsListView);

            AlertAdapter adapter = new AlertAdapter(getActivity(), R.layout.alert_list_row, new ArrayList<Alert>());
            adapter.addAll(manager.getAlerts());
            listView.setAdapter(adapter);

            return  view;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.alert_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_new) {
            // Start new Activity, AlertCreationActivity
            startActivity(new Intent(AlertListFragment.this.getActivity(), AlertCreationActivity.class));

            return true;
        } else
            return super.onOptionsItemSelected(item);
    }
}
