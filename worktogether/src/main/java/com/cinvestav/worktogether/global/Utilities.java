package com.cinvestav.worktogether.global;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.apache.http.conn.util.InetAddressUtils;

/**
 *
 * @author 
 */
public abstract class Utilities {
    // DEBUG en true habilita los logs del soporte
    public static final boolean DEBUG   = true;
    public static final String TAG      = "workTogether";
    public static final float  volume   = 1.0f;
    private static final int minValuePort   = 60001;//49152
    private static final int maxValuePort   = 65000;//65535

    public static String getIPAddress( boolean useIPv4 ) {
        //<editor-fold defaultstate="collapsed" desc="getIPAddress(useIPv4?)">
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (SocketException ex) { } // for now eat exceptions
        return "";
        //</editor-fold>
    }

    private static InetAddress getInetAddress() {
        //<editor-fold defaultstate="collapsed" desc="Utilizada por getBroadcastAddress()">
        try{
            InetAddress inetAddress;
            InetAddress myAddr = null;

            for (Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces(); networkInterface.hasMoreElements();) {

                NetworkInterface singleInterface = networkInterface.nextElement();

                for (Enumeration<InetAddress> IpAddresses = singleInterface.getInetAddresses(); IpAddresses.hasMoreElements();) {
                    inetAddress = IpAddresses.nextElement();

                    if (!inetAddress.isLoopbackAddress() && (singleInterface.getDisplayName().contains("wlan0")
                            || singleInterface.getDisplayName().contains("eth0"))) {

                        myAddr = inetAddress;
                    }
                }
            }
            return myAddr;

        } catch (SocketException ex) {
        }
        return null;
        //</editor-fold>
    }

    public static InetAddress getBroadcastAddress() {
        //<editor-fold defaultstate="collapsed" desc="getBroadcastAddress()">
        try{
            NetworkInterface temp;
            InetAddress iAddr = null;
            temp = NetworkInterface.getByInetAddress( getInetAddress() );
            List<InterfaceAddress> addresses = temp.getInterfaceAddresses();
            for (InterfaceAddress inetAddress : addresses) {
                iAddr = inetAddress.getBroadcast();
            }
            return iAddr;
        } catch (SocketException e) {
            return null;
        }
        //</editor-fold>
    }

    public static int getOneFreePort(){
        //<editor-fold defaultstate="collapsed" desc="Obtener un puerto disponible">
        int port = 0;
        boolean one = true;
        while(one){
            try{
                int portOne = getPortRandom(minValuePort,maxValuePort);
                new ServerSocket(portOne).close();
                port = portOne;
                if(port != 0)
                    one = false;
            }catch(IOException e){port = 0;}
        }
        return port;
        //</editor-fold>
    }

    public static int[] getTwoFreePorts(){
        //<editor-fold defaultstate="collapsed" desc="Obtener dos puertos disponibles">
        int ports[] = {0, 0};
        boolean one = true, two = true;
        while (one){
            try{
                int portOne = getPortRandom(minValuePort,maxValuePort);
                new ServerSocket(portOne).close();
                ports[0] = portOne;
                if (ports[0] != 0)
                    one = false;
            }catch(IOException e){ports[0] = 0;}
        }
        while(two){
            try{
                int portTwo = getPortRandom(minValuePort,maxValuePort);
                new ServerSocket(portTwo).close();
                ports[1] = portTwo;
                if (ports[0] != 0)
                    two = false;
            }catch(IOException e){ports[1] = 0;}
        }

        if(ports[0] == ports[1])
            while (two){
                try{
                    int portOne = getPortRandom(minValuePort,maxValuePort);
                    new ServerSocket(portOne).close();
                    ports[0] = portOne;
                    if (ports[0] != ports[1] && ports[0] != 0 )
                        two = false;
                }catch(IOException e){ports[0] = 0;}
            }
        return ports;
        //</editor-fold>
    }

    public static void enableScreenRotation( Activity MainActivity, boolean rotationEnabled ){
        //<editor-fold defaultstate="collapsed" desc="Habilitar o deshabilitar la rotación de la pantalla">
        //Settings.System.putInt( ctx.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, rotationEnabled ? 1 : 0);
        if( !rotationEnabled ) {
            int rotation = MainActivity.getWindowManager().getDefaultDisplay().getRotation();
            int orientation = getScreenOrientation(rotation, MainActivity);
            MainActivity.setRequestedOrientation(orientation);
        } else {
            MainActivity.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED );
        }
        //</editor-fold>
    }

    private static int getScreenOrientation(int default_display_rotation, Activity MainActivity) {
        //<editor-fold defaultstate="collapsed" desc="Obtener la orientación del dispositivo de tipo ActivityInfo.X">
        DisplayMetrics dm = new DisplayMetrics();
        MainActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // Si la orientación natural es Portrait
        if ((default_display_rotation == Surface.ROTATION_0 || default_display_rotation == Surface.ROTATION_180) && height > width
                || (default_display_rotation == Surface.ROTATION_90 || default_display_rotation == Surface.ROTATION_270) && width > height) {
            switch (default_display_rotation) {
                case Surface.ROTATION_0:
                    return (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                case Surface.ROTATION_90:
                    return (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                case Surface.ROTATION_180:
                    return (ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                case Surface.ROTATION_270:
                    return (ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                default:
                    Log.e(Utilities.TAG, "Unknown screen orientation. Defaulting to portrait.");
                    return (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } // caso contrario, si es landscape
        else {
            switch (default_display_rotation) {
                case Surface.ROTATION_0:
                    return (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                case Surface.ROTATION_90:
                    return (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                case Surface.ROTATION_180:
                    return (ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                case Surface.ROTATION_270:
                    return (ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                default:
                    Log.e(Utilities.TAG, "Unknown screen orientation. Defaulting to landscape.");
                    return (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
        //</editor-fold>
    }

    public static int getRotation( Activity MainActivity ){
        //<editor-fold defaultstate="collapsed" desc="Obtener la rotación de la pantalla por defecto">
        return MainActivity.getWindowManager().getDefaultDisplay().getRotation();
        //</editor-fold>
    }

    // No utilizada debido a que requiere un contexto de la aplicación
    /*public static InetAddress getBroadcastAddress( Context ctx ){
        //<editor-fold defaultstate="collapsed" desc="getBroadcastAddress">
        try{
            WifiManager myWifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
            DhcpInfo myDhcpInfo = myWifiManager.getDhcpInfo();
            if (myDhcpInfo == null) {
                return null;
            }
            int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask) | ~myDhcpInfo.netmask;
            byte[] quads = new byte[4];
            for (int k = 0; k < 4; k++) {
                quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
            }
            return InetAddress.getByAddress(quads);
        }catch(UnknownHostException e){
            return null;
        }
        //</editor-fold>
    }*/

    public static int getPortRandom(int minValue, int maxValue){
        //<editor-fold defaultstate="collapsed" desc="genera un valor aleatorio">
        int range =  (maxValue - minValue) + 1;
        int randomValue = (int)(Math.random() * range) + minValue;
        return randomValue;
    }
    //</editor-fold>
}