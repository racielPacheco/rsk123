package com.cinvestav.worktogether.global;

import com.cinvestav.worktogether.setup.WorkTogetherSetup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by erik reyes on 6/24/16.
 */
public abstract class SharedResources {
    public static volatile String STATE_OBJECTS;// para el servicio state
    public static volatile Map<String, HashMap<String, Object>> STATE_OBJECTS_DEVICES;// para el servicio state
    public static volatile Map<String, HashMap<String, Object>> STATE_CHECK_DEVICES;// para el servicio state
    public static volatile List<Object> WORKSPACE;
    /*
        Las listas pueden ser accedidas como se haría para cualquier lista, sin embargo, para realizar iteraciones
        sobre las mismas (e.g. for each) se deben llamar las funciones copy_DISCOVERED_DEVICES y copy_GROUP_LIST
        para iterar sobre las copias de dichas listas, ya que esa operación no es "thread safe".
    */
    public static volatile List<HashMap<String, Object>>   DISCOVERED_DEVICES;
    public static volatile Map<String, HashMap<String, Object>>   GROUP_LIST;
    public static volatile List<HashMap<String, String>>  CONNECTED_DEVICES;//tiene los dispositivos conectados up,down,left,right
    /*
        Para los servicios SendPairingRequest y ListenPairingRequest
        Este seguro se debe revisar antes de ejecutar los servicios de acoplamiento ^
        Si ya existe un intento de acoplamiento en progreso, De ser así, esperar a que
        dicho intento finalice (CouplingSettings.COUPLING_WAITING_TIME).
    */
    //public static volatile boolean  LOCKED_REQUESTS;
    public static volatile Boolean  ACT_AS_SERVER;
    public static volatile String SYSTEM_DATE;

    public static Map<String, HashMap<String, Object>> copy_GROUP_LIST(){
        Map<String, HashMap<String, Object>> newList;
        synchronized(GROUP_LIST){
            newList = new HashMap<String, HashMap<String, Object>>(GROUP_LIST);
        }
        return newList;
    }

    public static List<HashMap<String, Object>> copy_DISCOVERED_DEVICES(){
        List<HashMap<String, Object>> newList;
        synchronized(DISCOVERED_DEVICES){
            newList = new ArrayList<HashMap<String, Object>>(DISCOVERED_DEVICES);
        }
        return newList;
    }

    public static List<HashMap<String, String>> copy_CONNECTED_DEVICES(){
        List<HashMap<String, String>> newList;
        synchronized(CONNECTED_DEVICES){
            newList = new ArrayList<HashMap<String, String>>(CONNECTED_DEVICES);
        }
        return newList;
    }

    public static String copy_STATE_OBJECTS(){
        String newList;
        synchronized(STATE_OBJECTS){
            newList = new String(STATE_OBJECTS);
        }
        return newList;
    }
    public static Map<String, HashMap<String, Object>> copy_STATE_OBJECTS_DEVICES(){
        Map<String, HashMap<String, Object>> newList;
        synchronized(STATE_OBJECTS_DEVICES){
            newList = new HashMap<String, HashMap<String, Object>>(STATE_OBJECTS_DEVICES);
        }
        return newList;
    }

    public static Map<String, HashMap<String, Object>> copy_STATE_CHECK_DEVICES(){
        Map<String, HashMap<String, Object>> newList;
        synchronized(STATE_CHECK_DEVICES){
            newList = new HashMap<String, HashMap<String, Object>>(STATE_CHECK_DEVICES);
        }
        return newList;
    }
    public static void init_SharedResources() {
        WORKSPACE           = new ArrayList<Object>();
        DISCOVERED_DEVICES  = Collections.synchronizedList(new ArrayList<HashMap<String, Object>>());//<HashMap<String, Object>>();
        GROUP_LIST = Collections.synchronizedMap(new HashMap<String, HashMap<String, Object>>());
        CONNECTED_DEVICES    = Collections.synchronizedList(new ArrayList<HashMap<String, String>>());
        STATE_OBJECTS       = new String();
        STATE_OBJECTS_DEVICES=Collections.synchronizedMap(new HashMap<String, HashMap<String, Object>>());
        STATE_CHECK_DEVICES = Collections.synchronizedMap(new HashMap<String, HashMap<String, Object>>());
        //LOCKED_REQUESTS     = false;
        ACT_AS_SERVER       = false;
        //SYSTEM_DATE         = "";
        setServerTime();//SYSTEM_DATE
        init_CONNECTED_DEVICES();
    }

    public static void init_CONNECTED_DEVICES(){
        HashMap<String,String> newData = new HashMap<>();
        if (WorkTogetherSetup.is_TabletorPhone) {// is tablet
            newData.put("TA1","");//Side Up one
            newData.put("TA2","");//Side Up two
            newData.put("TB1","");//Side Down one
            newData.put("TB2","");//Side Down two
            newData.put("TC1","");//Side Left
            newData.put("TC2","");//Side Right
        }else {// is phone
            newData.put("PA1","");//Side Up
            newData.put("PB1","");//Side Down
            newData.put("PC1","");//Side Left
            newData.put("PC2","");//Side Right
        }
        CONNECTED_DEVICES.add(newData);
    }

    private static void setServerTime(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        SharedResources.SYSTEM_DATE = dateFormat.format(date).toString();
    }
    public static void freeResources() {
        DISCOVERED_DEVICES.clear();
        GROUP_LIST.clear();
        CONNECTED_DEVICES.clear();
        STATE_OBJECTS_DEVICES.clear();
    }
}