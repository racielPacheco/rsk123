package com.cinvestav.worktogether.global;

import android.app.Activity;
import android.content.pm.FeatureInfo;
import android.os.Build;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Marco
 */
public class DeviceFeatures {
    public final HashMap<String, Object> deviceFeatures;
    //public final JSONObject deviceFeatures;

    public DeviceFeatures( Activity ctx ){
        deviceFeatures = new HashMap<String, Object>();
        //deviceFeatures = new JSONObject();
        setAllFeatures( ctx );
    }

    public final void setAllFeatures( Activity ctx ){
        //deviceFeatures.clear();

        /* Extracción de sensores, interfaces y funcionalidades */
        /*ArrayList<String> features  = new ArrayList<String>();
        FeatureInfo info[]          = ctx.getPackageManager().getSystemAvailableFeatures();
        for ( FeatureInfo item : info ){
            if( item.name != null )     features.add( item.name );
            else                        features.add( item.toString() );
        }

        deviceFeatures.put("FEATURES", features);
        */
        /* Determinar el tamaño y densidad de la pantalla */
        //int width   = ctx.getResources().getDisplayMetrics().widthPixels;
        //int height  = ctx.getResources().getDisplayMetrics().heightPixels;
        //int density = ctx.getResources().getDisplayMetrics().densityDpi;

        //deviceFeatures.put("WIDTH", width);
        //deviceFeatures.put("HEIGHT", height);
        //deviceFeatures.put("DENSITY", density);

        /* Agregar las últimas características del dispositivo y el SO */
        //deviceFeatures.put("MANUFACTURER", Build.MANUFACTURER);
        //deviceFeatures.put("MODEL", Build.MODEL);
        //deviceFeatures.put("SERIAL", Build.SERIAL);
        //deviceFeatures.put("SDK", Build.VERSION.SDK_INT);


        //deviceFeatures.put("DEVICE", "ANDROID");
        //deviceFeatures.put("MODEL", getDeviceName());



        //agregar estas por que las usare para acoplar el dispositivo
        /* Determinar el tamaño y densidad de la pantalla */
        int width   = ctx.getResources().getDisplayMetrics().widthPixels;
        int height  = ctx.getResources().getDisplayMetrics().heightPixels;
        //int density = ctx.getResources().getDisplayMetrics().densityDpi;

        deviceFeatures.put("DEVICE", "ANDROID");
        deviceFeatures.put("MODEL", getDeviceName());
        //deviceFeatures.put("SYSTEMVERSION", Build.VERSION.RELEASE);//android 4.02
        deviceFeatures.put("WIDTH", width);
        deviceFeatures.put("HEIGHT", height);

        if( Utilities.DEBUG ) {
            Log.d(Utilities.TAG, "************* Starting Framework WorkTogether *************");
            try{
                //Log.d(Utilities.TAG, "************* Size *************" + deviceFeatures.toString().getBytes("UTF-32").length + " bytes");
            } catch(Exception e){
                Log.d(Utilities.TAG, e.toString());
            }
        }
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
