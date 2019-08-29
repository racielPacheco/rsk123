package com.cinvestav.worktogether.setup;

import android.os.Build;
import com.cinvestav.worktogether.global.SharedResources;
import com.cinvestav.worktogether.global.Utilities;
import com.cinvestav.worktogether.services.updatedistribution.UpdateSettings;
import com.cinvestav.worktogether.services.devicediscovery.DiscoverySettings;
import com.cinvestav.worktogether.services.state.StateSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by
 */
public class Configuration {

    private final UpdateSettings updating_settings;
    private final DiscoverySettings discovery_settings;
    private final StateSettings state_settings;
    // Coupling
    private Boolean accelerometer;
    private Boolean swipe;

    //

    Configuration(UpdateSettings updating_settings, DiscoverySettings discovery_settings, StateSettings state_settings){
        this.updating_settings = updating_settings;
        this.discovery_settings = discovery_settings;
        this.state_settings = state_settings;
        accelerometer = false;
        swipe         = true;
    }

    public void setupCoupling (Boolean setAccelerometer, Boolean setSwipe){
        accelerometer = setAccelerometer;
        swipe         = setSwipe;
    }


    public String[] getCoupling (){
        String[] coupling = new String[2];
        coupling[0] = this.accelerometer ? "TRUE" : "FALSE";
        coupling[1] = this.swipe ? "TRUE" : "FALSE";

        return coupling;
    }
    public Boolean getAccelerometer(){
        return this.accelerometer;
    }
    public Boolean getSwipe(){
        return this.swipe;
    }


    public List<HashMap<String, Object>> getConected(){
        List<HashMap<String, Object>>  connected = new ArrayList<>();

        // copy of CONNECTED_DEVICES()
        List<HashMap<String, String>> connected_copy = SharedResources.copy_CONNECTED_DEVICES();
        HashMap<String, String> map_connected = connected_copy.get(0);
        // copy of DISCOVERED_DEVICES()
        List<HashMap<String, Object>> discovery_copy = SharedResources.copy_DISCOVERED_DEVICES();

        for (Map.Entry<String,String> entry: map_connected.entrySet()){
            HashMap<String,Object> newData = new HashMap<>();
            String side = entry.getKey();
            String ip   = entry.getValue();//if there is no device ip = empty
            newData.put(side,ip);// ("TA1", "TA2", "TB1", "TB2", "TC1", "TC2") or ("PA1", "PB1", "PC1", "PC2"), IP

            for (HashMap<String,Object> newDataTwo : discovery_copy){
                if (newDataTwo.get("IP").toString().equals(ip)){
                    newData.put("TCP",newDataTwo.get("TCP").toString());
                    newData.put("UDP",newDataTwo.get("UDP").toString());
                    newData.put("DEVICE",newDataTwo.get("DEVICE").toString());
                    newData.put("TYPEDEVICE",newDataTwo.get("TYPEDEVICE").toString());
                }
            }//for
            if (ip.equals("")){
                newData.put("TCP","");
                newData.put("UDP","");
                newData.put("DEVICE","");
                newData.put("TYPEDEVICE","");
            }

            connected.add(newData);

        }//for

        return connected;
    }

    public HashMap<String, String> getOwnInfo(){
        HashMap<String, String> info = new HashMap<>();
        info.put("IP", Utilities.getIPAddress(true));
        info.put("TCP",Integer.toString(updating_settings.UPDATE_DISTRIBUTION_TCP_PORT));
        info.put("UDP",Integer.toString(updating_settings.UPDATE_DISTRIBUTION_UDP_PORT));
        info.put("NAME",getDeviceName());

        return info;
    }

    public HashMap<String, String> getInfoConfig(){
        HashMap<String, String> info = new HashMap<>();

        info.put("IP", Utilities.getIPAddress(true));
        info.put("TCP",Integer.toString(updating_settings.UPDATE_DISTRIBUTION_TCP_PORT));
        info.put("UDP",Integer.toString(updating_settings.UPDATE_DISTRIBUTION_UDP_PORT));
        info.put("STATUSTIME",state_settings.getDelay());
        info.put("STATUSDISCOVERYTIME",discovery_settings.getDelay());
        info.put("STATUSACCELEROMETER",accelerometer ? "ON" : "OFF");
        info.put("STATUSSWIPE",swipe ? "ON" : "OFF");
        return info;
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

}
