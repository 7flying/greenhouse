package com.sevenflying.greenhouseclient.net;

import android.util.Base64;
import android.util.Log;

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

    /** Gets the sensor's last value from the server.
     * @param sensorPinId - sensor's pin id
     * @param sensorType - sensor's type
     * @return sensor's last value
     * @throws java.io.IOException
     * @throws ClassNotFoundException
     */
    public static double getLastValue(String sensorPinId, String sensorType)
            throws ClassNotFoundException, IOException
    {
        double lastValue = -3;
        try {
            InetAddress add = InetAddress.getByName(Constants.serverIP);
            Socket s = new Socket(add, Constants.serverPort);
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
        }catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return lastValue;
    }

    /** Requests a Sensor creation on the server.
     * @param name - sensor name
     * @param analogDig - sensor type (analog/digital)
     * @param pin - sensor's pin
     * @param type - sensor's type
     * @param refreshRate - sensor's refresh rate
     * @param isRefreshEnsured - whether the refresh rate has to be ensured
     * @return 0 if everything went right
     * @throws java.io.IOException
     * @throws ClassNotFoundException
     */
    public static int createSensor(String name, String analogDig, String pin, String type,
        String refreshRate, boolean isRefreshEnsured) throws IOException, ClassNotFoundException
    {
        Log.d("COMMUNICATOR", "At communicator");
        if(pin.length() == 1)
            pin = "0" + pin;
        String stringRefreshEnsured =  String.valueOf(isRefreshEnsured);
        String nameEncoded = new String(Base64.encode(name.getBytes(), Base64.DEFAULT));
        SensorCreationTask task = new SensorCreationTask();
        Integer ret = -1;
        try {
            ret = task.execute(nameEncoded, analogDig, pin, type, refreshRate, stringRefreshEnsured)
                    .get();
        } catch (Exception e) {
            ret = -1;
        }
        return ret;
    }

    /** Request the modification of a sensor
     * @param name - sensor name
     * @param analogDig - sensor type (analog/digital)
     * @param pin - sensor's pin
     * @param type - sensor's type
     * @param refreshRate - sensor's refresh rate
     * @param isRefreshEnsured - whether the refresh rate has to be ensured
     * @return 0 if everything went right
     */
    public static int modificateSensor(String name, String analogDig, String pin, String type,
        String refreshRate, boolean isRefreshEnsured)
    {
        if(pin.length() == 1)
            pin = "0" + pin;
        String stringRefreshEnsured =  String.valueOf(isRefreshEnsured);
        String nameEncoded = new String(Base64.encode(name.getBytes(), Base64.DEFAULT));
        Integer ret = -1;
        SensorModificationTask task = new SensorModificationTask();
        try {
            ret = task.execute(nameEncoded, analogDig, pin, type, refreshRate, stringRefreshEnsured)
                    .get();
        } catch (Exception e) {
            ret = -1;
        }
        return ret;
    }

    /** Deletes from the server a sensor given its pinId and type
     * @param pinID - sensor's pin id
     * @param type - - sensor's type
     * @return -1 if error
     */
    public static int deleteSensor(String pinID, String type) {
        SensorRemovalTask task = new SensorRemovalTask();
        Integer ret = -1;
        try {
           ret = task.execute(pinID, type).get();
        } catch (Exception e) {
            ret = -1;
        }
        return ret;
    }
}
