/*
    Aplicación de ejemplo. Modificaciones al soporte:

        Clase 		UpdateDistributionService
        Clase		ResponseToRequestFromBecomingServer
        Clase   	RequestForBecomingServer
        Clase		WorkTogetherSetup
        Clase		AccelerometerGesture
        Clase		SwipeGesture
        Nueva Clase     Net_Message
        Nueva Interfaz 	CustomListener
*/

package com.cinvestav.tesis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.cinvestav.worktogether.setup.WorkTogetherSetup;
import com.cinvestav.worktogether.global.SharedResources;
import com.cinvestav.worktogether.global.Utilities;
import com.cinvestav.worktogether.services.couplingcontrol.WorkspaceTouchEventListener;
import com.cinvestav.worktogether.services.devicediscovery.DiscoveryServiceListener;
import com.cinvestav.worktogether.services.updatedistribution.CustomListener;
import com.cinvestav.worktogether.services.updatedistribution.UpdateDistributionService;
import com.cinvestav.worktogether.services.updatedistribution.UpdateDistributionServiceListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
//toast
import com.toast.AnimationStyle;
import com.toast.CRToast;
import com.toast.CRToastManager;

import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.Toast;

/**
 *
 * @author Erik Alejandro Reyes Lozano
 */
public class ExampleWorkTogether extends Activity implements DiscoveryServiceListener, WorkspaceTouchEventListener, UpdateDistributionServiceListener, CustomListener
{
    WorkTogetherSetup workTogether;
    CRToast crToast;
    private App app;
    private View viewAPP;

    private String TAG;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        try{
            super.onCreate(savedInstanceState);


            // do not add title in app
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            //get fullscreen on app
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if (isTabletorPhone()){
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            else {
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }

            // create objects on app (menus,blocks,etc)
            app = new App(this);
            // request view root
            viewAPP = getWindow().getDecorView().getRootView();
            // start framework workTogether
            workTogether = new WorkTogetherSetup(this,viewAPP,this,this,this,this);

            app.setExampleWorkTogether(this);

            //get TAG for log
            TAG = app.getTAG();
            String typeDevice = "ANDROID";// IOS OR ANDROID
            String nameDevice = app.getNameDevice();// NAME OF DEVICE
            int type = 0;
            if(typeDevice.equals("IOS"))//logo android o ios
                    type = 0;// IOS
            else
                    type = 1;//ANDROID
                                                        //color blue
            showToast("Registrado en el grupo", "My " + nameDevice, Color.argb(255, 204, 255, 255), type);

            checkWiFiSate();// continuamente checar el estado de la conexion WiFi

        }catch(Exception e){
            Log.d(TAG, e.toString());
        }
    }

    /**
     * check status WiFi x second
     */
    private void checkWiFiSate(){
        //<editor-fold defaultstate="collapsed" desc="Revisa continuamente la conexion WiFi">
        final Context c = this;
        final ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                boolean keepRunning = true;
                while (keepRunning) {
                    try {
                        //ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                        if (!wifi.isConnected()){
                            // If Wi-Fi is not connected
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    final AlertDialog.Builder builder1 = new AlertDialog.Builder(c);
                                    builder1.setTitle("NO HAY CONEXIÓN WiFi");
                                    builder1.setMessage("ESTA APP REQUIERE CONEXIÓN A LA RED LOCAL");
                                    builder1.setCancelable(true);

                                    builder1.setPositiveButton(
                                            "ACEPTAR",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    onStop();
                                                    android.os.Process.killProcess(android.os.Process.myPid());
                                                    System.exit(1);
                                                }
                                            });

                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                }
                            });
                            keepRunning = false;
                        }//if
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                }//while
            }
        };
        Thread myThread = new Thread(myRunnable);
        myThread.start();
        //</editor-fold>
    }

    public UUID retrieveSessionID() {
        return app.getUUID();
    }

    public void setSessionID(final UUID id) {
        //<editor-fold defaultstate="collapsed" desc="Actualizar la pantalla si ocurrió un cambio del contador">
        Log.d(TAG, "APP ID:" + id.toString());
        // Actualizar el id en los otros dispositivos que no son parte del acoplamiento
        // pero que ya pertenecían a la misma sesión colaborativa.
        String data[] = new String[2];
        data[0] = "Net_SessionID";
        data[1] = id.toString().toUpperCase();

        UpdateDistributionService.spreadObject(jSON(data, 2), "TCP");
        runOnUiThread(new Runnable() {
            public void run() {
                app.seteID(id);
            }
        });
        //</editor-fold>
    }

    public int[] retrieveBackGroundColor(){
        return app.getBackGroundColor();
    }
    public void setBackGroundColor(final int[] backGroundColor){
        String data[] = new String[4];
        data[0] = "Net_BackGroundColor";
        data[1] = Integer.toString(backGroundColor[0]);
        data[2] = Integer.toString(backGroundColor[1]);
        data[3] = Integer.toString(backGroundColor[2]);

        UpdateDistributionService.spreadObject(jSON(data, 4), "TCP");
        runOnUiThread(new Runnable() {
            public void run() {
                app.setBackGroundColor(backGroundColor);
            }
        });
    }

    public void setColorConnected(final int[] color,final int ownDir) {
        //<editor-fold defaultstate="collapsed" desc="Actualizar la pantalla si se conecto un dispositivo">
        runOnUiThread(new Runnable() {
            public void run() {
                app.setConnectedDeviceColor(color, ownDir);
            }
        });
        //</editor-fold>
    }

    public void setColorDisonnected(final String direction){
        //<editor-fold defaultstate="collapsed" desc="Actualizar la pantalla si se conecto un dispositivo">
        runOnUiThread(new Runnable() {
            public void run() {
                app.setDisconnectedDevice(direction);
            }
        });
        //</editor-fold>
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        //<editor-fold defaultstate="collapsed" desc="Avisar sobre el cambio de especificaciones">
        super.onConfigurationChanged(newConfig);
        workTogether.onConfigurationChanged();

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
           // app.onChangedScreen();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
           // app.onChangedScreen();
        }
        //</editor-fold>
    }

    @Override
    protected void onPause(){
        //<editor-fold defaultstate="collapsed" desc="Parar el acelerómetro">
        workTogether.onPause();
        super.onPause();
        //</editor-fold>
    }

    @Override
    protected void onStop() {
        //<editor-fold defaultstate="collapsed" desc="Detener servicios, el servidor y borrar variables">
        workTogether.onStop();
        super.onStop();
        //</editor-fold>
    }

    public void DiscoveryServiceDeviceJoined(final HashMap<String, Object> hm) {
        //<editor-fold defaultstate="collapsed" desc="Avisar que un dispositivo se unió">
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String typeDevice = hm.get("TYPEDEVICE").toString();// IOS OR ANDROID
                String nameDevice = hm.get("DEVICE").toString();// NAME OF DEVICE
                int type = 0;
                if (typeDevice.equals("IOS"))//logo android o ios
                    type = 0;// IOS
                else
                    type = 1;//ANDROID
                // color green
                showToast("Dispositivo conectado", "Device " + nameDevice, Color.argb(255, 204, 255, 204), type);

            }
        });
        //</editor-fold>
    }

    public void DiscoveryServiceDeviceLeft(final HashMap<String, Object> hm, boolean deviceWasOnDiscoveredDevicesList,
                                           boolean deviceWasOnGroupList) {
        //<editor-fold defaultstate="collapsed" desc="Avisar que un dispositivo salió">
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String typeDevice = hm.get("TYPEDEVICE").toString();// IOS OR ANDROID
                String nameDevice = hm.get("DEVICE").toString();// NAME OF DEVICE
                int type = 0;
                if (typeDevice.equals("IOS"))//logo android o ios
                    type = 0;// IOS
                else //if(typeDevice.equals("ANDROID"))
                    type = 1;//ANDROID
                showToast("Dispositivo desconectado", "Device " + nameDevice, Color.argb(255, 255, 204, 204), type);
            }
        });
        //</editor-fold>
    }

    public void sendNetMessage(String[] data,HashMap<String, Object> infoDevice,String ipClient){
            UpdateDistributionService.sendObject(ipClient, infoDevice, jSON(data, 3), "TCP", false);
    }
    public void sendBackGroudColor(int[] backGroundColor){
        Log.d(app.getTAG(),"Cambio color :"+ Arrays.toString(SharedResources.copy_GROUP_LIST().entrySet().toArray()));
        if (!SharedResources.GROUP_LIST.isEmpty()){
            String data[] = new String[4];
            data[0] = "Net_BackGroundColor";
            data[1] = Integer.toString(backGroundColor[0]);
            data[2] = Integer.toString(backGroundColor[1]);
            data[3] = Integer.toString(backGroundColor[2]);
            UpdateDistributionService.spreadObject(jSON(data, 4), "TCP");//Net_BackGroundColor ,backcolor
        }
    }
    public void workspaceTapEventHappened(View view, MotionEvent event) {
        //<editor-fold defaultstate="collapsed" desc="No usado">

        if (event.getAction() == MotionEvent.ACTION_UP){
            if( !SharedResources.GROUP_LIST.isEmpty() ) {

            } else {

            }
        }
        //</editor-fold>
    }

    @Override
    public void checkObjectReceived(Object objectReceived, String from) {
        //<editor-fold defaultstate="collapsed" desc="Procesar los objetos recibidos ajenos al soporte">
        try{
            JSONObject jObject = new JSONObject(objectReceived.toString());
            String type = jObject.getString("@type");

            if( type.equals("Net_Message") ) {

                final int xp = Integer.parseInt(jObject.getString("xp"));
                final int yp = Integer.parseInt(jObject.getString("yp"));
                final String text = jObject.getString("text");
                final String ip = from;

                String bcolor = jObject.getString("color");
                String[] elements = bcolor.split(":");
                final int[] RGBCOLOR = new int[elements.length];
                for(int i=0;i < elements.length;++i)
                    RGBCOLOR[i] = Integer.parseInt(elements[i]);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        app.bloquesExternos(xp,yp,text,RGBCOLOR,ip);
                    }
                });
            } else if( type.equals("Net_DirConnected") ) {

                String response = jObject.getString("response");// yes or not
                String ownDirection = jObject.getString("receivedir");//U,D,L,R
                String ip = jObject.getString("ip");

                String sendDir = jObject.getString("owndir");//is phone(PA1,PB1,PC1,PC2) or tablet (TA1,TA2,TB1,TB2,TC1,TC2)

                int[] colorRGB = new int[]{Integer.parseInt(jObject.getString("R")),Integer.parseInt(jObject.getString("G")),Integer.parseInt(jObject.getString("B"))};
                int idDir = 0;
                String dir = null;

                List<HashMap<String, String>> connected_copy = SharedResources.copy_CONNECTED_DEVICES();
                HashMap<String, String> map_connected = connected_copy.get(0);

                if (response.equals("yes")){

                    for (Map.Entry<String,String> entry: map_connected.entrySet()){// si ya existe la ip la borramos y la agregamos de nuevo
                        if (entry.getValue().equals(ip))
                            map_connected.put(entry.getKey(), "");
                    }

                    for (HashMap<String, String> item : connected_copy) {
                        if (isTabletorPhone()) {//if is a tablet
                            if (ownDirection.equals("U")) {
                                if (item.get("TA1").isEmpty()) {
                                    map_connected.put("TA1", ip);
                                    idDir = 0;
                                    dir = "TA1";
                                } else {
                                    if (item.get("TA2").isEmpty()) {
                                        map_connected.put("TA2", ip);
                                        idDir = 1;
                                        dir = "TA2";
                                    } else {
                                        Log.d(Utilities.TAG, "SIDE UP IS BUSY, TRY ANOTHER SIDE OF TABLET");
                                        //toToast("SIDE UP IS BUSY, TRY ANOTHER SIDE OF TABLET");
                                    }
                                }
                            } else if (ownDirection.equals("D")) {
                                if (item.get("TB1").isEmpty()) {
                                    map_connected.put("TB1", ip);
                                    idDir = 2;
                                    dir = "TB1";
                                } else {
                                    if (item.get("TB2").isEmpty()) {
                                        map_connected.put("TB2", ip);
                                        idDir = 3;
                                        dir = "TB2";
                                    } else {
                                        Log.d(Utilities.TAG, "SIDE DOWN IS BUSY, TRY ANOTHER SIDE OF TABLET");
                                        //toToast("SIDE DOWN IS BUSY, TRY ANOTHER SIDE OF TABLET");
                                    }
                                }
                            } else if (ownDirection.equals("L")) {
                                if (item.get("TC1").isEmpty()) {
                                    map_connected.put("TC1", ip);
                                    idDir = 4;
                                    dir = "TC1";
                                } else {
                                    Log.d(Utilities.TAG, "SIDE LEFT IS BUSY, TRY ANOTHER SIDE OF TABLET");
                                    //toToast("SIDE LEFT IS BUSY, TRY ANOTHER SIDE OF TABLET");
                                }
                            } else if (ownDirection.equals("R")) {
                                if (item.get("TC2").isEmpty()) {
                                    map_connected.put("TC2", ip);
                                    idDir = 5;
                                    dir = "TC2";
                                } else {
                                    Log.d(Utilities.TAG, "SIDE RIGHT IS BUSY, TRY ANOTHER SIDE OF TABLET");
                                    //toToast("SIDE RIGHT IS BUSY, TRY ANOTHER SIDE OF TABLET");
                                }
                            }
                        } else {// phone
                            if (ownDirection.equals("U")) {
                                if (item.get("PA1").isEmpty()) {
                                    map_connected.put("PA1", ip);
                                    idDir = 0;
                                    dir = "PA1";
                                } else {
                                    Log.d(Utilities.TAG, "SIDE UP IS BUSY, TRY ANOTHER SIDE OF PHONE");
                                    //toToast("SIDE UP IS BUSY, TRY ANOTHER SIDE OF PHONE");
                                }
                            } else if (ownDirection.equals("D")) {
                                if (item.get("PB1").isEmpty()) {
                                    map_connected.put("PB1", ip);
                                    idDir = 1;
                                    dir = "PB1";
                                } else {
                                    Log.d(Utilities.TAG, "SIDE DOWN IS BUSY, TRY ANOTHER SIDE OF PHONE");
                                    //toToast("SIDE DOWN IS BUSY, TRY ANOTHER SIDE OF PHONE");
                                }
                            } else if (ownDirection.equals("L")) {
                                if (item.get("PC1").isEmpty()) {
                                    map_connected.put("PC1", ip);
                                    idDir = 2;
                                    dir = "PC1";
                                } else {
                                    Log.d(Utilities.TAG, "SIDE LEFT IS BUSY, TRY ANOTHER SIDE OF PHONE");
                                    //toToast("SIDE LEFT IS BUSY, TRY ANOTHER SIDE OF PHONE");
                                }
                            } else if (ownDirection.equals("R")) {
                                if (item.get("PC2").isEmpty()) {
                                    map_connected.put("PC2", ip);
                                    idDir = 3;
                                    dir = "PC2";
                                } else {
                                    Log.d(Utilities.TAG, "SIDE RIGHT IS BUSY, TRY ANOTHER SIDE OF PHONE");
                                    //toToast("SIDE RIGHT IS BUSY, TRY ANOTHER SIDE OF PHONE");
                                }
                            }
                        }
                    }
                    SharedResources.CONNECTED_DEVICES.clear();
                    SharedResources.CONNECTED_DEVICES.add(map_connected);

                    setColorConnected(colorRGB, idDir);

                    String typec = "Net_DirConnected";
                    JSONObject objc = new JSONObject( );
                    objc.put("@type", typec);
                    objc.put("receivedir", "x");//U,D,L,R
                    objc.put("owndir",sendDir);//is phone(PA1,PB1,PC1,PC2) or tablet (TA1,TA2,TB1,TB2,TC1,TC2)
                    objc.put("response", "no");// request response
                    objc.put("ip", ip);//my ip
                    objc.put("R", jObject.getString("R"));
                    objc.put("G", jObject.getString("G"));
                    objc.put("B", jObject.getString("B"));

                    HashMap<String, Object> request = new HashMap<String, Object>();
                    //request.put("IP", request_JSON.get("IP").toString());
                    request.put("TCP", jObject.get("TCP").toString());
                    request.put("UDP", jObject.get("UDP").toString());
                    //request.put("SPECS", strToMap(request_JSON.get("SPECS").toString()));

                    UpdateDistributionService.sendObject(ip, request, objc.toString(), "TCP", false);

                    //enviar respuesta y poner no
                }
                else {

                    if (sendDir.equals("TA1"))
                        idDir = 0;
                    else if (sendDir.equals("TA2"))
                        idDir = 1;
                    else if (sendDir.equals("TB1"))
                        idDir = 2;
                    else if (sendDir.equals("TB2"))
                        idDir = 3;
                    else if (sendDir.equals("TC1"))
                        idDir = 4;
                    else if (sendDir.equals("TC2"))
                        idDir = 5;
                    else if (sendDir.equals("PA1"))
                        idDir = 0;
                    else if (sendDir.equals("PB1"))
                        idDir = 1;
                    else if (sendDir.equals("PC1"))
                        idDir = 2;
                    else if (sendDir.equals("PC2"))
                        idDir = 3;

                    setColorConnected(colorRGB, idDir);
                }


            } else if( type.equals("Net_SessionID") ) {
                final UUID objid = UUID.fromString(jObject.getString("id"));
                runOnUiThread(new Runnable(){
                    public void run() {
                        app.seteID(objid);
                    }
                });
            }else if ( type.equals("Net_BackGroundColor")){
                //Log.d(app.getTAG(),"ExampleWorkTogether:checkObjectReceived  COLOR");
                String sbackColor = jObject.getString("backcolor");
                String[] objbColor = sbackColor.split(":");
                final int[] newbColorN = new int[objbColor.length];
                newbColorN[0] = Integer.parseInt(objbColor[0]);
                newbColorN[1] = Integer.parseInt(objbColor[1]);
                newbColorN[2] = Integer.parseInt(objbColor[2]);

                runOnUiThread(new Runnable(){
                    public void run() {
                        app.setBackGroundColor(newbColorN);
                    }
                });

            }

        } catch (JSONException ex) {
            Log.d(app.getTAG(),"ExampleWorkTogether:checkObjectReceived "+ ex.toString());
        }
        //</editor-fold>
    }

    public String jSON(String[] data,int opc){

        JSONObject objx = new JSONObject();
        try {
            switch(opc){
                case 1://Net_DirConnected
                    objx.put("@type", data[0]);
                    objx.put("dirconnected", data[1]);
                    objx.put("ip", data[2]);
                    break;
                case 2://Net_SessionID
                    objx.put("@type", data[0]);
                    objx.put("id", data[1]);
                    break;
                case 3://Net_Message
                    objx.put("@type", data[0]);
                    objx.put("xp", Integer.parseInt(data[1]));
                    objx.put("yp", Integer.parseInt(data[2]));
                    objx.put("text", data[3]);
                    String objColor = data[4]+":"+data[5]+":"+data[6];
                    objx.put("color", objColor);
                    objx.put("ip", data[7]);
                    break;
                case 4://Net_BackGroundColor
                    objx.put("@type", data[0]);
                    String backColor = data[1]+":"+data[2]+":"+data[3];
                    objx.put("backcolor", backColor);
            }
        } catch (JSONException ex) {
            Log.d(app.getTAG(),"ExampleWorkTogether:jSON "+ ex.toString());
        }
        return objx.toString();
    }

    private void showToast(String title, String subTitle,int xcolor,int type) {
        CRToast.Builder builder = new CRToast.Builder(this);
        builder.animationStyle(AnimationStyle.valueOf("RightToBottom"))
                .notificationMessage(title)
                .subtitleText(subTitle)
                .duration(3*1000)// tres segundos
                .dismissWithTap(false)
                .statusBarVisible(false)
                .backgroundColor(xcolor)//Color.GREEN
                .insideActionBar(false);// hay un error si es true

        if(type == 0)
            builder.image(ResourcesCompat.getDrawable(getResources(), R.drawable.apple_logo, null));
        else if (type == 1)
            builder.image(ResourcesCompat.getDrawable(getResources(), R.drawable.android_logo, null));
        else
            builder.image(ResourcesCompat.getDrawable(getResources(), R.drawable.wifi, null));

        crToast = builder.build();
        CRToastManager.show(crToast);
    }

    private boolean isTabletorPhone()
    {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels / displayMetrics.densityDpi;
        int height = displayMetrics.heightPixels / displayMetrics.densityDpi;

        double screenDiagonal = Math.sqrt(width * width + height * height);
        return (screenDiagonal >= 6.5);
    }
    public List<HashMap<String, Object>> getConected(){
        return workTogether.getConected();
    }

    public HashMap<String, String> getOwnInfo(){
        return workTogether.getOwnInfo();
    }

    public HashMap<String, String> getInfoConfig(){
        return workTogether.getInfoConfig();
    }

    public void setupCoupling(Boolean setAccelerometer, Boolean setSwipe){
        workTogether.setupCoupling(setAccelerometer,setSwipe);
    }
    public String[] getCoupling (){
        return workTogether.getCoupling();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("EXIT")
                .setMessage("ARE YOU SURE?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("NO", null).show();
    }

    public void setObjectsState(Map<Integer, HashMap<String, Object>> hsm){

        workTogether.setStateObjects(hashMapToString(hsm));
    }
    private static String hashMapToString(Map<Integer, HashMap<String, Object>> hsm){
        //<editor-fold defaultstate="collapsed" desc="Convertir un hasmap a string">
        String hsmtostring="";
        try{
            for (Map.Entry<Integer, HashMap<String, Object>> entry : hsm.entrySet()) {
                //Integer key = entry.getKey();// ID del Bloque
                HashMap<String, Object> obj = entry.getValue();
                hsmtostring += obj.get("TEXT").toString() + "=" + obj.get("COLOR").toString() + "#";
            }
        }catch(Exception e){
            Log.d("workTogether","WorkTogetherSetup:hashMaptoString "+ e);
            //StackTraceElement[] elements = e.getStackTrace();
            //for (int iterator=1; iterator<=elements.length; iterator++){
            //    Log.d(Utilities.TAG, "UpdateDistributionService::stringToMap: " + "Class Name:"+elements[iterator-1].getClassName()+" Method Name:"+elements[iterator-1].getMethodName()+" Line Number:"+elements[iterator-1].getLineNumber());
            // }
        }
        if (hsmtostring.endsWith("#"))
            hsmtostring = hsmtostring.substring(0, hsmtostring.length()-1);
        return hsmtostring;
        //</editor-fold>
    }

    public void setRestoreObjects(final String restoreObjects,final String ip){
        Log.d(TAG, "RESTORE WORK APP" + restoreObjects);
        final Context c = this;
        runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(c);
                builder1.setMessage("DO YOU WANT RESTORE OBJECTS FROM ANOTHER DEVICE");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //dialog.cancel();
                                app.restoreObjects(restoreObjects,ip);
                    }
                });

                builder1.setNegativeButton(
                "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

    }
}
