package com.cinvestav.worktogether.services.state;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.cinvestav.worktogether.global.SharedResources;
import com.cinvestav.worktogether.global.Utilities;
import com.cinvestav.worktogether.services.devicediscovery.DiscoverySettings;
import com.cinvestav.worktogether.services.updatedistribution.UpdateSettings;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by
 */
public class StatePublishService extends AsyncTask<Void, Void, Boolean> { /* Params, Progress, Result */

    private final StateSettings settings;
    //private final UpdateSettings updating_settings;
    private volatile boolean KEEP_THIS_SERVICE_RUNNING;

    public StatePublishService(StateSettings param){//}, UpdateSettings updating_settings){
        this.settings = param;
        //this.updating_settings = updating_settings;
        KEEP_THIS_SERVICE_RUNNING = true;
    }

    @Override
    protected Boolean doInBackground(Void... args) {

        try {

            while ( KEEP_THIS_SERVICE_RUNNING ) {

                List<HashMap<String, String>> connected_copy = SharedResources.copy_CONNECTED_DEVICES();
                HashMap<String, String> map_connected = connected_copy.get(0);

               // for (Map.Entry<String,String> entry : map_connected.entrySet()) {

                //      Log.d(Utilities.TAG, "Sending+++-- " + entry.getKey() + " : " + entry.getValue());
               // }

                //DateFormat dateFormat = new SimpleDateFormat("mm:ss");
                //Date date = new Date();
                //System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
                //Log.d("workTogether"," "+ dateFormat.format(date));

                for (Map.Entry<String,String> entry: map_connected.entrySet()){

                    if (!entry.getValue().equals("")) {// evalua si el lado esta vacio o no contiene ninguna ip
                        //Log.d(Utilities.TAG, "Sending -> Sate to " + entry.getValue() + ":" + settings.STATE_SERVICES_PORT);

                        Socket socket = new Socket(entry.getValue(), Integer.valueOf(settings.STATE_SERVICES_PORT));

                        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                        Date date = new Date();

                        JSONObject obj = new JSONObject( );
                        obj.put("IP", Utilities.getIPAddress(true));//status
                        obj.put("OBJECT", SharedResources.copy_STATE_OBJECTS());
                        obj.put("STATUS", dateFormat.format(date));
                        String data = obj.toString( );

                        PrintWriter outWriter = new PrintWriter(socket.getOutputStream(), true);
                        outWriter.println(data);// envia al servidor collected
                        socket.close();


                        try {
                            Thread.sleep(settings.PUBLISHING_DELAY_PER_DEVICE);
                            Log.d(Utilities.TAG, "Sending -> PAUSANDO X DISPOSITIVO...");
                        } catch (InterruptedException e) {
                            break;
                        }
                    }//if

                }//for


                try {
                    Thread.sleep(settings.PUBLISHING_DELAY);
                    Log.d(Utilities.TAG, "Sending -> STATE PAUSADO...");
                } catch (InterruptedException e) {
                    //break;
                    continue;
                }
            }

           // socket.close();
        } catch ( Exception ex ) {
            Log.d(Utilities.TAG, "PublisherSate: " + ex.toString());
            //continue;
        }
        Log.d(Utilities.TAG, "PublishingState service stopped...");
        return true;
    }

    public void stopService( ) {
        KEEP_THIS_SERVICE_RUNNING = false;
    }
}