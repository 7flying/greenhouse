package com.sevenflying.greenhouseclient.net;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.sevenflying.greenhouseclient.app.MainActivity;
import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.settings.SettingsFragment;
import com.sevenflying.greenhouseclient.net.tasks.ActuatorCreationTask;
import com.sevenflying.greenhouseclient.net.tasks.ActuatorLaunchTask;
import com.sevenflying.greenhouseclient.net.tasks.ActuatorModificationTask;
import com.sevenflying.greenhouseclient.net.tasks.ActuatorRemovalTask;
import com.sevenflying.greenhouseclient.net.tasks.SensorCreationTask;
import com.sevenflying.greenhouseclient.net.tasks.SensorModificationTask;
import com.sevenflying.greenhouseclient.net.tasks.SensorRemovalTask;
import com.sevenflying.greenhouseclient.net.tasks.TestConnectionTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/** Handles communication with the server.
 * Created by 7flying on 11/08/2014.
 */
public class Communicator {

    private SharedPreferences prefs;
    private Context context;
    private static String server = null;
    private static int port = -1;
    public static boolean connectionOk = false;
    public static long lastConnectionCheck = 0l;
    private static Communicator instance = null;


    private Communicator(Context context) {
        this.context = context;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    public static Communicator getInstance(Context context) {
        if (instance == null)
            instance = new Communicator(context);
        return  instance;
    }

    public String getServer() {
        if (server == null)
            server = prefs.getString(SettingsFragment.PREF_SERVER_IP, "192.168.1.57");
        // Log.d(Constants.DEBUGTAG, "$ Communicator - ip: " + ip);
        return server;
    }

    public int getServerPort() {
        if (port == -1)
            port = Integer.valueOf(prefs.getString(SettingsFragment.PREF_SERVER_PORT, "5432"));
        // Log.d(Constants.DEBUGTAG, "$ Communicator - port: " + port);
        return port;
    }

    // -- General --

    /**
     * Test the connection to the server.
     * If the last check has been done less than a minute ago the previous value
     * is returned.
     * @return true if the connection is fine, false otherwise
     */
    public boolean testConnection() {
        if (System.currentTimeMillis() - lastConnectionCheck > 60000) {
            TestConnectionTask task = new TestConnectionTask(context);
            boolean ret = true;
            try {
                ret = task.execute().get();
            } catch (Exception e) {
                ret = false;
            }
            connectionOk = ret;
            lastConnectionCheck = System.currentTimeMillis();
            Log.d(Constants.DEBUGTAG, "$ Communicator testConnection: UPDATE VALUE: "
                + ret);
            return ret;
        } else {
            Log.d(Constants.DEBUGTAG, "$ Communicator testConnection: CACHED VALUE: "
                    + connectionOk);
            return connectionOk;
        }
    }

    /** Shows a no-connection dialog
     */
    public void showNoConnectionDialog(Context currentContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
        builder.setMessage(currentContext.getResources().getString(R.string.alert_no_server_conn));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {}});
        builder.show();
    }

    // -- Sensors --

    /**
     * Gets the sensor's last value from the server.
     *
     * @param sensorPinId - sensor's pin id
     * @param sensorType  - sensor's type
     * @return sensor's last value
     * @throws java.io.IOException
     * @throws ClassNotFoundException
     */
    public double getLastValue(String sensorPinId, String sensorType)
            throws ClassNotFoundException, IOException {
        double lastValue = -3;
        try {
            // InetAddress add = InetAddress.getByName(Constants.serverIP);
            // Socket s = new Socket(add, Constants.serverPort);
            InetAddress add = InetAddress.getByName(getServer());
            Socket s = new Socket(add, getServerPort());
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            oos.writeObject(Commands.CHECK);
            oos.flush();
            oos.writeObject(sensorPinId + ":" + sensorType);
            oos.flush();
            lastValue = Double.valueOf(new String(
                    Base64.decode(((String) ois.readObject()).getBytes(), Base64.DEFAULT)));
            s.close();
            oos.close();
            ois.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return lastValue;
    }

    /**
     * Requests a Sensor creation on the server.
     *
     * @param name             - sensor name
     * @param analogDig        - sensor type (analog/digital)
     * @param pin              - sensor's pin
     * @param type             - sensor's type
     * @param refreshRate      - sensor's refresh rate
     * @param isRefreshEnsured - whether the refresh rate has to be ensured
     * @return status code
     * @throws java.io.IOException
     * @throws ClassNotFoundException
     */
    public String createSensor(String name, String analogDig, String pin, String type,
                               String refreshRate, boolean isRefreshEnsured) throws IOException, ClassNotFoundException {
        Log.d("COMMUNICATOR", "At communicator");
        if (pin.length() == 1)
            pin = "0" + pin;
        String stringRefreshEnsured = String.valueOf(isRefreshEnsured);
        String nameEncoded = new String(Base64.encode(name.getBytes(), Base64.DEFAULT));
        SensorCreationTask task = new SensorCreationTask(context);
        String ret = null;
        try {
            ret = task.execute(nameEncoded, analogDig, pin, type, refreshRate, stringRefreshEnsured)
                    .get();
        } catch (Exception e) {
            ret = null;
        }
        return ret;
    }

    /**
     * Request the modification of a sensor
     *
     * @param name             - sensor name
     * @param analogDig        - sensor type (analog/digital)
     * @param pin              - sensor's pin
     * @param type             - sensor's type
     * @param refreshRate      - sensor's refresh rate
     * @param isRefreshEnsured - whether the refresh rate has to be ensured
     * @return 0 if everything went right
     */
    public String editSensor(String name, String analogDig, String pin, String type,
                             String refreshRate, boolean isRefreshEnsured) {
        if (pin.length() == 1)
            pin = "0" + pin;
        String stringRefreshEnsured = String.valueOf(isRefreshEnsured);
        String nameEncoded = new String(Base64.encode(name.getBytes(), Base64.DEFAULT));
        String ret = null;
        SensorModificationTask task = new SensorModificationTask(context);
        try {
            ret = task.execute(nameEncoded, analogDig, pin, type, refreshRate, stringRefreshEnsured)
                    .get();
        } catch (Exception e) {
            ret = null;
        }
        return ret;
    }

    /**
     * Deletes from the server a sensor given its pinId and type
     *
     * @param pinID - sensor's pin id
     * @param type  - - sensor's type
     * @return -1 if error
     */
    public int deleteSensor(String pinID, String type) {
        SensorRemovalTask task = new SensorRemovalTask(context);
        Integer ret = -1;
        try {
            ret = task.execute(pinID, type).get();
        } catch (Exception e) {
            ret = -1;
        }
        return ret;
    }

    // -- Actuators --

    /**
     * Creates an actuator with a control sensor
     *
     * @param name         - actuator name
     * @param id           - actuator id
     * @param sensorType   - sensor's type
     * @param sensorId     - sensor id
     * @param compareType  - control type
     * @param compareValue - compare value
     * @return ok or error description
     */
    public String createActuator(String name, String id, String sensorType, String sensorId,
                                 String compareType, double compareValue) {
        ActuatorCreationTask task = new ActuatorCreationTask(context);
        String ret = null;
        try {
            ret = task.execute(new String(Base64.encode(name.getBytes(), Base64.DEFAULT)), id,
                    sensorType, sensorId, compareType, Double.toString(compareValue))
                    .get();
        } catch (Exception e) {
            ret = null;
        }
        return ret;
    }

    /**
     * Creates a simple actuator
     *
     * @param name - actuator name
     * @param id   - actuator id
     * @return ok or error description
     */
    public String createActuator(String name, String id) {
        ActuatorCreationTask task = new ActuatorCreationTask(context);
        String ret = null;
        try {
            ret = task.execute(new String(Base64.encode(name.getBytes(), Base64.DEFAULT)), id)
                    .get();
        } catch (Exception e) {
            ret = null;
        }
        return ret;
    }

    /**
     * Deletes an actuator
     *
     * @param id - - actuator id
     * @return ok or error description
     */
    public String deleteActuator(String id) {
        ActuatorRemovalTask task = new ActuatorRemovalTask(context);
        String ret = null;
        try {
            ret = task.execute(id).get();
        } catch (Exception e) {
            ret = null;
        }
        return ret;
    }

    /**
     * Modifies an actuator with a control sensor
     *
     * @param name         - actuator name
     * @param id           - actuator id
     * @param type         - actuator type
     * @param sensorId     - sensor id
     * @param compareType  - control type
     * @param compareValue - compare value
     * @return ok or error description
     */
    public String modifyActuator(String name, String id, String type, String sensorId,
                                 String compareType, double compareValue) {
        ActuatorModificationTask task = new ActuatorModificationTask(context);
        String ret = null;
        try {
            ret = task.execute(new String(Base64.encode(name.getBytes(), Base64.DEFAULT)), id, type,
                    sensorId, compareType, Double.toString(compareValue)).get();
        } catch (Exception e) {
            ret = null;
        }
        return ret;
    }

    /**
     * Modifies a simple actuator
     *
     * @param name - actuator name
     * @param id   - actuator id
     * @return ok or error description
     */
    public String modifyActuator(String name, String id) {
        ActuatorModificationTask task = new ActuatorModificationTask(context);
        String ret = null;
        try {
            ret = task.execute(new String(Base64.encode(name.getBytes(), Base64.DEFAULT)), id)
                    .get();
        } catch (Exception e) {
            ret = null;
        }
        return ret;
    }

    /**
     * Launches an actuator
     * @param pinid - actuator to launch
     */
    public void launchActuator(String pinid) {
        ActuatorLaunchTask task = new ActuatorLaunchTask(context);
        String ret = null;
        try {
            ret = task.execute(pinid).get();
        } catch (Exception e) {
            ret = null;
        }
        if (ret == null) {
            Toast.makeText(context, context.getResources().getString(
                            R.string.actuator_launch_error),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.actuator_launched),
                    Toast.LENGTH_SHORT).show();
        }
    }
}