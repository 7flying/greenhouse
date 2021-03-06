package com.sevenflying.greenhouseclient.app.actuatorstab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.utils.ImageLoader;
import com.sevenflying.greenhouseclient.domain.Actuator;

/** ActuatorView class.
 * Created by 7flying on 11/08/2014.
 */
public class ActuatorView extends RelativeLayout {

    private TextView name;
    private ImageView icon;
    private Button launchButton;
    private static ImageLoader loader = null;

    public static ActuatorView inflate(ViewGroup parent) {
        if (loader == null)
            loader = new ImageLoader(parent.getContext());
        return  (ActuatorView) LayoutInflater
                .from(parent.getContext()).inflate(R.layout.actuator_view, parent, false);
    }

    public ActuatorView(Context context) {
        this(context, null);
    }

    public ActuatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActuatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.actuator_row, this, true);
        name = (TextView) findViewById(R.id.tv_actuator_name);
        icon = (ImageView) findViewById(R.id.image_actuator);
        launchButton = (Button) findViewById(R.id.button_launch);
    }

    public void setActuator(Actuator actuator) {
        name.setText(actuator.getName());
        loader.loadBitmapResource(actuator.getIcon(), icon);
    }

    public Button getLaunchButton() {
        return launchButton;
    }
}
