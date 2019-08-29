package com.cinvestav.worktogether.services.state;

import android.util.Log;
import com.cinvestav.worktogether.global.SharedResources;
import com.cinvestav.worktogether.global.Utilities;
import com.cinvestav.worktogether.services.updatedistribution.CustomListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.lang.Math.abs;

/**
 * Class StateCheckService
 */
public class StateCheckService{ /* Params, Progress, Result */

    private final StateSettings settings;
    private final CustomListener customListener;
    //private final UpdateSettings updating_settings;
    private volatile boolean KEEP_THIS_SERVICE_RUNNING;

    public StateCheckService(CustomListener customListener, StateSettings param){//}, UpdateSettings updating_settings){
        this.customListener = customListener;
        this.settings = param;
        //this.updating_settings = updating_settings;
        KEEP_THIS_SERVICE_RUNNING = true;
    }

    public void startCheckService(){
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                checkStatus();
            }
        };
        Thread myThread = new Thread(myRunnable);
        myThread.start();

    }
    private void checkStatus(){

        try {

            while ( KEEP_THIS_SERVICE_RUNNING ) {

                Map<String,HashMap<String, Object>> copy_Object = SharedResources.copy_STATE_OBJECTS_DEVICES();
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date();
                //System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
                //Log.d("workTogether"," "+ dateFormat.format(date));
                for (Map.Entry<String, HashMap<String, Object>> entry : copy_Object.entrySet()) {

                    String key = entry.getKey();// IP
                    HashMap<String, Object> obj = entry.getValue();//STATUS

                    String getStatus = obj.get("STATUS").toString();
                    String[] objStatus = getStatus.split(":");
                    final int[] timeRecived = new int[objStatus.length];//array color rgb received
                    timeRecived[0] = Integer.parseInt(objStatus[0]);//HH
                    timeRecived[1] = Integer.parseInt(objStatus[1]);//mm
                    timeRecived[2] = Integer.parseInt(objStatus[2]);//ss

                    String getStatusOwn = dateFormat.format(date).toString();
                    String[] objStatusOwn = getStatusOwn.split(":");
                    final int[] timeOwn = new int[objStatusOwn.length];//array color rgb received
                    timeOwn[0] = Integer.parseInt(objStatusOwn[0]);//HH
                    timeOwn[1] = Integer.parseInt(objStatusOwn[1]);//mm
                    timeOwn[2] = Integer.parseInt(objStatusOwn[2]);//ss

                    int timeOneHour = timeRecived[0] * 60 * 60;
                    int timeOneMin = timeRecived[1] * 60;
                    int timeOne = timeOneHour + timeOneMin + timeRecived[2];

                    int timeTwoHour = timeOwn[0] * 60 * 60;
                    int timeTwoMin = timeOwn[1] * 60;
                    int timeTwo = timeTwoHour + timeTwoMin + timeOwn[2];

                    int timeEnd = timeTwo - timeOne;
                    timeEnd = abs(timeEnd);
                    Log.d(Utilities.TAG, "Tiempo::"+timeEnd);
                    if (timeEnd > 35){// 00:00:35
                        // si el tiempo es mayor notificar que se desconecto

                        Map<String,HashMap<String, Object>> copy_ObjectRestore = SharedResources.copy_STATE_OBJECTS_DEVICES();

                        HashMap<String, Object> objectTemp = copy_ObjectRestore.get(key);
                        //String getStatus = objectTemp.get("STATUS").toString();
                        String getObject = objectTemp.get("OBJECT").toString();
                        //Log.d("workTogether"," RESTAURAR-->"+ getObject);
                        customListener.setRestoreObjects(getObject , key);

                        List<HashMap<String, Object>> discoveredDevices_copy = SharedResources.copy_DISCOVERED_DEVICES();
                        Map<String, HashMap<String, Object>> grouplist_copy = SharedResources.copy_GROUP_LIST();
                        List<HashMap<String, String>> connectedDevices_copy = SharedResources.copy_CONNECTED_DEVICES();
                        Map<String, HashMap<String, Object>> stateObjectsDevices_copy = SharedResources.copy_STATE_OBJECTS_DEVICES();
                        Map<String, HashMap<String, Object>> checkObjectsDevices_copy = SharedResources.copy_STATE_CHECK_DEVICES();

                        HashMap<String, Object> copy = new HashMap<String, Object>();
                        boolean deviceWasOnDiscoveredDevicesList = false;
                        boolean deviceWasOnGroupList = false;

                        //Eliminar el dispositivo de la lista de descubrimiento, si es que existe.
                        for ( HashMap<String, Object> item : discoveredDevices_copy ) {
                            if ( item.get("IP").toString().equals( key ) ) {

                                SharedResources.DISCOVERED_DEVICES.remove( item );
                                Log.d(Utilities.TAG, key + "--> DELETING FROM DISCOVERY LIST");
                                copy = item;
                                deviceWasOnDiscoveredDevicesList = true;
                                break;
                            }
                        }

                        // Eliminar el dispositivo de la lista de los acoplados, si es que existe.

                        for (Map.Entry<String, HashMap<String, Object>> entryGroup : grouplist_copy.entrySet()) {
                            String keyGroup = entryGroup.getKey();
                            if( keyGroup.equals(keyGroup) ) {

                                SharedResources.GROUP_LIST.remove( keyGroup );
                                // Si solo queda un elemento en el grupo, entonces debe ser el mismo dispositivo.
                                if ( SharedResources.GROUP_LIST.size() == 1 )
                                    SharedResources.GROUP_LIST.clear();
                                    deviceWasOnGroupList = true;
                                    break;
                            }
                        }

                        //Eliminar el dispositivo de la lista de dispositivos conectados, si es que existe.
                        HashMap<String, String> map_connected = connectedDevices_copy.get(0);
                        for (Map.Entry<String, String> entryConn : map_connected.entrySet()) {
                            if (entryConn.getValue().equals(key)) {
                                customListener.setColorDisonnected(entryConn.getKey());
                                map_connected.put(entryConn.getKey(), "");
                                SharedResources.CONNECTED_DEVICES.clear();
                                SharedResources.CONNECTED_DEVICES.add(map_connected);
                                break;
                            }
                        }
                        // Eliminar de Objetos Recibidos
                        for (Map.Entry<String, HashMap<String, Object>> entryObject : stateObjectsDevices_copy.entrySet()) {
                            String keyIP = entryObject.getKey();
                            if( keyIP.equals(key) ) {
                                SharedResources.STATE_OBJECTS_DEVICES.remove( keyIP );
                                break;
                            }
                        }
                        // Eliminar de check state
                        for (Map.Entry<String, HashMap<String, Object>> entryObject : checkObjectsDevices_copy.entrySet()) {
                            String keyIP = entryObject.getKey();
                            if( keyIP.equals(key) ) {
                                SharedResources.STATE_CHECK_DEVICES.remove( keyIP );
                                break;
                            }
                        }

                        if( deviceWasOnDiscoveredDevicesList || deviceWasOnGroupList )
                            customListener.DiscoveryServiceDeviceLeft( copy, deviceWasOnDiscoveredDevicesList, deviceWasOnGroupList );
                    }


                    //avisar al custumlistener
                    // key(ip), Objectos

                    try {
                        Thread.sleep(settings.PUBLISHING_DELAY_PER_DEVICE);
                        //Log.d(Utilities.TAG, "Checando -> PAUSANDO X DISPOSITIVO...");
                    } catch (InterruptedException e) {
                        continue;
                    }

                }//for

                try {
                    Thread.sleep(settings.PUBLISHING_DELAY);
                    //Log.d(Utilities.TAG, "checando -> STATE PAUSADO...");
                } catch (InterruptedException e) {
                    break;
                }
            }//while
            // socket.close();
        } catch ( Exception ex ) {
            Log.d(Utilities.TAG, "PublisherSate: " + ex.toString());
        }
        Log.d(Utilities.TAG, "PublishingState service stopped...");
    }

    public void stopService( ) {
        KEEP_THIS_SERVICE_RUNNING = false;
    }
}