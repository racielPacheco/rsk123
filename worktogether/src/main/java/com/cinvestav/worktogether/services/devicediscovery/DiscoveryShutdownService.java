package com.cinvestav.worktogether.services.devicediscovery;

import android.os.Handler;
import android.util.Log;
import com.cinvestav.worktogether.global.SharedResources;
import com.cinvestav.worktogether.global.Utilities;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author
 */
public class DiscoveryShutdownService {

    private final Handler handler;
    private final DiscoveryShutdownServiceRunnable runnable;
    private final DiscoverySettings settings;

    public DiscoveryShutdownService( DiscoverySettings settings ){
        handler         = new Handler();
        runnable        = new DiscoveryShutdownServiceRunnable();
        this.settings   = settings;
    }

    public void execute(){
        handler.removeCallbacks(runnable);
        handler.postDelayed( runnable, settings.SHUTDOWNSERVICE_CHECK_DELAY);
    }

    public void stopService() {
        handler.removeCallbacks( runnable );
    }

    private class DiscoveryShutdownServiceRunnable implements Runnable {

        public void run() {

            ArrayList<HashMap<String, Object>> UnavailableDevices = new ArrayList<HashMap<String, Object>>();

            List<HashMap<String, Object>> DiscoveredDevices_copy = SharedResources.copy_DISCOVERED_DEVICES();

            for( HashMap<String, Object> item : DiscoveredDevices_copy ){

                Socket socketChecker;
                try {
                    //socketChecker = new Socket(item.get("IP").toString(), Integer.valueOf(item.get("TCP").toString()));
                    socketChecker = new Socket();
                    SocketAddress socketAddress = new InetSocketAddress(item.get("IP").toString(), Integer.valueOf(item.get("TCP").toString()));
                    socketChecker.connect(socketAddress, 2000);

                    if( !socketChecker.isConnected() )
                        UnavailableDevices.add(item);
                    else {
                        Log.d(Utilities.TAG, item.get("IP").toString() + "...OK");
                    }
                    socketChecker.close();
                } catch (Exception e) {
                    Log.d(Utilities.TAG, item.get("IP").toString() + "..." + e.toString());
                    UnavailableDevices.add(item);
                }
            }

            for( HashMap<String, Object> item : UnavailableDevices ) {
                SharedResources.DISCOVERED_DEVICES.remove(item);
                Log.d(Utilities.TAG, "DISCOVERYSHUTDOWN --> " + item.get("IP").toString() + " REMOVED FROM THE DISCOVERY LIST");
                /* Eliminar el dispositivo de la lista de los acoplados, si es que existe. */
                Map<String, HashMap<String, Object>> grouplist_copy = SharedResources.copy_GROUP_LIST();
                for (Map.Entry<String, HashMap<String, Object>> user : grouplist_copy.entrySet()) {
                    String key = user.getKey();
                    HashMap<String, Object> obj = user.getValue();
                    if (key.equals(item.get("IP").toString() ) ) {
                        SharedResources.GROUP_LIST.remove( key );
                        Log.d(Utilities.TAG, "DISCOVERYSHUTDOWN --> " + key + " REMOVED FROM THE DISCOVERY COUPLING LIST");
                        // Si solo queda un elemento en el grupo, entonces debe ser el mismo dispositivo.
                        if ( SharedResources.GROUP_LIST.size() == 1 )
                            SharedResources.GROUP_LIST.clear();
                        //Log.d(Utilities.TAG, "NEW LIST FROM --> (GROUP_LIST): " + SharedResources.GROUP_LIST.toString());
                        break;
                    }
                }
            }
            handler.postDelayed(this, settings.SHUTDOWNSERVICE_CHECK_DELAY);
        }
    }
}