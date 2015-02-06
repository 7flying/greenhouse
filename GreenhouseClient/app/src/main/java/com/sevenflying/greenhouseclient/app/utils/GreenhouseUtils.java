package com.sevenflying.greenhouseclient.app.utils;

import android.content.Context;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.SensorType;

import java.text.DecimalFormat;

/** General Greenhouse utils
 * Created by 7flying on 20/07/2014.
 */
public class GreenhouseUtils {

    private Context reference;

    /** Removes unimportant zeroes.
     * @param toFormat - number to format
     * @return formatted string
     */
    public static String suppressZeros(double toFormat) {
        DecimalFormat format = new DecimalFormat("#.##");
        return format.format(toFormat);
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
                default:
                    return " ";
            }
        } else
            return "";
    }
}
