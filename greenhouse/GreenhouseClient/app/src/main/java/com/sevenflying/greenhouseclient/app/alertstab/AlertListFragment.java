package com.sevenflying.greenhouseclient.app.alertstab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
            View view =  inflater.inflate(R.layout.fragment_alert_list, container, false);
            manager = AlertManager.getInstance();
            listView = (ListView) view.findViewById(R.id.alertsListView);

            AlertAdapter adapter = new AlertAdapter(getActivity(), R.layout.alert_list_row,new ArrayList<Alert>());
            adapter.addAll(manager.getAlerts());
            listView.setAdapter(adapter);

           // adapter.notifyDataSetChanged();
            /*
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                    // TODO see if we can

                }
            });
            */
            return  view;
        }
    }


}
