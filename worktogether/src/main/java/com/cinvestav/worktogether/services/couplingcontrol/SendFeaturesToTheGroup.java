package com.cinvestav.worktogether.services.couplingcontrol;

import android.app.Activity;
import android.util.Log;
import com.cinvestav.worktogether.global.DeviceFeatures;
import com.cinvestav.worktogether.global.SharedResources;
import com.cinvestav.worktogether.global.Utilities;
import com.cinvestav.worktogether.services.updatedistribution.networkclasses.Net_DeviceFeatures;
import com.cinvestav.worktogether.services.updatedistribution.UpdateDistributionService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author
 */
public class SendFeaturesToTheGroup implements Runnable{

    private final Activity act;
    private final DeviceFeatures features;

    public SendFeaturesToTheGroup( Activity act, DeviceFeatures Features ){
        this.act        = act;
        this.features   = Features;
    }

    public void run() {
        Utilities.enableScreenRotation(act, false);
        try{
            String ownIP = Utilities.getIPAddress(true);
            //Log.d(Utilities.TAG, "::SendFeaturesToTheGroup::Actualizando...");
            String type = "Net_DeviceFeatures";
            JSONObject objx = new JSONObject( );
            objx.put("@type", type);
            objx.put("deviceFeatures", features.deviceFeatures.toString());
            objx.put("IP", Utilities.getIPAddress(true));
            UpdateDistributionService.spreadObject(objx.toString(), "TCP");
            //UpdateDistributionService.spreadObject(new Net_DeviceFeatures( features.deviceFeatures, Utilities.getIPAddress(true) ), "TCP");

            Map<String, HashMap<String, Object>> grouplist_copy = SharedResources.copy_GROUP_LIST();

            for (Map.Entry<String, HashMap<String, Object>> entry : grouplist_copy.entrySet()) {
                String key = entry.getKey();
                HashMap<String, Object> obj = entry.getValue();
                if( key.equals( ownIP ) ) {
                    int TCP = 0;
                    int UDP = 0;
                    for (String o : obj.keySet()) {
                        if (obj.get(o).equals("TCP")) {
                            TCP = (Integer)obj.get(o);
                        }
                        else if(obj.get(o).equals("UDP")){
                            UDP = (Integer)obj.get(o);
                        }
                    }
                    HashMap<String, Object> me = new HashMap<String, Object>();
                    me.put("TCP", String.valueOf( TCP ) );
                    me.put("UDP", String.valueOf( UDP ) );
                    me.put("SPECS", features.deviceFeatures.toString());
                    SharedResources.GROUP_LIST.remove( key );
                    SharedResources.GROUP_LIST.put(ownIP, me);
                    //Log.d(Utilities.TAG, "Item actualizado en la lista propia: " + me.toString());
                    break;
                }
            }
            //Log.d(Utilities.TAG, "::SendFeaturesToTheGroup::Actualización de features finalizado en todos los dispositivos");
        }catch( Exception e ){
            Log.d(Utilities.TAG, "::SendFeaturesToTheGroup::exception: " + e.toString());
        }

        Utilities.enableScreenRotation(act, true);
    }
}
