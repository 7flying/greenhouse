package com.sevenflying.greenhouseclient.app.statustab;

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
import com.sevenflying.greenhouseclient.database.DBManager;
import com.sevenflying.greenhouseclient.app.utils.Codes;
import com.sevenflying.greenhouseclient.domain.MonitoringItem;

import java.util.ArrayList;
import java.util.List;

/** This fragment shows the status of the items that are monitored.
 * Created by 7flying on 25/06/2014.
 */
public class StatusFragment extends Fragment implements Updateable {

    private List<MonitoringItem> monitoringItems;
    private DBManager manager;

    private MonitoringItemAdapter moniAdapter;
    private ListView moniList;

    private LinearLayout layoutNoItems;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        if(container == null)
            return null;
        else {
            View view =  inflater.inflate(R.layout.fragment_status, container, false);
            moniList = (ListView) view.findViewById(R.id.list_items);
            layoutNoItems = (LinearLayout) view.findViewById(R.id.layout_no_items);
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
                                            getActivity().startActivityForResult(intent, Codes.CODE_EDIT_MONI_ITEM);
                                            break;
                                        case 1: // Delete
                                            manager.deleteItem(monitoringItems.get(listPosition));
                                            monitoringItems.remove(monitoringItems.get(listPosition));
                                            moniAdapter.notifyDataSetChanged();
                                            Toast.makeText(getActivity().getBaseContext(),
                                                    getResources().getString(R.string.item_deleted),
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
            checkVisibility();
            return view;
        }
    }

    private void checkVisibility() {
        if (monitoringItems.size() > 0)
            layoutNoItems.setVisibility(View.GONE);
        else
            layoutNoItems.setVisibility(View.VISIBLE);
    }

    @Override
    public void update() {
        // Monitoring Items
        monitoringItems = manager.getItems();
        moniAdapter = new MonitoringItemAdapter(getActivity(), R.layout.monitoring_item_row,
                monitoringItems);
        moniList.setAdapter(moniAdapter);
        moniAdapter.notifyDataSetChanged();
        checkVisibility();
    }
}
