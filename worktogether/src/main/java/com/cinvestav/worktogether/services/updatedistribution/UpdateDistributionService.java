package com.cinvestav.worktogether.services.updatedistribution;


import android.util.Log;
import com.cinvestav.worktogether.global.SharedResources;
import com.cinvestav.worktogether.global.Utilities;
import com.cinvestav.worktogether.services.updatedistribution.networkclasses.Net_DeviceFeatures;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class UpdateDistributionService
 */
public class UpdateDistributionService {
    /*
        El servidor es quien procesa los mensajes recibidos en el puerto propagado a través del 
        descubrimiento de servicios (UPDATE_DISTRIBUTION_TCP_PORT).
    */
    private final UpdateDistributionServiceListener listener;
    private static CustomListener customListener;

    private volatile boolean        keepRunning;
    private final UpdateSettings    update_settings;

    public UpdateDistributionService( UpdateDistributionServiceListener listener, UpdateSettings settings, CustomListener customListener ){
        this.listener = listener;
        UpdateDistributionService.customListener = customListener;
        update_settings = settings;
        keepRunning = true;
    }

    public void startService( ){
        new Thread(new UpdateDistributionTCPServer()).start();
    }

    public void stopService(){
        keepRunning = false;
    }

    private class UpdateDistributionTCPServer implements Runnable{
        //<editor-fold defaultstate="collapsed" desc="Servidor encargado de recolectar y procesar los objetos recibidos">
        public void run() {
            try{
                String receivedData;
                ServerSocket welcomeSocket = new ServerSocket( update_settings.UPDATE_DISTRIBUTION_TCP_PORT );
                //welcomeSocket.setSoTimeout( 8000 );
                while (keepRunning) {
                    //Log.d(Utilities.TAG, "WAITING FOR CONNECTIONS ON TCP SERVER");
                    Socket clientSocket;
                    try{
                        clientSocket = welcomeSocket.accept();
                    } catch( InterruptedIOException ex ) {
                        continue;
                    }

                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            clientSocket.getInputStream()));

                    receivedData = in.readLine();
                    Log.d(Utilities.TAG, "ORIGINAL MESSAGE RECEIVED ---> "+receivedData);
                    receivedData = receivedData.replaceAll("[^\\p{Print}]", "");

                    JSONObject newData_JSON = new JSONObject(receivedData);
                    Boolean bmensaje = true;//true el mensaje es dirigido a la app y no al framework

                    String type =newData_JSON.getString("@type");// tipo de mensaje

                    if(type.equals("Net_RequestForBecomingServer")){
                        bmensaje = false;
                        String receivedDate = newData_JSON.get("date").toString();
                        int ownTime = getTimeSystem(0,"");
                        int receivedTime = getTimeSystem(1,receivedDate);
                        // compara el tiempo recibido con el del framework
                        if (ownTime < receivedTime){
                            SharedResources.ACT_AS_SERVER = true;
                        }else {
                            SharedResources.ACT_AS_SERVER = false;
                        }

                        InetAddress addr = clientSocket.getInetAddress();
                        int         port = clientSocket.getPort();
                        Log.d(Utilities.TAG, "::: IP:::"+ addr);
                        Log.d(Utilities.TAG, "::: Puerto:::"+port);

                        List<HashMap<String, Object>> discoveredDevices_copy = SharedResources.copy_DISCOVERED_DEVICES();
                        //Log.d(Utilities.TAG, "::: Lista Disco:::"+Arrays.toString(discoveredDevices_copy.toArray()));
                        for ( HashMap<String, Object> item : discoveredDevices_copy ) {
                            if ( item.get("IP").toString().equals( addr.getHostAddress() ) ) {
                                port = Integer.parseInt(item.get("TCP").toString());
                                Log.d(Utilities.TAG, "::: Puerto2:::"+port);
                                break;
                            }
                        }
                        Socket socket = new Socket(addr.getHostAddress(), port);

                        String sbackColor = newData_JSON.get("backcolor").toString();// get backgroud color
                        String[] objbColor = sbackColor.split(":");//split message color
                        final int[] recbColor = new int[objbColor.length];//array color rgb received
                        recbColor[0] = Integer.parseInt(objbColor[0]);//red color
                        recbColor[1] = Integer.parseInt(objbColor[1]);//green color
                        recbColor[2] = Integer.parseInt(objbColor[2]);//blue color

                        customListener.setBackGroundColor(recbColor);// set new backgroud color

                        UUID recID = UUID.fromString(newData_JSON.get("id").toString());//get UUID

                        int[] ownbColor       = customListener.retrieveBackGroundColor();//get own color
                        UUID ownID              = customListener.retrieveSessionID();

                        Boolean updatebColor    = true;
                        Boolean updateSessionID = false;

                        String ownIDTemp = ownID.toString();
                        String recIDTemp = recID.toString();
                        int evaluation = compara(recIDTemp.compareTo(ownIDTemp));//recID.compareTo(ownID);
                        if( evaluation == 1 ) {// Si el recibido es mayor
                            customListener.setSessionID( recID );
                        } else if( evaluation == -1 ){ // Si el recibido es menor, debe actualizar
                            updateSessionID = true;
                        } else {
                            //customListener.setSessionID( ownID );
                            //no actualice
                            updateSessionID = false;
                        }

                        //<editor-fold defaultstate="collapsed" desc="Se pregunta quién es el servidor, responder...">
                        Log.d(Utilities.TAG, "::: RequestForBecomingServer recibido en el servidor :::");
                        // Si estamos actuando como servidor, regresamos solamente un objeto de tipo Boolean
                        if( SharedResources.ACT_AS_SERVER ) {
                            //Log.d(Utilities.TAG, "ANSWER SENT ---> I AM A SERVER, DO NOT ANYTHING");
                            JSONObject objx = new JSONObject( );
                            objx.put("@type", "Net_ResponseToRequestFromBecomingServer");
                            objx.put("List", true);
                            objx.put("Response", SharedResources.ACT_AS_SERVER);//boolean = true
                            objx.put("UpdatebColor", updatebColor);
                            objx.put("NewbColor", ownbColor[0]+":"+ownbColor[1]+":"+ownbColor[2]);
                            objx.put("UpdateID", updateSessionID);
                            objx.put("NewID", ownID);
                            String data = objx.toString( );

                            PrintStream output = new PrintStream(socket.getOutputStream());
                            //enviamos la respuesta
                            output.println(data);
                            Log.d(Utilities.TAG,"I AM A SERVER");
                        } else {
                            JSONObject objx = new JSONObject( );
                            objx.put("@type", "Net_ResponseToRequestFromBecomingServer");
                            objx.put("List", false);
                            objx.put("Response", hashMapToString(SharedResources.copy_GROUP_LIST()));//array = false
                            Log.d(Utilities.TAG, "Lista copy_GROUP_LIST:::" + SharedResources.copy_GROUP_LIST().toString());
                            objx.put("UpdatebColor", updatebColor);
                            objx.put("NewbColor", ownbColor[0]+":"+ownbColor[1]+":"+ownbColor[2]);
                            objx.put("UpdateID", updateSessionID);
                            objx.put("NewID", ownID);
                            String data = objx.toString( );

                            PrintStream output = new PrintStream(socket.getOutputStream());
                            //enviamos la respuesta al servidor
                            output.println(data);
                            Log.d(Utilities.TAG, "ANSWER SENT --> I DO NOT KNOW THE REQUEST, SHARING THE LIST");
                        }
                        socket.close();
                        //</editor-fold>

                    } else if (type.equals("UpdateList") ) {
                        bmensaje = false;
                        //<editor-fold defaultstate="collapsed" desc="Se recibió una lista, actualizar la propia...">
                        Log.d(Utilities.TAG, "LIST RECEIVED --> UPGRADING LIST");

                        String responce = newData_JSON.getString("UPLIST");
                        Map<String,HashMap<String, Object>> receivedGroupList = new HashMap<String, HashMap<String, Object>>();
                        // revisa si el mensaje tiene mas de 50 caracteres
                        if(responce.length() > 50){
                            receivedGroupList = stringToMap(responce);
                            // Revisar cada uno de los dispositivos recibidos en la nueva lista y actualizar la antigua
                            for (Map.Entry<String, HashMap<String, Object>> entry : receivedGroupList.entrySet()) {
                                String key = entry.getKey();
                                HashMap<String, Object> object = entry.getValue();
                                if( !SharedResources.GROUP_LIST.containsKey(key) ) {
                                    SharedResources.GROUP_LIST.put(key, object);
                                }
                            }
                            //Log.d(Utilities.TAG, "UpdateDistributionService:UpdateList::UPDATING LIST --> "+SharedResources.GROUP_LIST.toString());
                        }
                        //</editor-fold>

                    } else if (type.equals("Net_DeviceFeatures")){
                        bmensaje = false;
                        // NO IMPLEMENTADO
                        //<editor-fold defaultstate="collapsed" desc="Se rotó alguna pantalla, se actualizan sus specs...">
                        Log.d(Utilities.TAG, "Sucedio una rotación de pantalla en otro dispositivo");

                        JSONObject deviceFeatures = newData_JSON.getJSONObject("Response");
                        HashMap<String, Object> hmp = new HashMap<String, Object>();
                        hmp.put("WIDTH",  Integer.parseInt(deviceFeatures.get("WIDTH").toString()));
                        hmp.put("HEIGHT", Integer.parseInt(deviceFeatures.get("HEIGHT").toString()));
                        hmp.put("DEVICE", deviceFeatures.get("DEVICE").toString());
                        hmp.put("MODEL",  deviceFeatures.get("MODEL").toString());
                        String ipClient = clientSocket.getRemoteSocketAddress().toString();
                        Net_DeviceFeatures newFeatures = new Net_DeviceFeatures(hmp,ipClient);

                        Map<String, HashMap<String, Object>> grouplist_copy = SharedResources.copy_GROUP_LIST();
                        for (Map.Entry<String, HashMap<String, Object>> entry : grouplist_copy.entrySet()) {
                            String key = entry.getKey();
                            HashMap<String, Object> obj = entry.getValue();
                            if(key.equals( newFeatures.IP )){
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
                                SharedResources.GROUP_LIST.remove(key);

                                HashMap<String, Object> me = new HashMap<String, Object>();
                                me.put("TCP", String.valueOf( TCP ) );
                                me.put("UDP", String.valueOf( UDP ) );
                                me.put("SPECS", newFeatures.deviceFeatures.toString());
                                SharedResources.GROUP_LIST.put(key, me);
                                Log.d(Utilities.TAG, "Item actualizado: " + me.toString());
                                break;
                            }
                        }
                        Log.d(Utilities.TAG, "UpdateDistributionService:Net_DeviceFeatures::"+SharedResources.GROUP_LIST.toString());

                        //</editor-fold>
                    }
                    else if (type.equals("Net_ResponseToRequestFromBecomingServer")){//for ios
                        bmensaje = false;
                        net_ResponseToRequest(newData_JSON);
                    }
                    //Procesar el objeto (actualización recibida) dependerá de la lógica de la aplicación final.
                    if (bmensaje)// si no entro en alguno de los anteriores entra en este
                    listener.checkObjectReceived( newData_JSON, clientSocket.getInetAddress().getHostAddress() );
                }
                Log.d(Utilities.TAG, "UPDATEDISTRIBUTION --> STOOPED");
            }catch(Exception e){
                Log.d(Utilities.TAG, "UpdateDistributionService::UpdateDistributionServer: " + e.toString());
                Log.d(Utilities.TAG, ""+ e.getStackTrace()[2].getLineNumber());
            }
        }
        //</editor-fold>
    }

    public static void spreadObject( String obj, String protocol) {
        //<editor-fold defaultstate="collapsed" desc="Difundir un objeto a todos los integrantes de la sesión colaborativa">
        String ownIP = Utilities.getIPAddress(true);
        Map<String, HashMap<String, Object>> lista = SharedResources.copy_GROUP_LIST();
        for (Map.Entry<String, HashMap<String, Object>> entry : lista.entrySet()) {
            String key = entry.getKey();//is IP from HashMap
            HashMap<String, Object> item = entry.getValue();
            if( ownIP.equals( key ) ) continue;
            try{
                if( protocol.equals("TCP") )
                    new Thread( new WorkerThread(key, item, obj, true, false) ).start();
                else
                    new Thread( new WorkerThread(key, item, obj, false, false) ).start();
            }catch(Exception e){
                Log.d(Utilities.TAG, e.toString());
            }
        }
        //</editor-fold>
    }

    public static void sendObject(String key, HashMap<String, Object> item, String obj, String protocol, boolean waitForAnswer ) {
        //<editor-fold defaultstate="collapsed" desc="Enviar un objeto a un solo dispositivo">
        try{
            if( protocol.equals("TCP") ){
                //Log.d(Utilities.TAG, "SENDING OBJECT --> " + obj.toString());
                new Thread( new WorkerThread(key, item, obj, true, waitForAnswer) ).start();}
            else
                new Thread( new WorkerThread(key, item, obj, false, false) ).start();
            //esto no se cumple hasta que se envie algo que sea udp
        }catch(Exception e){
            Log.d(Utilities.TAG, "UpdateDistributionService::sendObject: " + e.toString());
        }
        //</editor-fold>
    }

    private static class WorkerThread implements Runnable{
        //<editor-fold defaultstate="collapsed" desc="Procesa una operación de envío de objetos">
        String                  ipTo;
        HashMap<String, Object> item;
        String                  obj;
        boolean                 isTCP;
        boolean                 waitForAnswer;

        public WorkerThread(String ipTo, HashMap<String, Object> item, String obj, boolean isTCP, boolean waitForAnswer ){
            this.ipTo           = ipTo;
            this.item           = item;
            this.obj            = obj;
            this.isTCP          = isTCP;
            this.waitForAnswer  = waitForAnswer;
        }

        public void run() {
            try{
                if(Utilities.DEBUG) {
                    //Log.d(Utilities.TAG, "WorkerThread::run()::Enviando a " + ipTo + ":" + item.get("TCP").toString() + "...");
                    Log.d(Utilities.TAG,"WORKERTHREAD SENDING ---> " + ipTo);
                }
                /*
                    Para enviar vía TCP
                */
                if( isTCP ) {
                    //<editor-fold defaultstate="collapsed" desc="Para enviar vía TCP">
                    Socket clientSocket = new Socket(ipTo, Integer.valueOf(item.get("TCP").toString()));
                    //clientSocket.setSoTimeout(3000);

                    PrintWriter outWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                    outWriter.println(obj);// envia al servidor

                    if( waitForAnswer) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(
                                clientSocket.getInputStream()));

                        String sg = in.readLine();

                        Log.d(Utilities.TAG, "WAITING MESSAGE --> "+sg);
                        if(sg != null){
                            sg = sg.replaceAll("[^\\p{Print}]", "");

                            JSONObject newData_JSON = new JSONObject(sg);
                            String type = newData_JSON.getString("@type");//get type message

                        /*
                            Si la respuesta del servidor es un objeto de tipo Net_ResponseToRequestFromBecomingServer,
                            entonces quiere decir que nos está respondiendo conforme al protocolo de acuerdo para
                            distribuir la lista de grupo.
                        */
                            if( type.equals("Net_ResponseToRequestFromBecomingServer") ) {
                                net_ResponseToRequest(newData_JSON);
                                //</editor-fold>
                            } else {
                            /*
                                Si la respuesta es de otro tipo, entonces la aplicación final deberá procesar dicha respuesta.
                            */
                            }
                        }
                    }

                    clientSocket.close();
                    //</editor-fold>
                }
                /*
                    Para enviar vía UDP
                */
                else {
                    //<editor-fold defaultstate="collapsed" desc="Para enviar vía UDP">
                    DatagramSocket clientSocket = new DatagramSocket();
                    clientSocket.setSoTimeout(3000);
                    byte[] sendData = obj.getBytes();

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                            InetAddress.getByName(item.get("IP").toString()), Integer.valueOf(item.get("UDP").toString()));
                    clientSocket.send(sendPacket);

                    clientSocket.close();
                    //</editor-fold>
                }
            }catch(Exception e){
                Log.d(Utilities.TAG, "UpdateDistributionService::WorkerThread: " + e.toString());
                StackTraceElement[] elements = e.getStackTrace();
                for (int iterator=1; iterator<=elements.length; iterator++){
                Log.d(Utilities.TAG, "UpdateDistributionService::WorkerThread: " + "Class Name:"+elements[iterator-1].getClassName()+" Method Name:"+elements[iterator-1].getMethodName()+" Line Number:"+elements[iterator-1].getLineNumber());
            }
            }
        }
        //</editor-fold>
    }

    //compara id recivido contra el nuestro
    private static int compara (int retval){
        int x = 0;
        if (retval < 0)
            x = -1;
        else if (retval == 0)
            x = 0;
        else if (retval > 0)
            x = 1;
        return x;
    }

    private static void net_ResponseToRequest(JSONObject jsonObject){

     /*
         Si regresa un Boolean entonces quiere decir que
         es servidor y no debemos hacer nada; si recibimos la lista, la propagaremos a todo
         el grupo de trabajo nuevo.
     */

      /* CÓDIGO EXTRA */
        try{
            Boolean updateList = Boolean.valueOf(jsonObject.getString("List"));
            String responce = jsonObject.getString("Response");//"Response":"[]"
            Map<String, HashMap<String, Object>> lhmp = new HashMap<String, HashMap<String, Object>>();

            if(!updateList){// si es falso tiene una lista
                if(responce.length() > 50){
                    lhmp = stringToMap(responce);// verificar que no se repita el mismo dispocitivo en el array
                    //Log.d(Utilities.TAG,"MESSAGE WAIT FOR ANSWER HSMP  ---> "+Arrays.asList(lhmp).toString());
                }else{
                    //String responce = newData_JSON.getString("Response");//"Response":"[]"
                }
            }

            Boolean updatebColor = Boolean.valueOf(jsonObject.getString("UpdatebColor"));
            String sbackColor = jsonObject.get("NewbColor").toString();
            String[] objbColor = sbackColor.split(":");
            int[] newbColor = new int[objbColor.length];
            newbColor[0] = Integer.parseInt(objbColor[0]);
            newbColor[1] = Integer.parseInt(objbColor[1]);
            newbColor[2] = Integer.parseInt(objbColor[2]);

            Boolean updateSession = Boolean.valueOf(jsonObject.getString("UpdateID"));
            UUID newID = UUID.fromString(jsonObject.getString("NewID"));

            if (updatebColor){
                customListener.setBackGroundColor(newbColor);
            }
            else if( updateSession ){
                customListener.setSessionID( newID );
            }
            // Actualizamos la lista posteriormente.
            Log.d(Utilities.TAG,"");//talvez un else if si este log

            if ( updateList ) {
                //<editor-fold defaultstate="collapsed" desc="Se recibió un booleano, no hacer nada...">
                Log.d( Utilities.TAG, "LIST NEED NOT BE DISTRIBUTED, THE OTHER DEVICE IS THE SERVER" );
                //</editor-fold>
            }
            else if( !updateList) {
                //<editor-fold defaultstate="collapsed" desc="Se recibió una lista, propagarla a los demás">
                try {
                    SharedResources.ACT_AS_SERVER = true;

                    // Actualizar la lista de grupo propia.
                    for (Map.Entry<String, HashMap<String, Object>> entry : lhmp.entrySet()) {
                        String key = entry.getKey();
                        HashMap<String, Object> object = entry.getValue();
                        if( !SharedResources.GROUP_LIST.containsKey(key) ) {
                            SharedResources.GROUP_LIST.put(key, object);
                        }
                    }
                    //Log.d(Utilities.TAG, "COMPLETED LIST WITH --> " + SharedResources.GROUP_LIST.size());

                    // Enviar la lista a cada uno de los dispositivos del nuevo grupo.
                    // El dispositivo se comporta como un servidor provisional.

                    String type_N = "UpdateList";
                    JSONObject objx = new JSONObject();
                    objx.put("@type", type_N);
                    objx.put("UPLIST", hashMapToString(SharedResources.copy_GROUP_LIST()));
                    spreadObject( objx.toString(), "TCP" );
                    Log.d( Utilities.TAG, "Net_ResponseToRequestFromBecomingServer" );
                } catch (Exception ex) {
                    Log.d(Utilities.TAG, "WorkerThread2:: " + ex.toString());
                }

            }
            // Hasta que se reciba y procese la respuesta del otro dispositivo después de preguntar quién
            // será el servidor, se desbloquea el seguro para realizar peticiones de acoplamiento nuevamente.
            //SharedResources.LOCKED_REQUESTS = false;

        }catch(Exception e){
            Log.d(Utilities.TAG, "UpdateDistributionService::WorkerThread2: " + e.toString());
            StackTraceElement[] elements = e.getStackTrace();
            for (int iterator=1; iterator<=elements.length; iterator++){
                Log.d(Utilities.TAG, "UpdateDistributionService::WorkerThread2: " + "Class Name:"+elements[iterator-1].getClassName()+" Method Name:"+elements[iterator-1].getMethodName()+" Line Number:"+elements[iterator-1].getLineNumber());
            }
        }
    }



    private static String hashMapToString(Map<String, HashMap<String, Object>> hsm){
        //<editor-fold defaultstate="collapsed" desc="Convertir un hasmap a string">
        String hsmtostring="";
        try{
            for (Map.Entry<String, HashMap<String, Object>> entry : hsm.entrySet()) {

                String key = entry.getKey();
                HashMap<String, Object> obj = entry.getValue();

                String IP  = key;
                JSONObject objx = new JSONObject();
                try{
                    objx.put("IP", IP);
                    objx.put("TCP", obj.get("TCP").toString());
                    objx.put("UDP", obj.get("UDP").toString());
                    HashMap<String, Object>  specs = (HashMap<String, Object>)obj.get("SPECS");
                    Log.d(Utilities.TAG,"UpdateDistributionService:hashMaptoString "+ specs.toString());
                    objx.put("HEIGHT", specs.get("HEIGHT").toString());
                    objx.put("DEVICE", specs.get("DEVICE").toString());
                    objx.put("MODEL", specs.get("MODEL").toString());
                    objx.put("WIDTH", specs.get("WIDTH").toString());

                    hsmtostring += objx.toString()+"#";

                } catch (JSONException ex) {
                    Log.d(Utilities.TAG,"UpdateDistributionService:hashMaptoString "+ ex);
                    //StackTraceElement[] elements = ex.getStackTrace();
                    //for (int iterator=1; iterator<=elements.length; iterator++){
                    //    Log.d(Utilities.TAG, "UpdateDistributionService::stringToMap: " + "Class Name:"+elements[iterator-1].getClassName()+" Method Name:"+elements[iterator-1].getMethodName()+" Line Number:"+elements[iterator-1].getLineNumber());
                    // }
                }
            }
        }catch(Exception e){
            Log.d(Utilities.TAG,"UpdateDistributionService:hashMaptoString "+ e);
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

    private static Map<String, HashMap<String, Object>> stringToMap(String str){

        Map<String, HashMap<String, Object>> hmp = new HashMap<String, HashMap<String, Object>>();
        String[] results = str.split("(?<=\\})#(?=\\{)");
        for (int i=0; i<results.length; i++){
            results[i] = toJson(results[i]);
            //results[i] = results[i].substring(0, results[i].length()).replace("\":\\{\"","\"=\\[\\{\"");
            //results[i] = results[i].substring(0, results[i].length()).replace("\"\\},","\"\\}\\],");
            //results[i] = results[i].substring(0, results[i].length()).replace("\"\\}\\}","\"\\}\\]\\}");
            //results[i] = results[i].substring(0, results[i].length()).replace(":","=");
            //Log.d(Utilities.TAG,"RESULTADOs:stringToMap:"+results[i]);//imprime los dos o mas string
        }

        for (int i=0; i<results.length; i++){
            try{
                JSONObject jsonObj = new JSONObject(results[i]);
                HashMap<String, Object> hmpspecs = new HashMap<String, Object>();
                hmpspecs.put("DEVICE", jsonObj.getString("DEVICE"));
                hmpspecs.put("MODEL", jsonObj.getString("MODEL"));
                hmpspecs.put("WIDTH", jsonObj.getString("WIDTH"));
                hmpspecs.put("HEIGHT", jsonObj.getString("HEIGHT"));
                HashMap<String, Object> hmpt = new HashMap<String, Object>();
                hmpt.put("UDP", jsonObj.getString("UDP"));
                hmpt.put("TCP", jsonObj.getString("TCP"));
                hmpt.put("SPECS", hmpspecs);// DEVICE and MODEL others WIDTH and HEIGHT
                hmp.put(jsonObj.getString("IP"), hmpt);
            } catch (JSONException ex) {
                Log.d(Utilities.TAG,"UpdateDistributionService:stringToMap "+ ex);
                //StackTraceElement[] elements = ex.getStackTrace();
                //for (int iterator=1; iterator<=elements.length; iterator++){
                //    Log.d(Utilities.TAG, "UpdateDistributionService::stringToMap: " + "Class Name:"+elements[iterator-1].getClassName()+" Method Name:"+elements[iterator-1].getMethodName()+" Line Number:"+elements[iterator-1].getLineNumber());
               // }
            }
        }
        return hmp;
    }
    private static String toJson(String s) {
        s = s.substring(0, s.length()).replace(":","=");//agregado
        s = s.substring(0, s.length()).replace("\"","");//agregado
        s = s.substring(0, s.length()).replace("{", "{\"");
        s = s.substring(0, s.length()).replace("}", "\"}");
        s = s.substring(0, s.length()).replace(",", "\",\"");//agregado
        s = s.substring(0, s.length()).replace(", ", "\",\"");
        s = s.substring(0, s.length()).replace("={", ":{");//agregado
        s = s.substring(0, s.length()).replace("=", "\":\"");
        s = s.substring(0, s.length()).replace("\"[", "[");// este ya no servira
        s = s.substring(0, s.length()).replace("]\"", "]");// este ya no servira
        s = s.substring(0, s.length()).replace("}\",\"{", "},{");// este ya no servira
        s = s.substring(0, s.length()).replace("\"\"","\"");
        s = s.substring(0, s.length()).replace("\"}\"", "\"}");//agregado// este ya no servira
        return s;
    }
    private static String toJsontwo(String s) {
        s = s.substring(0, s.length()).replace("{", "{\"");
        s = s.substring(0, s.length()).replace("}", "\"}");
        s = s.substring(0, s.length()).replace(", ", "\", \"");
        s = s.substring(0, s.length()).replace("=", "\":\"");
        s = s.substring(0, s.length()).replace("\"[", "[");
        s = s.substring(0, s.length()).replace("]\"", "]");
        s = s.substring(0, s.length()).replace("}\", \"{", "}, {");
        return s;
    }
    private int getTimeSystem(int opc, String time){
        int timeOne = 0;
        if (opc == 0){//own system
            String getTimeOwn = SharedResources.SYSTEM_DATE;
            //Log.d( Utilities.TAG, "onStop()1: " + SharedResources.SYSTEM_DATE );
            String[] objTimeOwn = getTimeOwn.split(":");
            final int[] timeOwn = new int[objTimeOwn.length];//array color rgb received
            timeOwn[0] = Integer.parseInt(objTimeOwn[0]);//HH
            timeOwn[1] = Integer.parseInt(objTimeOwn[1]);//mm
            timeOwn[2] = Integer.parseInt(objTimeOwn[2]);//ss

            int timeOneHour = timeOwn[0] * 60 * 60;
            int timeOneMin = timeOwn[1] * 60;
            timeOne = timeOneHour + timeOneMin + timeOwn[2];

        }else {// received time
            String getTimeOwn = time;
            //Log.d( Utilities.TAG, "onStop()2: " + time );
            String[] objTimeOwn = getTimeOwn.split(":");
            final int[] timeOwn = new int[objTimeOwn.length];//array color rgb received
            timeOwn[0] = Integer.parseInt(objTimeOwn[0]);//HH
            timeOwn[1] = Integer.parseInt(objTimeOwn[1]);//mm
            timeOwn[2] = Integer.parseInt(objTimeOwn[2]);//ss

            int timeOneHour = timeOwn[0] * 60 * 60;
            int timeOneMin = timeOwn[1] * 60;
            timeOne = timeOneHour + timeOneMin + timeOwn[2];

        }
        return timeOne;
    }

}
