package com.sevenflying.greenhouseclient.app.statustab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sevenflying.greenhouseclient.app.R;

import java.util.ArrayList;
import java.util.Arrays;

/** This fragment shows the status of the items that are monitored
 *  and the actuators that can be applied.
 * Created by 7flying on 25/06/2014.
 */
public class StatusFragment extends Fragment {
    private ListView lv1 = null;
    private ListView lv2 = null;
    private String s1[] = {"a", "b", "c", "a", "b", "c", "a", "b", "c"};

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        if(container == null)
            return null;
        else {
            View view =  inflater.inflate(R.layout.fragment_status, container, false);
            lv1 = (ListView) view.findViewById(R.id.list_actuators);
            lv2 = (ListView) view.findViewById(R.id.list_items);

            ArrayList<String> listOne = new ArrayList<String>();
            listOne.addAll(Arrays.asList(s1));
            ArrayAdapter<String> listAdapterOne = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, listOne);
            lv1.setAdapter(listAdapterOne);

            ArrayList<String> listTwo = new ArrayList<String>();
            listTwo.addAll(Arrays.asList(s1));
            ArrayAdapter<String> listAdapterTwo = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, listTwo);
            lv2.setAdapter(listAdapterTwo);

            return view;
        }
    }
}
