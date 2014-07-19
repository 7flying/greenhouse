package com.sevenflying.greenhouseclient.app.utils;

import java.text.DecimalFormat;

/** General Greenhouse utils
 * Created by 7flying on 20/07/2014.
 */
public class GreenhouseUtils {

    /** Removes unimportant zeroes.
     * @param toFormat - number to format
     * @return formated string
     */
    public static String suppressZeros(double toFormat) {
        DecimalFormat format = new DecimalFormat("#.##");
        return format.format(toFormat);
    }
}
