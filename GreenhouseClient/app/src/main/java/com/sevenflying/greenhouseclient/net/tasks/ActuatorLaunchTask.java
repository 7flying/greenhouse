package com.sevenflying.greenhouseclient.net.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sevenflying.greenhouseclient.net.Commands;
import com.sevenflying.greenhouseclient.net.Communicator;
import com.sevenflying.greenhouseclient.net.Constants;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/** Requests a launch of an Actuator.
 * Created by flying on 25/02/15.
 */
public class ActuatorLaunchTask extends AsyncTask<String, Void, String> {

    private Communicator comm;
    private String host;
    private int serverPort;

    public ActuatorLaunchTask(Context context) {
        this.comm = new Communicator(context);
    }

    @Override
    protected void onPreExecute() {
        this.serverPort = comm.getServerPort();
        this.host = comm.getServer();
    }
    @Override
    protected String doInBackground(String... strings) {
        // needs the pinid
        if (strings.length != 1)
            return null;
        String ret = null;
        try {
            InetAddress add = InetAddress.getByName(host);
            Socket s = new Socket(add, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            oos.writeObject(Commands.LAUNCH);
            oos.flush();
            oos.writeObject(strings[0]);
            oos.flush();

            String response = (String) ois.readObject();
            ret = response.equals(Constants.OK) ? Constants.OK : response;
            s.close();
            oos.close();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
