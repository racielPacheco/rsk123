package com.cinvestav.worktogether.services.couplingcontrol;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.cinvestav.worktogether.R;
import com.cinvestav.worktogether.setup.WorkTogetherSetup;
import com.cinvestav.worktogether.global.DeviceFeatures;
import com.cinvestav.worktogether.global.SharedResources;
import com.cinvestav.worktogether.global.Utilities;
import com.cinvestav.worktogether.services.updatedistribution.CustomListener;
import com.cinvestav.worktogether.services.updatedistribution.UpdateDistributionService;
import com.cinvestav.worktogether.services.updatedistribution.UpdateSettings;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

/**
 * Created by erik reyes on 6/24/16.
 */
public class ListenOnePairingRequest extends AsyncTask<Object, Void, Boolean> {

    private final CouplingSettings  coupling_settings;
    private final UpdateSettings    updating_settings;
    private final String            ownDirection;
    private final DeviceFeatures    features;
    private final CustomListener    customListener;

    private final Context           ctx;

    public ListenOnePairingRequest(Context ctx, CouplingSettings param, UpdateSettings updating_settings, String ownDir, DeviceFeatures features, CustomListener customListener) {
        this.ctx                = ctx;
        this.features           = features;
        coupling_settings       = param;
        this.updating_settings  = updating_settings;
        ownDirection            = ownDir;
        this.customListener     = customListener;
    }

    @Override
    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch", "UseSpecificCatch"})
    protected Boolean doInBackground(Object... args) {
        try {
            /*
                Inicia un nuevo intento de acoplamiento, así que aún no se decide quién será el servidor encargado de
                distribuir la lista de grupo.
            */

            //SharedResources.ACT_AS_SERVER = false;

            DatagramSocket socket = new DatagramSocket( coupling_settings.COUPLING_SERVICE_PORT );
            socket.setBroadcast( true );
            socket.setSoTimeout( coupling_settings.COUPLING_WAITING_TIME );
            byte[] buf = new byte[ coupling_settings.COUPLING_MESSAGE_SIZE ];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            try {
                socket.receive(packet);
                while (packet.getAddress().getHostAddress().equals(Utilities.getIPAddress(true))) {
                    //Log.d(Utilities.TAG, "Ignoring own pairing request...");
                    packet = new DatagramPacket( buf, buf.length );
                    socket.receive(packet);
                }
            } catch (Exception ex) {
                socket.close();
                somethingHappened();
                Log.d(Utilities.TAG, "No matches found...");// + ex.toString());
                //SharedResources.LOCKED_REQUESTS = false;

                try{
                    MediaPlayer mp = MediaPlayer.create(ctx, R.raw.end);
                    mp.setVolume(Utilities.volume, Utilities.volume);
                    mp.start();
                }catch(Exception e){
                    Toast.makeText(ctx, e.toString(), Toast.LENGTH_LONG).show();
                }

                return false;
            }
            socket.close();

            /*
             * Si se recibieron datos, procesarlos.
             */
            String receivedData = new String(buf);
            receivedData = receivedData.replaceAll("[^\\p{Print}]", "");

            JSONObject request_JSON = new JSONObject(receivedData);
            HashMap<String, Object> request = new HashMap<String, Object>();
            //request.put("IP", request_JSON.get("IP").toString());
            request.put("TCP", request_JSON.get("TCP").toString());
            request.put("UDP", request_JSON.get("UDP").toString());
            request.put("SPECS", strToMap(request_JSON.get("SPECS").toString()));

            String receivedDirection = request_JSON.get("DIR").toString();
            Log.d(Utilities.TAG, "Request ::" + receivedDirection +":: received from " + request_JSON.get("IP").toString());
            Log.d(Utilities.TAG, "My Direction is  ::" + ownDirection);
            /*
             * Revisar si las direcciones de los gestos de arrastre (pinch) son inversas y si el dispositivo no estaba
             * ya en la lista del grupo.
             */
            boolean pair = false;

            Map<String, HashMap<String, Object>> addMe = SharedResources.GROUP_LIST;

            if (!(addMe.containsKey(request_JSON.get("IP").toString()))){
                if (receivedDirection.equals("R") && ownDirection.equals("L")) {
                    pair = true;
                } else if (receivedDirection.equals("L") && ownDirection.equals("R")) {
                    pair = true;
                } else if (receivedDirection.equals("U") && ownDirection.equals("D")) {
                    pair = true;
                } else if (receivedDirection.equals("D") && ownDirection.equals("U")) {
                    pair = true;
                }
            }
            /*
             * Verifica si el lado de acoplamiento esta libre
             */
            //Log.d(Utilities.TAG,"Pair:"+pair);
            Boolean connectedSide = false;
            String sideDir = null;
            String sideOwnDir = null;

            int idDir = 0;
            if (pair) {

                List<HashMap<String, String>> connected_copy = SharedResources.copy_CONNECTED_DEVICES();
                HashMap<String, String> map_connected = connected_copy.get(0);

                Boolean existe = true;
                for (Map.Entry<String,String> entry: map_connected.entrySet()){
                    if (entry.getValue().equals(request_JSON.get("IP").toString()))
                        existe = false;
                }
                if (existe)
                for (HashMap<String, String> item : connected_copy) {
                    if (WorkTogetherSetup.is_TabletorPhone) {//if is a tablet
                        if (ownDirection.equals("U")) {
                            if (item.get("TA1").isEmpty()) {
                                map_connected.put("TA1", request_JSON.get("IP").toString());
                                sideDir = "B1";
                                sideOwnDir = "TA1";
                                idDir = 0;
                                connectedSide = true;
                            } else {
                                if (item.get("TA2").isEmpty()) {
                                    map_connected.put("TA2", request_JSON.get("IP").toString());
                                    sideDir = "B2";
                                    sideOwnDir = "TA2";
                                    idDir = 1;
                                    connectedSide = true;
                                } else {
                                    Log.d(Utilities.TAG, "SIDE UP IS BUSY, TRY ANOTHER SIDE OF TABLET");
                                    toToast("DIRECCION UP OCUPADO, INTENTA OTRO LADO");
                                }
                            }
                        } else if (ownDirection.equals("D")) {
                            if (item.get("TB1").isEmpty()) {
                                map_connected.put("TB1", request_JSON.get("IP").toString());
                                sideDir = "A1";
                                sideOwnDir = "TB1";
                                idDir = 2;
                                connectedSide = true;
                            } else {
                                if (item.get("TB2").isEmpty()) {
                                    map_connected.put("TB2", request_JSON.get("IP").toString());
                                    sideDir = "A2";
                                    sideOwnDir = "TB2";
                                    idDir = 3;
                                    connectedSide = true;
                                } else {
                                    Log.d(Utilities.TAG, "SIDE DOWN IS BUSY, TRY ANOTHER SIDE OF TABLET");
                                    toToast("DIRECCION DOWN OCUPADO, INTENTA OTRO LADO");
                                }
                            }
                        } else if (ownDirection.equals("L")) {
                            if (item.get("TC1").isEmpty()) {
                                map_connected.put("TC1", request_JSON.get("IP").toString());
                                sideDir = "C2";
                                sideOwnDir = "TC1";
                                idDir = 4;
                                connectedSide = true;
                            } else {
                                Log.d(Utilities.TAG, "SIDE LEFT IS BUSY, TRY ANOTHER SIDE OF TABLET");
                                toToast("DIRECCION LEFT OCUPADO, INTENTA OTRO LADO");
                            }
                        } else if (ownDirection.equals("R")) {
                            if (item.get("TC2").isEmpty()) {
                                map_connected.put("TC2", request_JSON.get("IP").toString());
                                sideDir = "C1";
                                sideOwnDir = "TC2";
                                idDir = 5;
                                connectedSide = true;
                            } else {
                                Log.d(Utilities.TAG, "SIDE RIGHT IS BUSY, TRY ANOTHER SIDE OF TABLET");
                                toToast("DIRECCION RIGHT OCUPADO, INTENTA OTRO LADO");
                            }
                        }
                    } else {// phone
                        if (ownDirection.equals("U")) {
                            if (item.get("PA1").isEmpty()) {
                                map_connected.put("PA1", request_JSON.get("IP").toString());
                                sideDir = "B1";
                                sideOwnDir = "PA1";
                                idDir = 0;
                                connectedSide = true;
                            } else {
                                Log.d(Utilities.TAG, "SIDE UP IS BUSY, TRY ANOTHER SIDE OF PHONE");
                                toToast("DIRECCION UP OCUPADO, INTENTA OTRO LADO");
                            }
                        } else if (ownDirection.equals("D")) {
                            if (item.get("PB1").isEmpty()) {
                                map_connected.put("PB1", request_JSON.get("IP").toString());
                                sideDir = "A1";
                                sideOwnDir = "PB1";
                                idDir = 1;
                                connectedSide = true;
                            } else {
                                Log.d(Utilities.TAG, "SIDE DOWN IS BUSY, TRY ANOTHER SIDE OF PHONE");
                                toToast("DIRECCION DOWN OCUPADO, INTENTA OTRO LADO");
                            }
                        } else if (ownDirection.equals("L")) {
                            if (item.get("PC1").isEmpty()) {
                                map_connected.put("PC1", request_JSON.get("IP").toString());
                                sideDir = "C2";
                                sideOwnDir = "PC1";
                                idDir = 2;
                                connectedSide = true;
                            } else {
                                Log.d(Utilities.TAG, "SIDE LEFT IS BUSY, TRY ANOTHER SIDE OF PHONE");
                                toToast("DIRECCION LEFT OCUPADO, INTENTA OTRO LADO");
                            }
                        } else if (ownDirection.equals("R")) {
                            if (item.get("PC2").isEmpty()) {
                                map_connected.put("PC2", request_JSON.get("IP").toString());
                                sideDir = "C1";
                                sideOwnDir = "PC2";
                                idDir = 3;
                                connectedSide = true;
                            } else {
                                Log.d(Utilities.TAG, "SIDE RIGHT IS BUSY, TRY ANOTHER SIDE OF PHONE");
                                toToast("DIRECCION RIGHT OCUPADO, INTENTA OTRO LADO");
                            }
                        }
                    }
                }
                SharedResources.CONNECTED_DEVICES.clear();
                SharedResources.CONNECTED_DEVICES.add(map_connected);
                Log.d(Utilities.TAG, "existe..."+existe);
                Log.d(Utilities.TAG, "connectedSide..."+connectedSide);
            }


            /*
             * Si el acoplamiento se puede realizar: agregarlo al grupo y actualizar dicha lista en ambos dispositivos.
             */
            if (connectedSide){
                try{
                    MediaPlayer mp = MediaPlayer.create(ctx, R.raw.successful);
                    mp.start();
                }catch(Exception e){
                    Toast.makeText(ctx, e.toString(), Toast.LENGTH_LONG).show();
                }

                /* Si la lista está vacía, nos agregamos a nosotros mismos */
                if( SharedResources.GROUP_LIST.isEmpty() ){
                    HashMap<String, Object> me = new HashMap<String, Object>();
                    //me.put("IP", Utilities.getIPAddress(true) );
                    me.put("TCP", String.valueOf( updating_settings.UPDATE_DISTRIBUTION_TCP_PORT ) );
                    me.put("UDP", String.valueOf( updating_settings.UPDATE_DISTRIBUTION_UDP_PORT ) );
                    me.put("SPECS", features.deviceFeatures);
                    // nos agregamos a la lista
                    SharedResources.GROUP_LIST.put(Utilities.getIPAddress(true), me);//add key an objects
                }
                //agregamos al dispositivo nuevo
                SharedResources.GROUP_LIST.put(request_JSON.get("IP").toString(), request);//add key an objects
               // Log.d(Utilities.TAG,"ListaDa2:"+SharedResources.copy_GROUP_LIST().toString());

                /*
                    Hacemos uso del servicio de distribución de actualizaciones para propagar la lista de grupo.
                    El protocolo a seguir es:
                        1.- Decidir quién estará a cargo de dicha tarea.
                        2.- Distribuir la lista de grupo.
                */

                int[] backColor = customListener.retrieveBackGroundColor();
                String tobackColor = backColor[0]+":"+backColor[1]+":"+backColor[2];
                String type = "Net_RequestForBecomingServer";
                JSONObject objx = new JSONObject( );
                objx.put("@type", type);
                objx.put("id", customListener.retrieveSessionID().toString());
                objx.put("backcolor",tobackColor);// color de fondo ej. 30:123:210
                //objx.put("date",request_JSON.get("DATE").toString());
                objx.put("date",SharedResources.SYSTEM_DATE);
                String ipClient = request_JSON.get("IP").toString();
                UpdateDistributionService.sendObject(ipClient, request, objx.toString(), "TCP", true);

                // se genera un color rgb aleatorio
                int R = (int)(Math.random()*256);
                int G = (int)(Math.random()*256);
                int B= (int)(Math.random()*256);

                //mostramos la barra de color de conectado
                int[] rgb = new int[]{R,G,B};
                customListener.setColorConnected(rgb,idDir);

                String typec = "Net_DirConnected";
                JSONObject objc = new JSONObject( );
                objc.put("@type", typec);
                objc.put("receivedir", receivedDirection);//U,D,L,R
                objc.put("owndir",sideOwnDir);//is phone(PA1,PB1,PC1,PC2) or tablet (TA1,TA2,TB1,TB2,TC1,TC2)
                objc.put("response","yes");// request response
                objc.put("ip",Utilities.getIPAddress(true));//my ip
                objc.put("TCP", updating_settings.UPDATE_DISTRIBUTION_TCP_PORT);
                objc.put("UDP", updating_settings.UPDATE_DISTRIBUTION_UDP_PORT);
                //objc.put("TCP", request_JSON.get("TCP").toString());
                //objc.put("UDP", request_JSON.get("UDP").toString());
                objc.put("R", Integer.toString(R));
                objc.put("G", Integer.toString(G));
                objc.put("B", Integer.toString(B));
                //String ipClient = request_JSON.get("IP").toString();
                UpdateDistributionService.sendObject(ipClient, request, objc.toString(), "TCP", false);



            }else{
                // Si no se prosigue con el acoplamiento, habilitar los intentos nuevamente
                //SharedResources.LOCKED_REQUESTS = false;
                Log.d(Utilities.TAG, "NO ATTEMPT WILL BE MADE COUPLING. PAIR IS FALSE");
                toToast("INTENTO DE ACOPLAMIENTO ERRONEO");
            }
        } catch (Exception e) {
            Log.d(Utilities.TAG, "ListenOnePairingRequest exception: " + e.toString());
            StackTraceElement[] elements = e.getStackTrace();
            for (int iterator=1; iterator<=elements.length; iterator++){
                Log.d(Utilities.TAG, "ListenOnePairingRequest::exception: " + "Class Name:"+elements[iterator-1].getClassName()+" Method Name:"+elements[iterator-1].getMethodName()+" Line Number:"+elements[iterator-1].getLineNumber());
            }
        }
        return true;
    }

    public static HashMap<String, Object>strToMap(String str){
        //<editor-fold defaultstate="collapsed" desc="Convierte una cadena a MAP">
        HashMap<String, Object> map = new HashMap<String, Object>();
        str = str.substring(0, str.length()).replace("{ ","{");
        str = str.substring(0, str.length()).replace(" }","}");
        str = str.substring(0, str.length()).replace("{","");
        str = str.substring(0, str.length()).replace("}","");
        str = str.substring(0, str.length()).replace(", ", "\",\"");
        str = str.substring(0, str.length()).replace("\"","");
        str = str.substring(0, str.length()).replace("=",":");
        String[] elements = str.split(",");
        for(String s1: elements) {
            String[] keyValue = s1.split(":");
            map.put(keyValue[0], keyValue[1]);
        }
        //</editor-fold>
        return map;
    }
    public void toToast(final String msg){
        //<editor-fold defaultstate="collapsed" desc="Toast message">
        final Context cotextX = this.ctx;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(cotextX, msg, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        //</editor-fold>
    }

    private void somethingHappened()
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        toToast("NO HAY INTENTOS DE ACOPLAMIENTO");
                    }
                }
        );
    }


}