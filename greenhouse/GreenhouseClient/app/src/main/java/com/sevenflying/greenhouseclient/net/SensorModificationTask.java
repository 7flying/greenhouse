package com.sevenflying.greenhouseclient.net;

import android.os.AsyncTask;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/** Requests to update the description of a sensor using a background task. PinID and type
 * Created by 7flying on 24/09/2014.
 */
public class SensorModificationTask extends AsyncTask<String, Void, Integer> {

    @Override
    protected Integer doInBackground(String... strings) {
        // String name : 0
        // String analogDig : 1
        // String pin: 2
        // String type: 3
        // String refreshRate: 4
        // String isRefreshEnsured: 5
        if(strings.length != 6)
            return -1;
        Integer ret = -1;
        try {
            InetAddress add = InetAddress.getByName(Constants.serverIP);
            Socket s = new Socket(add, Constants.serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            oos.writeObject(Commands.UPDATE);
            oos.flush();
            String send = strings[0] + ":" + strings[1] + strings[2] + ":" + strings[3] + ":" +
                    strings[4] + ":" + strings[5];
            oos.writeObject(send);
            oos.flush();
            String response = (String) ois.readObject();
            if(response.equals(Constants.OK))
                ret = 0;
            s.close();
            oos.close();
            ois.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
