package com.sevenflying.greenhouseclient.net;

import android.util.Base64;

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
            lastValue = Double.valueOf(new String(Base64.decode(((String) ois.readObject()).getBytes(), Base64.DEFAULT)));
            s.close();
            oos.close();
            ois.close();
        }catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return lastValue;
    }
}