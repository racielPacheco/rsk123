package com.cinvestav.worktogether.services.devicediscovery;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import com.cinvestav.worktogether.global.Utilities;
import com.cinvestav.worktogether.services.updatedistribution.UpdateSettings;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import org.json.JSONObject;

/**
 *
 * @author
 */
public class DiscoveryPublishService extends AsyncTask<Void, Void, Boolean> { /* Params, Progress, Result */

    private final DiscoverySettings settings;
    private final UpdateSettings    updating_settings;
    private volatile boolean KEEP_THIS_SERVICE_RUNNING;

    public DiscoveryPublishService(DiscoverySettings param, UpdateSettings updating_settings){
        this.settings = param;
        this.updating_settings = updating_settings;
        KEEP_THIS_SERVICE_RUNNING = true;
    }

    @Override
    protected Boolean doInBackground(Void... args) {

        try {
            DatagramSocket socket;
            socket = new DatagramSocket( );
            socket.setBroadcast( true );
            JSONObject obj = new JSONObject( );
            obj.put("IP", Utilities.getIPAddress( true ));
            obj.put("UP", true);
            obj.put("TCP", updating_settings.UPDATE_DISTRIBUTION_TCP_PORT);
            obj.put("UDP", updating_settings.UPDATE_DISTRIBUTION_UDP_PORT);
            obj.put("DEVICE", getDeviceName());
            obj.put("TYPEDEVICE", "ANDROID");

            String data = obj.toString( );

            /*
                Propagar la IP del dispositivo indefinidamente.
             */
            while ( KEEP_THIS_SERVICE_RUNNING ) {
                DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), Utilities.getBroadcastAddress(), settings.DISCOVERY_SERVICES_PORT);
                socket.send(packet);

                try {
                    Thread.sleep(settings.PUBLISHING_DELAY);
                } catch (InterruptedException e) {
                    break;
                }
            }
            /*
                Avisar que el dispositivo abandonará la sesión.
             */
            obj.put("UP", false);
            data = obj.toString();
            Log.d( Utilities.TAG, "DISCOVERYPUBLISH --> THIS DEVICE IS LEAVING" );
            for ( int i = 0 ; i < settings.LEAVING_ALERT_MESSAGES ; i++ ) {
                DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), Utilities.getBroadcastAddress(), settings.DISCOVERY_SERVICES_PORT);
                socket.send(packet);

                try {
                    Thread.sleep(settings.LEAVING_DELAY);
                } catch (InterruptedException e) {
                    break;
                }
            }
            socket.close();
        } catch ( Exception ex ) {
            Log.d(Utilities.TAG, "DISCOVERYPUBLISH: " + ex.toString());
        }
        Log.d(Utilities.TAG, "PUBLISHING SERVICE STOPPED");
        return true;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model.replaceAll("\\s+","_"));
        } else {
            return capitalize(manufacturer) + "_" + model.replaceAll("\\s+","_");//return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public void stopService( ) {
        KEEP_THIS_SERVICE_RUNNING = false;
    }
}