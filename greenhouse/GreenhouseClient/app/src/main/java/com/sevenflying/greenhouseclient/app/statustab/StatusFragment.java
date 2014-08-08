package com.sevenflying.greenhouseclient.app.statustab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.sevenflying.greenhouseclient.app.R;

/**
 * Created by 7flying on 25/06/2014.
 */
public class StatusFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        if(container == null)
            return null;
        else {
            View view =  inflater.inflate(R.layout.fragment_status, container, false);
            GridView gridView = (GridView) view.findViewById(R.id.grid_view);
            gridView.setAdapter(new ImageAdapter(getActivity().getApplicationContext()));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
                }
            });
            return view;
        }
    }
}
