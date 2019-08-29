package com.cinvestav.worktogether.services.updatedistribution.networkclasses;

import java.util.HashMap;

/**
 *
 * @author
 */
public class Net_DeviceFeatures {

    public HashMap<String, Object> deviceFeatures;
    //public JSONObject deviceFeatures;
    public String           IP;

    public Net_DeviceFeatures( HashMap<String, Object> deviceFeatures, String IP ){

        /*String df = deviceFeaturesx.toString();
        JSONObject deviceFeatures_JSON = new JSONObject(df);
        HashMap<String, Object> hmp = new HashMap<String, Object>();
        //hmp.put("WIDTH",  Integer.parseInt(deviceFeatures_JSON.get("WIDTH").toString()));
        //hmp.put("HEIGHT", Integer.parseInt(deviceFeatures_JSON.get("HEIGHT").toString()));
        hmp.put("DEVICE", deviceFeatures_JSON.get("DEVICE").toString());
        hmp.put("MODEL",  deviceFeatures_JSON.get("MODEL").toString());
        //ArrayList<HashMap<String, Object>> lhmp = new ArrayList<HashMap<String, Object>>();
        //lhmp.add(hmp);*/
        this.deviceFeatures     = (HashMap<String, Object>)deviceFeatures.clone();
        this.IP                 = IP;
    }

    /* ES INDISPENSABLE CONTAR CON EL CONSTRUCTOR POR DEFECTO PARA QUE ObjectOutputStream PERMITA SU ENVÍO A TRAVÉS DE LA RED */
    public Net_DeviceFeatures() {
        this.deviceFeatures = new HashMap<String, Object>();
    }
}
