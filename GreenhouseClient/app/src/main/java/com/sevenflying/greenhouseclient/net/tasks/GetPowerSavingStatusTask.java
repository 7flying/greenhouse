package com.sevenflying.greenhouseclient.net.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sevenflying.greenhouseclient.net.Commands;
import com.sevenflying.greenhouseclient.net.Communicator;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/** Retrieves the power status of a sensor from the server.
 * Created by flying on 26/02/15.
 */
public class GetPowerSavingStatusTask extends AsyncTask<String, Void, String> {

    private Communicator comm;
    private String host;
    private int serverPort;

    public GetPowerSavingStatusTask(Context context) {
        this.comm = new Communicator(context);
    }

    @Override
    protected void onPreExecute() {
        this.serverPort = comm.getServerPort();
        this.host = comm.getServer();
    }

    @Override
    protected String doInBackground(String... params) {
        // needs pinid and type
        if (params.length != 2)
            return null;
        String ret = null;
        try {
            InetAddress add = InetAddress.getByName(host);
            Socket s = new Socket(add, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            oos.writeObject(Commands.POWSAV_STATUS);
            oos.flush();
            oos.writeObject(params[0] + ":" + params[1]);
            oos.flush();

            ret = (String) ois.readObject();
            s.close();
            oos.close();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
