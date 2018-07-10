package mx.cicese.biosignals;

import android.os.AsyncTask;

import android.widget.TextView;


import java.io.IOException;

import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends AsyncTask<Void, Void, Void> {

    String dstAddress, msg;
    int dstPort;
    String response = "";
    TextView textResponse;


    Client(String addr, int port, String msg) {
        dstAddress = addr;
        dstPort = port;
        this.msg = msg;
        //this.textResponse = textResponse;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(msg);


        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        //textResponse.setText(response);
        super.onPostExecute(result);
    }

}