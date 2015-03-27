package com.sevenflying.greenhouseclient.app.statustab;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.utils.ImageLoader;
import com.sevenflying.greenhouseclient.domain.MonitoringItem;


/** MonitoringItemView class.
 * Created by 7flying on 10/08/2014.
 */
public class MonitoringItemView extends RelativeLayout {

    private TextView name;
    private ImageView icon, warning;
    private static ImageLoader imageLoader;

    public static MonitoringItemView inflate(ViewGroup parent) {
        if (imageLoader == null)
            imageLoader = new ImageLoader(parent.getContext());
        return  (MonitoringItemView) LayoutInflater
                .from(parent.getContext()).inflate(R.layout.monitoring_item_view, parent, false);
    }

    public MonitoringItemView(Context context) {
        this(context, null);
    }

    public MonitoringItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonitoringItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.monitoring_item_row, this, true);
        name = (TextView) findViewById(R.id.tv_moni_name);
        icon = (ImageView) findViewById(R.id.image_monitoring);
        warning = (ImageView) findViewById(R.id.image_warning);
    }

    public void setMonitoringItem(MonitoringItem item) {
        name.setText(item.getName());
        // Set image icon or photo
        if(item.getPhotoPath() == null)
            imageLoader.loadBitmapResource(item.getIcon(), icon);
        else
            imageLoader.loadBitmapFile(item.getPhotoPath(), icon, item.getIcon());

        if(item.isWarningEnabled())
            warning.setImageResource(item.getWarningIcon());
        else
            warning.setImageDrawable(null);
    }
}
