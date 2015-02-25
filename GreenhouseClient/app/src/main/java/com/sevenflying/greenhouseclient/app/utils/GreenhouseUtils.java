package com.sevenflying.greenhouseclient.app.utils;

import android.content.Context;
import android.util.Log;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.domain.SensorType;
import com.sevenflying.greenhouseclient.net.Constants;

import java.text.DecimalFormat;

/** General Greenhouse utils
 * Created by 7flying on 20/07/2014.
 */
public class GreenhouseUtils {

    private Context reference;
    public static final int THIRTY_SECONDS = 30 * 1000;

    /** Removes unimportant zeroes.
     * @param toFormat - number to format
     * @return formatted string
     */
    public static String suppressZeros(double toFormat) {
        DecimalFormat format = new DecimalFormat("#.##");
        return format.format(toFormat);
    }

    /** Returns a formatted version of the sensor data:
     * "SensorName (sensorPin) - sensorType"
     * @param s - sensor to format
     * @return string
     */
    public String getFormattedSensor(Sensor s) {
       return s.getName() + " (" + s.getPinId() + ") - " + getI18nSensorType(s.getType());
    }

    public GreenhouseUtils(Context reference) {
        this.reference = reference;
    }

    public String getI18nSensorType(SensorType type) {
        if (reference != null) {
            switch (type) {
                case HUMIDITY:
                    return reference.getResources().getString(R.string.humidity);
                case TEMPERATURE:
                    return reference.getResources().getString(R.string.temperature);
                case LIGHT:
                    return reference.getResources().getString(R.string.light);
                case PRESSURE:
                    return reference.getResources().getString(R.string.pressure);
                case STEAM:
                    return reference.getResources().getString(R.string.steam);
                default:
                    return " ";
            }
        } else
            return "";
    }
}
