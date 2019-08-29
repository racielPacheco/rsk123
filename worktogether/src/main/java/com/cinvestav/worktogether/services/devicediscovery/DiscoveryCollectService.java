package com.cinvestav.worktogether.services.devicediscovery;

import android.os.AsyncTask;
import android.util.Log;

import com.cinvestav.worktogether.global.SharedResources;
import com.cinvestav.worktogether.global.Utilities;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author
 */
public class DiscoveryCollectService extends AsyncTask<Void, Void, Boolean> {

    private final DiscoverySettings discovery_settings;
    private volatile boolean KEEP_THIS_SERVICE_RUNNING;
    private final DiscoveryServiceListener listener;

    public DiscoveryCollectService(DiscoverySettings param, DiscoveryServiceListener listener) {
        discovery_settings = param;
        KEEP_THIS_SERVICE_RUNNING = true;
        this.listener = listener;
    }

    @Override
    @SuppressWarnings({"UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch"})
    //private void doInBackgroundX() {
    protected Boolean doInBackground(Void... params) {

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(discovery_settings.DISCOVERY_SERVICES_PORT);
            socket.setBroadcast(true);
            socket.setSoTimeout(discovery_settings.PUBLISHING_DELAY * 2);
            byte[] buf = new byte[discovery_settings.MESSAGE_SIZE];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            String receivedData;

            while( KEEP_THIS_SERVICE_RUNNING ) {
                try {
                    // Time out de PUBLISHING_DELAY x 2 milisegundos
                    socket.receive(packet);
                } catch (InterruptedIOException ex) {
                    Log.d(Utilities.TAG, "TIME OUT OCURRED ---> TRYING AGAIN IF POSSIBLE");
                    continue;
                }
                /*
                 * Si se recibieron datos, obtener la IP lista para agregarla a la lista de descubrimiento.
                 */
                receivedData = new String(buf);
                receivedData = receivedData.replaceAll("[^\\p{Print}]", "");

                JSONObject newDevice_JSON = new JSONObject(receivedData);

                HashMap<String, Object> newDevice = new HashMap<String, Object>();
                newDevice.put("IP", newDevice_JSON.get("IP").toString());
                newDevice.put("TCP", newDevice_JSON.get("TCP").toString());
                newDevice.put("UDP", newDevice_JSON.get("UDP").toString());
                newDevice.put("DEVICE", newDevice_JSON.get("DEVICE").toString());
                newDevice.put("TYPEDEVICE", newDevice_JSON.get("TYPEDEVICE").toString());

                if (!newDevice.get("IP").toString().equals( Utilities.getIPAddress(true)) ) {
                    if (newDevice_JSON.get("UP").toString().equals("true")) {
                        if ( SharedResources.DISCOVERED_DEVICES.indexOf(newDevice) == -1) {
                            SharedResources.DISCOVERED_DEVICES.add(newDevice);
                            Log.d(Utilities.TAG, "ADDING --> "+ newDevice_JSON.get("IP").toString()+":"+newDevice_JSON.get("TCP").toString());
                            //Informar que un dispositivo ha iniciado la aplicación y ha sido detectado.
                            listener.DiscoveryServiceDeviceJoined( newDevice );
                        }
                    } else {
                        // Se necesita una copia de las listas para iterar sobre ellas.
                        List<HashMap<String, Object>> discoveredDevices_copy = SharedResources.copy_DISCOVERED_DEVICES();
                        Map<String, HashMap<String, Object>> grouplist_copy = SharedResources.copy_GROUP_LIST();
                        List<HashMap<String, String>> connectedDevices_copy = SharedResources.copy_CONNECTED_DEVICES();
                        Map<String, HashMap<String, Object>> stateObjectsDevices_copy = SharedResources.copy_STATE_OBJECTS_DEVICES();
                        Map<String, HashMap<String, Object>> checkObjectsDevices_copy = SharedResources.copy_STATE_CHECK_DEVICES();

                        /*
                            Copia a enviar a los listener cuando un dispositivo abandona o abre la aplicación.
                            No enviamos newDevice porque cada lista guarda información distinta del dispositivo;
                            la lista de grupo guarda más información, es por ello que se tratan de copiar los datos del
                            dispositivo de dicha lista, de lo contrario solamente se envían los datos que contiene la
                            lista de descubrimiento.
                        */
                        HashMap<String, Object> copy = new HashMap<String, Object>();
                        boolean deviceWasOnDiscoveredDevicesList = false;
                        boolean deviceWasOnGroupList = false;


                        //Eliminar el dispositivo de la lista de descubrimiento, si es que existe.
                        for ( HashMap<String, Object> item : discoveredDevices_copy ) {
                            if ( item.get("IP").toString().equals( newDevice.get("IP").toString() ) ) {
                                SharedResources.DISCOVERED_DEVICES.remove( item );
                                Log.d(Utilities.TAG, newDevice.get("IP").toString() + "--> DELETING FROM DISCOVERY LIST");
                                copy = item;
                                deviceWasOnDiscoveredDevicesList = true;
                                break;
                            }
                        }

                        // Eliminar el dispositivo de la lista de los acoplados, si es que existe.

                        for (Map.Entry<String, HashMap<String, Object>> entry : grouplist_copy.entrySet()) {
                            String key = entry.getKey();
                            if( key.equals(newDevice.get("IP").toString()) ) {

                                SharedResources.GROUP_LIST.remove( key );
                                // Si solo queda un elemento en el grupo, entonces debe ser el mismo dispositivo.
                                if ( SharedResources.GROUP_LIST.size() == 1 )
                                    SharedResources.GROUP_LIST.clear();

                                HashMap<String, Object> me = new HashMap<String, Object>();
                                me.put("IP", key);
                                me.put("DEVICE", newDevice_JSON.get("DEVICE").toString());
                                me.put("TYPEDEVICE", newDevice_JSON.get("TYPEDEVICE").toString());
                                copy = me;
                                deviceWasOnGroupList = true;
                                break;
                            }
                        }

                        //Eliminar el dispositivo de la lista de dispositivos conectados, si es que existe.
                        HashMap<String, String> map_connected = connectedDevices_copy.get(0);
                        for (Map.Entry<String, String> entry : map_connected.entrySet()) {
                            if (entry.getValue().equals(newDevice.get("IP").toString())) {
                                listener.setColorDisonnected(entry.getKey());
                                map_connected.put(entry.getKey(), "");
                                SharedResources.CONNECTED_DEVICES.clear();
                                SharedResources.CONNECTED_DEVICES.add(map_connected);
                                break;
                            }
                        }

                        // Eliminar de Objetos Recibidos
                        for (Map.Entry<String, HashMap<String, Object>> entry : stateObjectsDevices_copy.entrySet()) {
                            String key = entry.getKey();
                            if( key.equals(newDevice.get("IP").toString()) ) {
                                SharedResources.STATE_OBJECTS_DEVICES.remove( key );
                                break;
                            }
                        }
                        // Eliminar de check state
                        for (Map.Entry<String, HashMap<String, Object>> entry : checkObjectsDevices_copy.entrySet()) {
                            String key = entry.getKey();
                            if( key.equals(newDevice.get("IP").toString()) ) {
                                SharedResources.STATE_CHECK_DEVICES.remove( key );
                                break;
                            }
                        }
                        /*
                            Informar que un dispositivo ha abandonado la aplicación
                        */
                        if( deviceWasOnDiscoveredDevicesList || deviceWasOnGroupList )
                            listener.DiscoveryServiceDeviceLeft( copy, deviceWasOnDiscoveredDevicesList, deviceWasOnGroupList );
                    }

                }
            }
        } catch (Exception e){
            Log.d(Utilities.TAG, "DISCOVERYCOLLECT: " + e.toString());
        }
        if (socket != null) {
            socket.close();
        }
        return true;
    }

    public void stopService() {
        KEEP_THIS_SERVICE_RUNNING = false;
        Log.d(Utilities.TAG, "COLLECTING SERVICE STOPPED");
    }
}