package com.cinvestav.tesis;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;

import java.util.Random;
import java.util.UUID;

/**
 * Created by
 */
public class AppSettings {
    private String TAG;
    private int[] iDbSettings;// id for buttons settings
    private int[] iDbGestures;// id for buttons gestures
    private int iDUUID;// id for iDSession
    private int[] iDConnectedForTablet;//id for devices connected
    private int[] iDConnectedForPhone;//id for devices connected
    private int idBlocks;// id for blocks of views
    private UUID iDSession;//iD for Session
    //private int[] colorConnectedForTablet;//colors for devices connected
    private int[] colorConnected;
    private int idConnectedImage;
    //private int[] colorConnectedForPhone;//colors for devices connected
    private int[] BColor;//BackGroudColor
    private GradientDrawable border;

    public AppSettings(){
        iDbSettings = new int[]{0,1,2,3};
        iDbGestures = new int[]{4,5};
        iDUUID = 6;
        iDConnectedForTablet = new int[]{7,8,9,10,11,12};//A1,A2,B1,B2,C1,C2
        iDConnectedForPhone = new int[]{7,8,9,10};//A1,B1,C1,C2
        idConnectedImage = 13;
        idBlocks = 14;//start in
        border = new GradientDrawable();
        iDSession= UUID.fromString(UUID.randomUUID().toString().toUpperCase());
        //colorConnectedForTablet = new int[]{Color.GREEN,Color.BLUE,Color.RED,Color.YELLOW,Color.MAGENTA,Color.CYAN};
        //colorConnectedForPhone = new int[]{Color.GREEN,Color.RED,Color.MAGENTA,Color.CYAN};
        colorConnected = new int[]{Color.GREEN,Color.BLUE,Color.RED,Color.YELLOW,Color.MAGENTA,Color.CYAN};
        //BColor = new SSColor().randomFlatColor();;
        TAG = "workTogether";
    }
    // Upercase
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


    public void setiDBlocks(int i){
        idBlocks = i;
    }
    public void setBColor(int[] bc){
        BColor = bc;
    }
    public void setiD(UUID id){
        iDSession = id;//UUID.fromString(id);
    }


    public int[] getiDSettings(){
        return iDbSettings;
    }
    public int[] getiDbGestures(){
        return iDbGestures;
    }
    public int getiDUUID(){
        return iDUUID;
    }
    public int[] getiDConnectedForTablet(){
        return iDConnectedForTablet;
    }
    public int[] getiDConnectedForPhone(){
        return iDConnectedForPhone;
    }
    public int[] getColorConnected(){
        return colorConnected;
    }
    public int getIdConnectedImage(){
        return idConnectedImage;
    }
    public int getIdBlocks(){
        return idBlocks;
    }
    /*public GradientDrawable getBorder (){

        BColor = new SSColor().randomFlatColor();
        border.setColor(Color.HSVToColor(BColor)); //background Color random
        border.setStroke(25, Color.rgb(255, 230, 255));// add border color

        return border;
    }
    // este es cuando se recibe un nuevo fondo
    public GradientDrawable getNewBorder (){
        border.setColor(Color.HSVToColor(BColor)); //background Color
        border.setStroke(25, Color.rgb(255, 230, 255));// add border color
        return border;
    }
*/
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model.replaceAll("\\s+","_"));
        } else {
            return capitalize(manufacturer) + "_" + model.replaceAll("\\s+","_");//return capitalize(manufacturer) + " " + model;
        }
    }
    public String getTAG(){
        return TAG;
    }

    public UUID getiDSession(){
        return iDSession;
    }

    public int[] getBColor(){
        return BColor;
    }
    public int[] getBNewColor(){
        Random rnd = new Random();
        int[] colors = new int[3];
        colors[0] = rnd.nextInt(256);
        colors[1] = rnd.nextInt(256);
        colors[2] = rnd.nextInt(256);
        BColor = colors;
        //BColor = new SSColor().randomFlatColor();
        return  BColor;

    }

}

