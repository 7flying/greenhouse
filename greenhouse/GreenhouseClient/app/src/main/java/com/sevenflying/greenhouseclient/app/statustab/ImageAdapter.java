package com.sevenflying.greenhouseclient.app.statustab;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.sevenflying.greenhouseclient.app.R;

/**
 * Created by 7flying on 06/08/2014.
 */
public class ImageAdapter extends BaseAdapter {

    private Context context;
    private Integer[] mThumbIds = {
            R.drawable.sensor, R.drawable.light_sensor,
            R.drawable.humidity_sensor, R.drawable.temperature_sensor,
            R.drawable.ic_action_new, R.drawable.ic_action_read,
            R.drawable.ic_action_refresh, R.drawable.ic_launcher
    };

    public ImageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setBackgroundColor(Color.rgb(26, 106, 106));
        imageView.setImageResource(mThumbIds[position]);
        return imageView;

    }
}
