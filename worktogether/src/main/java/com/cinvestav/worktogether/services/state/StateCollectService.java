package com.cinvestav.worktogether.services.state;

import android.util.Log;
import com.cinvestav.worktogether.global.SharedResources;
import com.cinvestav.worktogether.global.Utilities;
import com.cinvestav.worktogether.services.updatedistribution.CustomListener;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by
 */
public class StateCollectService {

    private final StateSettings settings;
    private final CustomListener customListener;
    private boolean KEEP_THIS_SERVICE_RUNNING;


    public StateCollectService(CustomListener customListener,StateSettings settings_param) {
        this.customListener = customListener;
        this.settings = settings_param;
        KEEP_THIS_SERVICE_RUNNING = true;
    }

    public void startStateCollectService(){
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                collectService();
            }
        };
        Thread myThread = new Thread(myRunnable);
        myThread.start();
    }
    private void collectService() {
        try {

            String receivedData;
            ServerSocket welcomeSocket = new ServerSocket( settings.STATE_SERVICES_PORT );
            while (KEEP_THIS_SERVICE_RUNNING) {

                Socket clientSocket;
                try {
                    clientSocket = welcomeSocket.accept();
                } catch (InterruptedIOException ex) {
                    continue;
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));

                receivedData = in.readLine();
                //Log.d(Utilities.TAG, "ORIGINAL MESSAGE RECEIVED COLLECTED ---> " + receivedData);
                receivedData = receivedData.replaceAll("[^\\p{Print}]", "");

                JSONObject newData_JSON = new JSONObject(receivedData);

                String STATUS =newData_JSON.getString("STATUS");
                String OBJECT =newData_JSON.getString("OBJECT");
                String IP =newData_JSON.getString("IP");

                HashMap<String, Object> meObject = new HashMap<>();
                meObject.put("STATUS", new String(STATUS));
                meObject.put("OBJECT", new String(OBJECT));

                if( !SharedResources.STATE_OBJECTS_DEVICES.containsKey(IP) ) {// si no existe
                    SharedResources.STATE_OBJECTS_DEVICES.put(IP, meObject);//"OBJECT":"TRABAJO@210:222:179#HOLA@39:127:244"

                    //HashMap<String, Object> meObjectCheck = new HashMap<>();
                    //meObjectCheck.put("OLDSTATUS", dateFormat.format(date));
                    //meObjectCheck.put("STATUS", new String(STATUS));
                    SharedResources.STATE_CHECK_DEVICES.put(IP,meObject);
                }else {
                    //Map<String,HashMap<String, Object>> copy_Object = SharedResources.copy_STATE_OBJECTS_DEVICES();
                    //HashMap<String, Object> objectTemp = copy_Object.get(IP);
                    //String getStatus = objectTemp.get("STATUS").toString();
                    //String getObject = objectTemp.get("STATUS").toString();
                    //HashMap<String, Object> meObjectCheck = new HashMap<>();
                    //meObjectCheck.put("OLDSTATUS", getStatus);
                    //meObjectCheck.put("STATUS", new String(STATUS));
                    SharedResources.STATE_CHECK_DEVICES.put(IP,meObject);


                    SharedResources.STATE_OBJECTS_DEVICES.remove(IP);
                    SharedResources.STATE_OBJECTS_DEVICES.put(IP, meObject);
                }





                //Log.d(Utilities.TAG, "ORIGINAL MESSAGE RECEIVED COLLECTED ---> " + newData_JSON.toString());

                //Log.d(Utilities.TAG, "ORIGINAL MESSAGE RECEIVED COLLECTED23 ---> " + SharedResources.copy_STATE_OBJECTS_DEVICES());


            }






    } catch ( Exception ex ) {
        Log.d(Utilities.TAG, "CollectSate: " + ex.toString());

    }
    Log.d(Utilities.TAG, "CollectSate service stopped...");
    //return true;
    }



    public void stopService( ) {
        KEEP_THIS_SERVICE_RUNNING = false;
    }
}
