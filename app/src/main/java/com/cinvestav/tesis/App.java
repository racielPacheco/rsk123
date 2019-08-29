package com.cinvestav.tesis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.cinvestav.worktogether.global.SharedResources;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by
 */
public class App extends View {

    private Context context;
    private float xCoOrdinate, yCoOrdinate;//determina x,y de los bloques se puede poner local
    private RelativeLayout relativeLayout;//relative layaout
    private RelativeLayout.LayoutParams rlp;//parametros

    private ExampleWorkTogether exampleWorkTogether;

    private AppSettings aps;

    private Map<Integer, HashMap<String, Object>> arrayBlock;//array with the characteristics

    public App(Context context) {
        super(context);
        this.context = context;

        aps = new AppSettings();
         /*
             id 0 es del button hide
             id 1 es del button settings
             id 2 es del button add blocks
             id 3 es del button exit
             id 4 en adelante es para los bloques
         */
        relativeLayout = new RelativeLayout(this.context);
        int[] ncolor = aps.getBNewColor();
        int r = (int) ncolor[0];
        int g = (int) ncolor[1];
        int b = (int) ncolor[2];
        relativeLayout.setBackgroundColor(Color.argb(230,r,g,b));// sin borde
        //relativeLayout.setBackground(aps.getBorder());// con borde


        rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        //List<HashMap<Integer,Object>> arrayBlock = new ArrayList<>();
        arrayBlock = new HashMap<>();




        // esto va en una funcion que sera llamada cuando llegue una peticion
        // de algun dispocitivo
        //tambien podra servir cuando se quiera recuperar el log de los dispocitivos
        //get all keys
        //Set keyset=map.keySet();
        //Set<String> keys = hm.keySet();

        /*for (Map.Entry<Integer, HashMap<String, Object>> entry : arrayBlock.entrySet()){
            Integer keyiD = entry.getKey();
            HashMap<String, Object> hm = entry.getValue();
            Integer XP = ((Integer)hm.get("X")).intValue();
            Integer YP = ((Integer)hm.get("Y")).intValue();
            String TEXT = hm.get("TEXT").toString().toUpperCase();
            String TEMPCOLOR = hm.get("COLOR").toString();
            String[] elements = TEMPCOLOR.split(":");
            int[] RGBCOLOR = new int[elements.length];
            for(int i=0;i < elements.length;++i)
                RGBCOLOR[i] = Integer.parseInt(elements[i]);

            addBlockParamsExtern(keyiD,XP,YP,TEXT,RGBCOLOR);

        }*/


        setMenu();//set menu
        barConnected();
        setID();//set UUID
    }

    private void barConnected(){

        DisplayMetrics displaymetricst = new DisplayMetrics();
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(displaymetricst);
        int widtht = displaymetricst.widthPixels;
        int heigh = displaymetricst.heightPixels;
        /*
            A1 = 0 = Green
            A2 = 1 = Blue
            B1 = 2 = Red
            B2 = 3 = Yellow
            C1 = 4 = Magenta
            C2 = 5 = Cyan
         */
        if (isTabletorPhone()){

            int[] PA1 = new int[]{widtht/6,0};
            int[] PA2 = new int[]{widtht/2 + widtht/6,0};
            int[] PB1 = new int[]{widtht/6,heigh-25};
            int[] PB2 = new int[]{widtht/2 + widtht/6,heigh-25};
            int[] PC1 = new int[]{0,heigh/4};
            int[] PC2 = new int[]{widtht-25,heigh/4};

            int[] A1 = new int[]{widtht/4,22};
            int[] A2 = new int[]{widtht/4,22};
            int[] B1 = new int[]{widtht/4,22};
            int[] B2 = new int[]{widtht/4,22};
            int[] C1 = new int[]{22,heigh/2};
            int[] C2 = new int[]{22,heigh/2};

            //int[] colores = aps.getColorConnectedForTablet();
            //int[] cbb = aps.getColorConnected();
            int[] iDConnected = aps.getiDConnectedForTablet();
            int[] bitmapx = new int[]{A1[0],A2[0],B1[0],B2[0],C1[0],C2[0]};
            int[] bitmapy = new int[]{A1[1],A2[1],B1[1],B2[1],C1[1],C2[1]};
            int[] marginsx = new int[]{PA1[0],PA2[0],PB1[0],PB2[0],PC1[0],PC2[0]};
            int[] marginsy = new int[]{PA1[1],PA2[1],PB1[1],PB2[1],PC1[1],PC2[1]};

            for (int i=0; i<iDConnected.length;++i){

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

                ImageView image = new ImageView(context);

                Bitmap bitmap = Bitmap.createBitmap(bitmapx[i], bitmapy[i], Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(Color.TRANSPARENT);
                bitmap = addBorderToBitmap(bitmap, 3, Color.TRANSPARENT);
                //bitmap.eraseColor(cbb[i]);
                //bitmap = addBorderToBitmap(bitmap, 3, Color.GRAY);
                image.setImageBitmap(bitmap);
                lp.setMargins(marginsx[i], marginsy[i], 0, 0);
                image.setLayoutParams(lp);
                image.setId(iDConnected[i]);
                relativeLayout.addView(image);
                ((Activity) this.context).setContentView(relativeLayout, rlp);
            }
        }else {

            int[] PA1 = new int[]{widtht/4,0};
            int[] PB1 = new int[]{widtht/4,heigh-25};
            int[] PC1 = new int[]{0,heigh/4};
            int[] PC2 = new int[]{widtht-25,heigh/4};

            int[] A1 = new int[]{widtht/2,22};
            int[] B1 = new int[]{widtht/2,22};
            int[] C1 = new int[]{22,heigh/2};
            int[] C2 = new int[]{22,heigh/2};

            //int[] cbb = aps.getColorConnected();
            int[] iDConnected = aps.getiDConnectedForPhone();
            int[] bitmapx = new int[]{A1[0], B1[0], C1[0], C2[0]};
            int[] bitmapy = new int[]{A1[1], B1[1], C1[1], C2[1]};
            int[] marginsx = new int[]{PA1[0], PB1[0], PC1[0], PC2[0]};
            int[] marginsy = new int[]{PA1[1], PB1[1], PC1[1], PC2[1]};

            for (int i = 0; i < iDConnected.length; ++i) {

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

                ImageView image = new ImageView(context);

                Bitmap bitmap = Bitmap.createBitmap(bitmapx[i], bitmapy[i], Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(Color.TRANSPARENT);
                bitmap = addBorderToBitmap(bitmap, 3, Color.TRANSPARENT);
                //bitmap.eraseColor(cbb[i]);
                //bitmap = addBorderToBitmap(bitmap, 3, Color.GRAY);
                image.setImageBitmap(bitmap);
                lp.setMargins(marginsx[i], marginsy[i], 0, 0);
                image.setLayoutParams(lp);
                image.setId(iDConnected[i]);
                relativeLayout.addView(image);
                ((Activity) this.context).setContentView(relativeLayout, rlp);
            }
        }



    }

    private void OpenCategroyDialogBox() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this.context);
        alert.setTitle("Agregar Bloque");
        alert.setMessage("Ingresar un texto:");

        // Use an EditText view to get user input.
        final EditText input = new EditText(this.context);
        input.setHint("Maximo de 10 caracteres");
        input.setTextColor(Color.BLACK);
        input.setSingleLine(true);// remove enter button from keyboard
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        //input.setId(1000);
        alert.setView(input);

        alert.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                Log.d("color", "Texto: " + value);
                if (value.length() >= 2)
                    addBlockParams(value.toUpperCase());
                else
                    addBlockParams("Minimo 2 caracteres".toUpperCase());
                return;
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        // create an alert dialog
        AlertDialog alert1 = alert.create();

        alert1.show();

    }

    public void addBlockParams(String text){

        DisplayMetrics displaymetricst = new DisplayMetrics();
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(displaymetricst);//outside activity
        int widtht = displaymetricst.widthPixels;

        //agregar una funcion que regrese un numero random
        Random r = new Random();
        int Low = 0;
        int High = 255;
        int ResultR = r.nextInt(High-Low) + Low;
        Random g = new Random();
        int ResultG = g.nextInt(High-Low) + Low;
        Random b = new Random();
        int ResultB = b.nextInt(High-Low) + Low;

        int[] colorArray = new int[4];
        colorArray[0] = 153;//alpha
        colorArray[1] = ResultR;//153;//red
        colorArray[2] = ResultG;//255;//green
        colorArray[3] = ResultB;//102;//blue

        Paint paint = new Paint();
        Rect bounds = new Rect();

        int text_height = 0;
        int text_width = 0;

        paint.setTypeface(Typeface.DEFAULT);// your preference here
        if (widtht <= 480)
            paint.setTextSize(48);// have this the same as your text size
        else
            paint.setTextSize(54);
        //String text = "WORK";

        paint.getTextBounds(text, 0, text.length(), bounds);

        text_height =  bounds.height();
        text_width =  bounds.width();

        // el 0.75 es el factor para que elimine un poco lo largo
        Bitmap bmp = Bitmap.createBitmap((int)(text_width * 0.75), text_height,  Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.argb(colorArray[0],colorArray[1],colorArray[2],colorArray[3]));//Color.RED);
        //bmp = getRoundedCornerBitmap(bmp,6);//bordes redondos
        bmp = addBorderToBitmap(bmp, 6, Color.argb(255, colorArray[1], colorArray[2], colorArray[3]));
        bmp = drawTextToBitmap(text, text_height, bmp);
        ImageView mImage = new ImageView(this.context);
        mImage.setImageBitmap(bmp);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);//outside activity
        //getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);//inside activity
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        //set the position of block
        //(widthScreenX / 2) - (widthBlock / 2)
        //(heightScreen / 2) - (heightBlock / 2)
        lp.setMargins((width / 2) - ((int) (text_width * 0.75) / 2), (height / 2) - (text_height / 2), 0, 0);

        // Setting the parameters on the TextView
        mImage.setLayoutParams(lp);

        //add gesture
        mImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                DisplayMetrics displaymetrics = new DisplayMetrics();
                ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int sH = displaymetrics.heightPixels;
                int sW = displaymetrics.widthPixels;
                float newX, newY;


                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        xCoOrdinate = view.getX() - event.getRawX();
                        yCoOrdinate = view.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        newX = event.getRawX() + xCoOrdinate;
                        newY = event.getRawY() + yCoOrdinate;

                        //Log.d(aps.getTAG(), "XP:" + newX);


                        if(true)//no sale de los limites de la pantalla
                            if ((newX <= 0 || newX >= sW-view.getWidth()) || (newY <= 0 || newY >= sH-view.getHeight()))
                                break;
                        //aqui van una serie de validaciones
                        //x- se habilita si hay algun device conectado
                        //x+ se habilita si hay algun device conectado
                        //y- y y- sera lo mismo
                        //tambien despues si una tablet se podran conectar dos devices
                        //ejemoplo
                        //tendra x1-,x2- y x1+ y x2+  ,,,, y y1- , y2-   y1+  y2+
                        //si se quiere solo se podra pasar el block de la mitad y la otra mitad si no esta conectado
                        //un dispositivo


                        view.setX(newX);// set new block position in x
                        view.setY(newY);// set new block position in y




                        //conectado de que derecha , izquierda , up o down ver despues
                        //aqui verifica si esta conectado con algun otro dispositivo
                        if (!SharedResources.GROUP_LIST.isEmpty()){
                            //aqui agregar el lado en que esta conectado,
                            String side = "";
                            Boolean isConected = false;
                            List<HashMap<String, String>> connected_copy = SharedResources.copy_CONNECTED_DEVICES();
                            HashMap<String, String> map_connected = connected_copy.get(0);
                            HashMap<String, Object> infoDevice = new HashMap<>();
                            String ipDevice = "";
                            if (isTabletorPhone()){
                                if (newX <= 25)
                                    side = "TC1";
                                else if (newX >= (sW-view.getWidth())-25)
                                    side = "TC2";
                                else if (newY <= 25 && newX <= sW/2)
                                    side = "TA1";
                                else if (newY <= 25 && newX >= sW/2)
                                    side = "TA2";
                                else if (newY >= (sH-view.getHeight())-25 && newX <= sW/2)
                                    side = "TB1";
                                else if (newY >= (sH-view.getHeight())-25 && newX >= sW/2)
                                    side = "TB2";

                            }else{
                                if (newX <= 25)
                                    side = "PC1";
                                else if (newX >= (sW-view.getWidth())-25)
                                    side = "PC2";
                                else if (newY <= 25)
                                    side = "PA1";
                                else if (newY >= (sH-view.getHeight())-25)
                                    side = "PB1";
                            }

                            for (Map.Entry<String,String> entry: map_connected.entrySet()){
                                if (entry.getKey().equals(side)){
                                    if (!entry.getValue().equals("")) {
                                        isConected = true;
                                        Map<String, HashMap<String, Object>> hm = SharedResources.GROUP_LIST;
                                        if (hm.containsKey(entry.getValue())){
                                            infoDevice = (HashMap<String, Object>) hm.get(entry.getValue());
                                            ipDevice = entry.getValue();
                                        }
                                    }
                                }
                            }
                            //Log.d(aps.getTAG(), "");

                            //if ((newX <= 25 || newX >= (sW-view.getWidth())-25) || (newY <= 25 || newY >= (sH-view.getHeight())-25)){
                            if (isConected){
                                //get block characteristics
                                for (Map.Entry<Integer, HashMap<String, Object>> entry : arrayBlock.entrySet()){
                                    Integer keyiD = entry.getKey();

                                    if (keyiD == view.getId()) {
                                        HashMap<String, Object> hm = entry.getValue();
                                        Integer XP = (int)(Math.round(newX));
                                        Integer YP = (int)(Math.round(newY));
                                        String TEXT = hm.get("TEXT").toString().toUpperCase();
                                        String TEMPCOLOR = hm.get("COLOR").toString();
                                        String[] elements = TEMPCOLOR.split(":");
                                        int[] RGBCOLOR = new int[elements.length];
                                        for (int i = 0; i < elements.length; ++i)
                                            RGBCOLOR[i] = Integer.parseInt(elements[i]);
                                            String data[] = new String[8];
                                            data[0] = "Net_Message";//type message
                                            data[1] = Integer.toString(XP);//x point
                                            data[2] = Integer.toString(YP);//y point
                                            data[3] = TEXT;//text
                                            data[4] = Integer.toString(RGBCOLOR[0]);//color red
                                            data[5] = Integer.toString(RGBCOLOR[1]);//color green
                                            data[6] = Integer.toString(RGBCOLOR[2]);//color blue
                                            data[7] = ipDevice;

                                            arrayBlock.remove(keyiD);//remove id from array blocks

                                            exampleWorkTogether.sendNetMessage(data,infoDevice,ipDevice);//send message

                                            //set block in SharedSources
                                            exampleWorkTogether.setObjectsState(arrayBlock);
                                        break;
                                    }
                                }
                                view.setVisibility(View.GONE);//delete from view
                                view.setEnabled(false);
                            }
                        }
                        break;
                    default:
                        return false;
                }
                return false;// false to enable setOnClickListener
            }
        });

        mImage.setOnClickListener(new DoubleClickListener() {

            @Override
            public void onSingleClick(View v) {
                Log.d(aps.getTAG(), "Un click Detectado");
            }

            @Override
            public void onDoubleClick(View v) {
                Log.d(aps.getTAG(), "doble click detectado");
                v.setVisibility(View.GONE);//delete from view
                v.setEnabled(false);
                arrayBlock.remove(v.getId());//remove id from array blocks

                //set block in SharedSources
                exampleWorkTogether.setObjectsState(arrayBlock);
            }
        });



        /// cunado se crea el bloque agrega a un array y sus caracteristicas para enviar
        HashMap<String, Object> meObject = new HashMap<>();
        meObject.put("X", new Integer(0));//tmp y se modifica en el listener
        meObject.put("Y", new Integer(0));//tmp y se modifica en el listener
        meObject.put("TEXT", new String(text));
        meObject.put("COLOR", new String(colorArray[1] + ":" + colorArray[2] + ":" + colorArray[3]));
        arrayBlock.put(aps.getIdBlocks(), meObject);
        //set block in SharedSources
        exampleWorkTogether.setObjectsState(arrayBlock);



        mImage.setId(aps.getIdBlocks());//seteamos el id que los identifica
        aps.setiDBlocks(aps.getIdBlocks() + 1);// id de los bloques


        // Adding the TextView to the RelativeLayout as a child
        relativeLayout.addView(mImage);

        // Setting the RelativeLayout as our content view
        //setContentView(relativeLayout, rlp);
        ((Activity) this.context).setContentView(relativeLayout, rlp);

    }

    /**
     * Adds external blocks that come from another device
     * @param iD this is the id for each block
     * @param xp this is the x point to set view
     * @param yp this is the y point to set view
     * @param text the string message
     * @param colors integer array that contains rgb colors information
     */
    public void addBlockParamsExtern(Integer iD, Integer xp, Integer yp, String text, int[] colors){

        DisplayMetrics displaymetricst = new DisplayMetrics();
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(displaymetricst);//outside activity
        final int widtht = displaymetricst.widthPixels;
        final int heigh = displaymetricst.heightPixels;

        //agregar una funcion que regrese un numero random
        Random r = new Random();
        final int Low = 0;
        int High = 255;
        int ResultR = r.nextInt(High-Low) + Low;
        Random g = new Random();
        int ResultG = g.nextInt(High-Low) + Low;
        Random b = new Random();
        int ResultB = b.nextInt(High-Low) + Low;

        int[] colorArray = new int[4];
        colorArray[0] = 153;//alpha
        colorArray[1] = colors[0]; //ResultR;//153;//red
        colorArray[2] = colors[1]; //ResultG;//255;//green
        colorArray[3] = colors[2]; //ResultB;//102;//blue

        Paint paint = new Paint();
        Rect bounds = new Rect();

        int text_height = 0;
        int text_width = 0;

        paint.setTypeface(Typeface.DEFAULT);// your preference here
        if (widtht <= 480 && heigh <= 820)
            paint.setTextSize(48);// have this the same as your text size
        else
            paint.setTextSize(54);
        //String text = "WORK";

        paint.getTextBounds(text, 0, text.length(), bounds);

        text_height =  bounds.height();
        text_width =  bounds.width();

        // el 0.75 es el factor para que elimine un poco lo largo
        Bitmap bmp = Bitmap.createBitmap((int)(text_width * 0.75), text_height,  Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.argb(colorArray[0],colorArray[1],colorArray[2],colorArray[3]));//Color.RED);
        //bmp = getRoundedCornerBitmap(bmp,6);//bordes redondos
        bmp = addBorderToBitmap(bmp, 6, Color.argb(255, colorArray[1], colorArray[2], colorArray[3]));
        bmp = drawTextToBitmap(text, text_height, bmp);
        ImageView mImage = new ImageView(this.context);
        mImage.setImageBitmap(bmp);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp.setMargins(xp - ((int) (text_width * 0.75) / 2), yp - (text_height / 2), 0, 0);

        // Setting the parameters on the TextView
        mImage.setLayoutParams(lp);

        //add gesture

        mImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                DisplayMetrics displaymetrics = new DisplayMetrics();
                ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int sH = displaymetrics.heightPixels;
                int sW = displaymetrics.widthPixels;
                float newX, newY;

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        xCoOrdinate = view.getX() - event.getRawX();
                        yCoOrdinate = view.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        newX = event.getRawX() + xCoOrdinate;
                        newY = event.getRawY() + yCoOrdinate;

                        if(true)//no sale de los limites de la pantalla
                            if ((newX <= 0 || newX >= sW-view.getWidth()) || (newY <= 0 || newY >= sH-view.getHeight()))
                                break;
                        //aqui van una serie de validaciones
                        //x- se habilita si hay algun device conectado
                        //x+ se habilita si hay algun device conectado
                        //y- y y- sera lo mismo
                        //tambien despues si una tablet se podran conectar dos devices
                        //ejemoplo
                        //tendra x1-,x2- y x1+ y x2+  ,,,, y y1- , y2-   y1+  y2+
                        //si se quiere solo se podra pasar el block de la mitad y la otra mitad si no esta conectado
                        //un dispositivo

                        //view.animate().x(newX).y(newY).setDuration(0).start();
                        view.setX(newX);
                        view.setY(newY);



                        //conectado de que derecha , izquierda , up o down ver despues
                        //aquie esta en general con conectado
                        if (!SharedResources.GROUP_LIST.isEmpty()){
                            //aqui agregar el lado en que esta conectado,
                            String side = "";
                            Boolean isConected = false;
                            List<HashMap<String, String>> connected_copy = SharedResources.copy_CONNECTED_DEVICES();
                            HashMap<String, String> map_connected = connected_copy.get(0);
                            HashMap<String, Object> infoDevice = new HashMap<>();
                            String ipDevice = "";
                            if (isTabletorPhone()){
                                if (newX <= 25)
                                    side = "TC1";
                                else if (newX >= (sW-view.getWidth())-25)
                                    side = "TC2";
                                else if (newY <= 25 && newX <= sW/2)
                                    side = "TA1";
                                else if (newY <= 25 && newX >= sW/2)
                                    side = "TA2";
                                else if (newY >= (sH-view.getHeight())-25 && newX <= sW/2)
                                    side = "TB1";
                                else if (newY >= (sH-view.getHeight())-25 && newX >= sW/2)
                                    side = "TB2";

                            }else{
                                if (newX <= 25)
                                    side = "PC1";
                                else if (newX >= (sW-view.getWidth())-25)
                                    side = "PC2";
                                else if (newY <= 25)
                                    side = "PA1";
                                else if (newY >= (sH-view.getHeight())-25)
                                    side = "PB1";
                            }

                            for (Map.Entry<String,String> entry: map_connected.entrySet()){
                                if (entry.getKey().equals(side)){
                                    if (!entry.getValue().equals("")) {
                                        isConected = true;
                                        Map<String, HashMap<String, Object>> hm = SharedResources.GROUP_LIST;
                                        if (hm.containsKey(entry.getValue())){
                                            infoDevice = (HashMap<String, Object>) hm.get(entry.getValue());
                                            ipDevice = entry.getValue();
                                        }
                                    }
                                }
                            }

                            //if ((newX <= 25 || newX >= (sW-view.getWidth())-25) || (newY <= 25 || newY >= (sH-view.getHeight())-25)){
                            if (isConected){
                                //get block characteristics
                                for (Map.Entry<Integer, HashMap<String, Object>> entry : arrayBlock.entrySet()){
                                    Integer keyiD = entry.getKey();

                                    if (keyiD == view.getId()) {
                                        HashMap<String, Object> hm = entry.getValue();
                                        Integer XP = (int)(Math.round(newX));
                                        Integer YP = (int)(Math.round(newY));
                                        String TEXT = hm.get("TEXT").toString().toUpperCase();
                                        String TEMPCOLOR = hm.get("COLOR").toString();
                                        String[] elements = TEMPCOLOR.split(":");
                                        int[] RGBCOLOR = new int[elements.length];
                                        for (int i = 0; i < elements.length; ++i)
                                            RGBCOLOR[i] = Integer.parseInt(elements[i]);
                                        String data[] = new String[8];
                                        data[0] = "Net_Message";//type message
                                        data[1] = Integer.toString(XP);//x point
                                        data[2] = Integer.toString(YP);//y point
                                        data[3] = TEXT;//text
                                        data[4] = Integer.toString(RGBCOLOR[0]);//color red
                                        data[5] = Integer.toString(RGBCOLOR[1]);//color green
                                        data[6] = Integer.toString(RGBCOLOR[2]);//color blue
                                        data[7] = ipDevice;

                                        arrayBlock.remove(keyiD);//remove id from array blocks

                                        exampleWorkTogether.sendNetMessage(data,infoDevice,ipDevice);//send message
                                        //set block in SharedSources
                                        exampleWorkTogether.setObjectsState(arrayBlock);

                                        break;
                                    }
                                }
                                view.setVisibility(View.GONE);//delete view
                                view.setEnabled(false);
                            }
                        }
                        break;
                    default:
                        return false;
                }
                return false;// false to enable setOnClickListener
            }
        });

        mImage.setOnClickListener(new DoubleClickListener() {

            @Override
            public void onSingleClick(View v) {
                Log.d("color", "uno");
            }

            @Override
            public void onDoubleClick(View v) {
                //Log.d(aps.getTAG(), "doble");
                //Log.d(aps.getTAG(), "idIMAGENFUE:"+v.getId());
                v.setVisibility(View.GONE);
            }
        });

        /// agrega a un array y sus caracteristicas para enviar
        HashMap<String, Object> meObject = new HashMap<>();
        meObject.put("X", new Integer(0));//tmp y se modifica en el listener
        meObject.put("Y", new Integer(0));//tmp y se modifica en el listener
        meObject.put("TEXT", new String(text));
        meObject.put("COLOR", new String(colorArray[1] + ":" + colorArray[2] + ":" + colorArray[3]));
        arrayBlock.put(aps.getIdBlocks(), meObject);
        //set block in SharedSources
        exampleWorkTogether.setObjectsState(arrayBlock);

        mImage.setId(aps.getIdBlocks());//seteamos el id que los identifica
        aps.setiDBlocks(aps.getIdBlocks() + 1);// id de los bloques
        // Adding the TextView to the RelativeLayout as a child
        relativeLayout.addView(mImage);

        // Setting the RelativeLayout as our content view
        //setContentView(relativeLayout, rlp);
        ((Activity) this.context).setContentView(relativeLayout, rlp);

    }

    /**
     * This function make corner into a bitmap
     * @param bitmap image in format bitmap
     * @param pixels size of border
     * @return new image bitmap with corner
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * This function make a border into a existing bitmap
     * @param srcBitmap source bitmap
     * @param borderWidth size of border
     * @param borderColor color border
     * @return image in format bitmap with border
     */
    protected Bitmap addBorderToBitmap(Bitmap srcBitmap, int borderWidth, int borderColor){
        // Initialize a new Bitmap to make it bordered bitmap
        Bitmap dstBitmap = Bitmap.createBitmap(
                srcBitmap.getWidth() + borderWidth * 2, // Width
                srcBitmap.getHeight() + borderWidth * 2, // Height
                Bitmap.Config.ARGB_8888 // Config
        );

        /*
            Canvas
                The Canvas class holds the "draw" calls. To draw something, you need 4 basic
                components: A Bitmap to hold the pixels, a Canvas to host the draw calls (writing
                into the bitmap), a drawing primitive (e.g. Rect, Path, text, Bitmap), and a paint
                (to describe the colors and styles for the drawing).
        */
        // Initialize a new Canvas instance
        Canvas canvas = new Canvas(dstBitmap);

        // Initialize a new Paint instance to draw border
        Paint paint = new Paint();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        paint.setAntiAlias(true);

        /*
            Rect
                Rect holds four integer coordinates for a rectangle. The rectangle is represented by
                the coordinates of its 4 edges (left, top, right bottom). These fields can be accessed
                directly. Use width() and height() to retrieve the rectangle's width and height.
                Note: most methods do not check to see that the coordinates are sorted correctly
                (i.e. left <= right and top <= bottom).
        */
        /*
            Rect(int left, int top, int right, int bottom)
                Create a new rectangle with the specified coordinates.
        */

        // Initialize a new Rect instance
        /*
            We set left = border width /2, because android draw border in a shape
            by covering both inner and outer side.
            By padding half border size, we included full border inside the canvas.
        */
        Rect rect = new Rect(
                borderWidth / 2,
                borderWidth / 2,
                canvas.getWidth() - borderWidth / 2,
                canvas.getHeight() - borderWidth / 2
        );

        /*
            public void drawRect (Rect r, Paint paint)
                Draw the specified Rect using the specified Paint. The rectangle will be filled
                or framed based on the Style in the paint.

            Parameters
                r : The rectangle to be drawn.
                paint : The paint used to draw the rectangle

        */
        // Draw a rectangle as a border/shadow on canvas
        canvas.drawRect(rect, paint);

        /*
            public void drawBitmap (Bitmap bitmap, float left, float top, Paint paint)
                Draw the specified bitmap, with its top/left corner at (x,y), using the specified
                paint, transformed by the current matrix.

                Note: if the paint contains a maskfilter that generates a mask which extends beyond
                the bitmap's original width/height (e.g. BlurMaskFilter), then the bitmap will be
                drawn as if it were in a Shader with CLAMP mode. Thus the color outside of the
                original width/height will be the edge color replicated.

                If the bitmap and canvas have different densities, this function will take care of
                automatically scaling the bitmap to draw at the same density as the canvas.

            Parameters
                bitmap : The bitmap to be drawn
                left : The position of the left side of the bitmap being drawn
                top : The position of the top side of the bitmap being drawn
                paint : The paint used to draw the bitmap (may be null)
        */

        // Draw source bitmap to canvas
        canvas.drawBitmap(srcBitmap, borderWidth, borderWidth, null);

        /*
            public void recycle ()
                Free the native object associated with this bitmap, and clear the reference to the
                pixel data. This will not free the pixel data synchronously; it simply allows it to
                be garbage collected if there are no other references. The bitmap is marked as
                "dead", meaning it will throw an exception if getPixels() or setPixels() is called,
                and will draw nothing. This operation cannot be reversed, so it should only be
                called if you are sure there are no further uses for the bitmap. This is an advanced
                call, and normally need not be called, since the normal GC process will free up this
                memory when there are no more references to this bitmap.
        */
        srcBitmap.recycle();

        // Return the bordered circular bitmap
        return dstBitmap;
    }

    /**
     * This function makes a text string into a bitmap
     * @param gText string text
     * @param size size of the font
     * @param bitmap source image in format bitmap
     * @return image in format bitmap with text
     */
    public Bitmap drawTextToBitmap(String gText,int size, Bitmap bitmap) {

        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(61, 61, 61));
        // text size in pixels
        paint.setTextSize(size);
        // text center
        //paint.setTextAlign(Paint.Align.CENTER);
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width())/2;
        int y = (bitmap.getHeight() + bounds.height())/2;
        canvas.drawText(gText, x, y, paint);

        return bitmap;
    }

    /**
     * This function make buttons on root view
     */
    public void setMenu(){

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);//outside activity
        //getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);//inside activity
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        // array list with class button
        ArrayList<CircularButton> mySettingsList = new ArrayList<>();
        mySettingsList = new ArrayList<>();
        mySettingsList.add(new CircularButton(this.context));//hide, id = 0
        mySettingsList.add(new CircularButton(this.context));//settings id = 1
        mySettingsList.add(new CircularButton(this.context));//add id = 2
        mySettingsList.add(new CircularButton(this.context));//exit id = 3
        //array list with images
        ArrayList<Integer> myImageList = new ArrayList<>();
        myImageList.add(R.drawable.hide);
        myImageList.add(R.drawable.settings);
        myImageList.add(R.drawable.add);
        myImageList.add(R.drawable.exit);
        //this specify the size of icons by size of screen
        ArrayList<List<Integer>> myPointsList = new ArrayList<>();
        if (width <= 480 && height<= 820) {//min tam for screen
            myPointsList.add(Arrays.asList(width - 100, width - 170, width - 240, width - 100));//x
            myPointsList.add(Arrays.asList(height - 100, height - 100, height - 100, height - 170));//y
        }else {
            myPointsList.add(Arrays.asList(width - 120, width - 220, width - 320, width - 120));//x
            myPointsList.add(Arrays.asList(height - 120, height - 120, height - 120, height - 220));//y
        }
        // to add listener for each button
        ArrayList<View.OnClickListener> myListeners = new ArrayList<>();
        myListeners.add(onHide);//hide
        myListeners.add(onSettings);//settings
        myListeners.add(onAdd);//add
        myListeners.add(onExit);//exit
        // setup the each icon
        for (int i = 0; i<myPointsList.get(0).size();i++) {

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            CircularButton cb = mySettingsList.get(i);
            if (width <= 480)
                cb.setSizeImage(0);
            else
                cb.setSizeImage(1);
            cb.setImageResource(myImageList.get(i));
            cb.setOnClickListener(myListeners.get(i));
            lp.setMargins(myPointsList.get(0).get(i), myPointsList.get(1).get(i), 0, 0);
            cb.setLayoutParams(lp);
            cb.setId(aps.getiDSettings()[i]);
            relativeLayout.addView(cb);
            // add icon into the root view
            ((Activity) this.context).setContentView(relativeLayout, rlp);
        }
        //hidden the buttons on start app
        final ArrayList<Integer> hide = new ArrayList<>(Arrays.asList(0,1,1,0));
        for (int i = 0; i<hide.size();++i){
            CircularButton c = (CircularButton) ((Activity) context).findViewById(i);
            if (i == 1)
                if(c.getVisibility() == View.VISIBLE){
                    for (int j=0;j<hide.size();j++) {
                        CircularButton hidden = (CircularButton) ((Activity) context).findViewById(j);
                        if (hide.get(j) == 1) {
                            hidden.setVisibility(View.INVISIBLE);
                        }
                    }
                }
        }
    }

    /**
     * this function is for a action when is pushed button hide
     */

    public View.OnClickListener onHide= new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //<editor-fold defaultstate="collapsed" desc="OnHide">
            //alpha animation
            AlphaAnimation animation = new AlphaAnimation(0.2f, 1.0f);
            animation.setDuration(200);
            v.setAlpha(1f);
            v.startAnimation(animation);

            v.postDelayed(new Runnable() {
                @Override
                public void run() {

                    /*
                         0 is for VISIBLE
                         1 is for INVISIBLE
                         2 is for GONE
                    */
                    //CircularButton dc = (CircularButton) findViewById(0);
                    //only the hide button do an animation
                    CircularButton dc = (CircularButton) ((Activity) context).findViewById(0);
                    // Create an animation instance
                    Animation an = new RotateAnimation(0.0f, 360.0f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    // Set the animation's parameters
                    an.setDuration(1000);               // duration in ms
                    an.setRepeatCount(0);                // -1 = infinite repeated
                    an.setRepeatMode(Animation.REVERSE); // reverses each repeat
                    an.setFillAfter(true);               // keep rotation after animation
                    // Apply animation to image view
                    dc.setAnimation(an);

                    // hide and show depending the context
                    final ArrayList<Integer> hide = new ArrayList<>(Arrays.asList(0,1,1,0));
                    final ArrayList<Integer> show = new ArrayList<>(Arrays.asList(0,0,0,0));
                    for (int i=0;i<hide.size();i++) {
                        CircularButton c = (CircularButton) ((Activity) context).findViewById(i);
                        if (i == 1)
                            if(c.getVisibility() == View.INVISIBLE){
                                for (int j=0;j<show.size();j++) {
                                    CircularButton cShow = (CircularButton) ((Activity) context).findViewById(j);
                                    if (show.get(j) == 0) {
                                        cShow.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                            else {
                                for (int j = 0; j < hide.size(); j++) {
                                    CircularButton cHide = (CircularButton) ((Activity) context).findViewById(j);
                                    if (hide.get(j) == 1) {
                                        cHide.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }
                    }
                }
            }, 200);
//</editor-fold>
        }
//</editor-fold>
    };

    /**
     * Show popup window with settings
     */
    public View.OnClickListener onSettings= new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //<editor-fold defaultstate="collapsed" desc="OnSettings">
            AlphaAnimation animation = new AlphaAnimation(0.2f, 1.0f);
            animation.setDuration(200);
            v.setAlpha(1f);
            v.startAnimation(animation);

            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MainConfig();
                }
            }, 200);
            //</editor-fold>
        }
    };
    /**
     * Add blocks into the root view
     */
    public View.OnClickListener onAdd= new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //<editor-fold defaultstate="collapsed" desc="onAdd">
            AlphaAnimation animation = new AlphaAnimation(0.2f, 1.0f);
            animation.setDuration(200);
            v.setAlpha(1f);
            v.startAnimation(animation);

            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    OpenCategroyDialogBox();
                }
            }, 200);
            //</editor-fold>
        }
    };
    /**
     * exit app or close it
     */
    public View.OnClickListener onExit = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //<editor-fold defaultstate="collapsed" desc="onExit">
            AlphaAnimation animation = new AlphaAnimation(0.2f, 1.0f);
            animation.setDuration(200);
            v.setAlpha(1f);
            v.startAnimation(animation);

            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    exampleWorkTogether.onBackPressed();
                }
            }, 200);
            //</editor-fold>
        }
    };

    /**
     * function for settings
     */
    public void MainConfig(){
        //<editor-fold defaultstate="collapsed" desc="Main menu Configuration">
        AlertDialog.Builder alert = new AlertDialog.Builder(this.context);

        View view = new View(context);
        alert.setView(view);
        TextView title = new TextView(context);
        title.setText("SETTINGS");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        alert.setCustomTitle(title);

        RelativeLayout rl_master = new RelativeLayout(this.context);

        //Layout COUPLING SETTINGS
        RelativeLayout.LayoutParams lp_gestures = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_gestures.setMargins(10, 10, 10, 0);// left, top, right, bottom
        // Title COUPLING SETTINGS
        TextView title_gestos = new TextView(context);
        title_gestos.setText("COUPLING SETTINGS");
        title_gestos.setBackgroundColor(Color.rgb(255,229,204));//(Color.LTGRAY);
        title_gestos.setPadding(10, 10, 10, 10);
        title_gestos.setGravity(Gravity.CENTER);
        title_gestos.setTextColor(Color.WHITE);
        title_gestos.setTextSize(13);
        title_gestos.setLayoutParams(lp_gestures);
        rl_master.addView(title_gestos);

        // Layout COUPLING SETTINGS --> Button
        RelativeLayout.LayoutParams lp_csettings = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_csettings.setMargins(10, 63, 10, 10);// left, top, right, bottom
        // Title COUPLING SETTINGS --> Button
        Button bCSettings = new Button(context);
        bCSettings.setText("COUPLING");
        bCSettings.setTextSize(12);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        bCSettings.setPadding(10, 10, 10, 10);
        bCSettings.setGravity(Gravity.CENTER);
        bCSettings.setLayoutParams(lp_csettings);
        bCSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainConfigCOUPLING();
            }
        });
        rl_master.addView(bCSettings);

        //Layout MOBILE INFO
        RelativeLayout.LayoutParams lp_info = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_info.setMargins(10, 157, 10, 0);// left, top, right, bottom
        // Title MOBILE INFO
        TextView title_info = new TextView(context);
        title_info.setText("MOBILE INFO");
        title_info.setBackgroundColor(Color.rgb(255,204,153));
        title_info.setPadding(10, 10, 10, 10);
        title_info.setGravity(Gravity.CENTER);
        title_info.setTextColor(Color.WHITE);
        title_info.setTextSize(13);
        title_info.setLayoutParams(lp_info);
        rl_master.addView(title_info);

        // Layout MOBILE INFO --> Button
        RelativeLayout.LayoutParams lp_minfo = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_minfo.setMargins(10, 210, 10, 10);// left, top, right, bottom
        // Title MOBILE INFO --> Button
        Button bMInfo = new Button(context);
        bMInfo.setText("INFORMATION");
        bMInfo.setTextSize(12);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        bMInfo.setPadding(10, 10, 10, 10);
        bMInfo.setGravity(Gravity.CENTER);
        bMInfo.setLayoutParams(lp_minfo);
        bMInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = exampleWorkTogether.getOwnInfo();
                String ip = "";
                String tcp= "";
                String udp= "";
                String device= "";
                String os = "";
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        if (entry.getKey().equals("IP")) {
                            ip = entry.getValue();
                        } else if (entry.getKey().equals("TCP")) {
                            tcp = entry.getValue();
                        } else if (entry.getKey().equals("UDP")) {
                            udp = entry.getValue();
                        } else if (entry.getKey().equals("NAME")) {
                            device = entry.getValue();
                        }
                    }
                MainINFO(ip,tcp,udp,device,"Android");
            }
        });
        rl_master.addView(bMInfo);

        //Layout FRAMEWORK CONFIG
        RelativeLayout.LayoutParams lp_cframework = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_cframework.setMargins(10, 303, 10, 0);// left, top, right, bottom
        // Title FRAMEWORK CONFIG
        TextView title_cframework = new TextView(context);
        title_cframework.setText("FRAMEWORK CONFIGURATION");
        title_cframework.setBackgroundColor(Color.rgb(255,178,102));
        title_cframework.setPadding(10, 10, 10, 10);
        title_cframework.setGravity(Gravity.CENTER);
        title_cframework.setTextColor(Color.WHITE);
        title_cframework.setTextSize(13);
        title_cframework.setLayoutParams(lp_cframework);
        rl_master.addView(title_cframework);

        // Layout FRAMEWORK CONFIG --> Button
        RelativeLayout.LayoutParams lp_bcframework = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_bcframework.setMargins(10, 355, 10, 10);// left, top, right, bottom
        // Title FRAMEWORK CONFIG --> Button
        Button bCFrame = new Button(context);
        bCFrame.setText("CONFIGURATION");
        bCFrame.setTextSize(12);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        bCFrame.setPadding(10, 10, 10, 10);
        bCFrame.setGravity(Gravity.CENTER);
        bCFrame.setLayoutParams(lp_bcframework);
        bCFrame.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> config = exampleWorkTogether.getInfoConfig();
                String ip = "";
                String tcp= "";
                String udp= "";
                String sstatustime= "";
                String statusdiscovery = "";
                String statusaccelerometer = "";
                String statusswipe = "";

                for (Map.Entry<String, String> entry : config.entrySet()) {
                        if (entry.getKey().equals("IP")) {
                            ip = entry.getValue().toString();
                        } else if (entry.getKey().equals("TCP")) {
                            tcp = entry.getValue().toString();
                        } else if (entry.getKey().equals("UDP")) {
                            udp = entry.getValue().toString();
                        } else if (entry.getKey().equals("STATUSTIME")) {
                            sstatustime = entry.getValue().toString();
                        } else if (entry.getKey().equals("STATUSDISCOVERYTIME")) {
                            statusdiscovery = entry.getValue().toString();
                        } else if (entry.getKey().equals("STATUSACCELEROMETER")) {
                            statusaccelerometer = entry.getValue().toString();
                        } else if (entry.getKey().equals("STATUSSWIPE")) {
                            statusswipe = entry.getValue().toString();
                        }
                }
                MainConfigSettings(ip,tcp,udp,sstatustime,statusdiscovery,statusaccelerometer,statusswipe);
            }
        });
        rl_master.addView(bCFrame);

        //Layout CHANGE COLOR
        RelativeLayout.LayoutParams lp_tbackcolor = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_tbackcolor.setMargins(10, 447, 10, 0);
        // title CHANGE COLOR
        TextView title_tbackcolor = new TextView(context);
        title_tbackcolor.setText("CHANGE COLOR SCREEN");
        title_tbackcolor.setBackgroundColor(Color.rgb(255,153,51));
        title_tbackcolor.setPadding(10, 10, 10, 10);
        title_tbackcolor.setGravity(Gravity.CENTER);
        title_tbackcolor.setTextColor(Color.WHITE);
        title_tbackcolor.setTextSize(11);
        title_tbackcolor.setLayoutParams(lp_tbackcolor);
        rl_master.addView(title_tbackcolor);
        // Title CHANGE COLOR --> Button
        RelativeLayout.LayoutParams lp_bbackcolor = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_bbackcolor.setMargins(10, 493, 0, 0);// left, top, right, bottom

        Button bColor = new Button(context);
        bColor.setText("CHANGE COLOR");
        bColor.setTextSize(12);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        bColor.setPadding(10, 10, 10, 10);
        bColor.setGravity(Gravity.CENTER);
        bColor.setLayoutParams(lp_bbackcolor);
        bColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] ncolor = aps.getBNewColor();
                int r = (int) ncolor[0];
                int g = (int) ncolor[1];
                int b = (int) ncolor[2];
                relativeLayout.setBackgroundColor(Color.argb(230,r,g,b));
                //relativeLayout.setBackground(aps.getBorder());// con borde
                exampleWorkTogether.sendBackGroudColor(aps.getBColor());
            }
        });
        rl_master.addView(bColor);

        alert.setView(rl_master);
        alert.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                return;
            }
        });

        // create an alert dialog
        AlertDialog alert1 = alert.create();

        alert1.show();
        //</editor-fold>
    }

    public void MainConfigCOUPLING(){
        //<editor-fold defaultstate="collapsed" desc="Show info of devices coupling">
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);//outside activity
        int width = displaymetrics.widthPixels;


        final AlertDialog.Builder alert = new AlertDialog.Builder(this.context);

        View view = new View(context);
        alert.setView(view);
        TextView title = new TextView(context);
        title.setText("COUPLING SETTINGS");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        alert.setCustomTitle(title);

        RelativeLayout rl_master = new RelativeLayout(this.context);

        //Layout COUPLING GESTURES
        RelativeLayout.LayoutParams lp_gestures = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_gestures.setMargins(10, 10, 10, 0);// left, top, right, bottom
        // Title COUPLING GESTURES
        TextView title_gestos = new TextView(context);
        title_gestos.setText("COUPLING GESTURES");
        title_gestos.setBackgroundColor(Color.rgb(204,255,204));//(Color.LTGRAY);
        title_gestos.setPadding(10, 10, 10, 10);
        title_gestos.setGravity(Gravity.CENTER);
        title_gestos.setTextColor(Color.WHITE);
        title_gestos.setTextSize(13);
        title_gestos.setLayoutParams(lp_gestures);
        rl_master.addView(title_gestos);
        //Layout GESTURES
        RelativeLayout.LayoutParams lp_rdgGrup = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_rdgGrup.setMargins(10, 63, 0, 0);
        // Title GESTURES --> Buttons
        String[] sgestures = {"ACCELEROMETER", "SWIPE"};
        RadioButton[] rbutton = new RadioButton[2];
        final RadioGroup rdgGrup = new RadioGroup(context);
        rdgGrup.setOrientation(RadioGroup.HORIZONTAL);
        String[] couplingGestures = exampleWorkTogether.getCoupling();//0 = accelerometer, 1 = swipe
        //Log.d("workTogether", "GESTOS: " + Arrays.toString(couplingGestures));
        for (int i=0;i<rbutton.length;++i){
            rbutton[i] = new RadioButton(context);
            rdgGrup.addView(rbutton[i]);
            rbutton[i].setText(sgestures[i]);
            rbutton[i].setId(aps.getiDbGestures()[i]);// id 4 y 5
            if (i == 0 && couplingGestures[0].equals("TRUE"))
                rdgGrup.check(rbutton[i].getId());// Accelerometer
            else if(i == 1 && couplingGestures[1].equals("TRUE"))
                rdgGrup.check(rbutton[i].getId());// Swipe
        }
        rdgGrup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String id = Integer.toString(checkedId);
                if (id.equals("4")){
                    exampleWorkTogether.setupCoupling(true,false);
                }
                else {
                    exampleWorkTogether.setupCoupling(false,true);
                }
               // Log.d("workTogether", "Setear ID: " + checkedId);
            }
        });

        rdgGrup.setPadding(10, 63, 10, 10);
        rdgGrup.setGravity(Gravity.CENTER);
        rl_master.addView(rdgGrup);




        //Layout CONNECTED DEVICES
        RelativeLayout.LayoutParams lp_info = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_info.setMargins(10, 135, 10, 0);// left, top, right, bottom
        // Title CONNECTED DEVICES
        TextView title_info = new TextView(context);
        title_info.setText("CONNECTED DEVICES");
        title_info.setBackgroundColor(Color.rgb(153, 255, 153));
        title_info.setPadding(10, 10, 10, 10);
        title_info.setGravity(Gravity.CENTER);
        title_info.setTextColor(Color.WHITE);
        title_info.setTextSize(13);
        title_info.setLayoutParams(lp_info);
        rl_master.addView(title_info);





        if(isTabletorPhone()) {
            //Layout CONNECTED DEVICES IMAGE
            RelativeLayout.LayoutParams lp_cdimage = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_cdimage.setMargins(10, 410, 10, 0);// left, top, right, bottom
            // IMAGE PHONE
            ImageView phone = new ImageView(context);
            //phone.setImageBitmap(img);
            phone.setImageResource(R.drawable.tablet_androidx);
            phone.setPadding(10, 10, 10, 10);
            phone.setLayoutParams(lp_cdimage);
            rl_master.addView(phone);

            // Layout CONNECTED DEVICES --> Button TA1
            RelativeLayout.LayoutParams lp_ta1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_ta1.setMargins(10 , 200, width/4 - 100, 10);// left, top, right, bottom
            // Title MOBILE INFO --> Button
            Button bta1 = new Button(context);
            bta1.setText("TA1");
            bta1.setTextSize(12);
            bta1.setBackgroundColor(Color.rgb(255,204,204));
            bta1.setPadding(10, 10, 10, 10);
            bta1.setGravity(Gravity.CENTER);
            bta1.setLayoutParams(lp_ta1);
            bta1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<HashMap<String, Object>> connected = exampleWorkTogether.getConected();// lista de connectados
                    String ip = "";
                    String tcp= "";
                    String udp= "";
                    String device= "";
                    String os = "";
                    for (HashMap<String,Object> data : connected) {
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            if (entry.getKey().equals("TA1")) {
                                ip = entry.getValue().toString();
                            } else if (entry.getKey().equals("TCP")) {
                                tcp = entry.getValue().toString();
                            } else if (entry.getKey().equals("UDP")) {
                                udp = entry.getValue().toString();
                            } else if (entry.getKey().equals("DEVICE")) {
                                device = entry.getValue().toString();
                            } else if (entry.getKey().equals("TYPEDEVICE")) {
                                os = entry.getValue().toString();
                            }
                        }
                        if (!ip.equals(""))
                            break;
                    }

                    if (!ip.equals("")){
                        MainConfigCOUPLINGINFO(ip,tcp,udp,device,os);
                    }
                    else {
                        Toast.makeText(context, "No devices connected in TA1", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            rl_master.addView(bta1);

            // Layout CONNECTED DEVICES --> Button TA2
            RelativeLayout.LayoutParams lp_ta2 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_ta2.setMargins(width/4 - 100 , 200, 10, 10);// left, top, right, bottom
            // Title MOBILE INFO --> Button
            Button bta2 = new Button(context);
            bta2.setText("TA2");
            bta2.setTextSize(12);
            bta2.setBackgroundColor(Color.rgb(255,204,229));
            bta2.setPadding(10, 10, 10, 10);
            bta2.setGravity(Gravity.CENTER);
            bta2.setLayoutParams(lp_ta2);
            bta2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<HashMap<String, Object>> connected = exampleWorkTogether.getConected();// lista de connectados
                    String ip = "";
                    String tcp= "";
                    String udp= "";
                    String device= "";
                    String os = "";
                    for (HashMap<String,Object> data : connected) {
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            if (entry.getKey().equals("TA2")) {
                                ip = entry.getValue().toString();
                            } else if (entry.getKey().equals("TCP")) {
                                tcp = entry.getValue().toString();
                            } else if (entry.getKey().equals("UDP")) {
                                udp = entry.getValue().toString();
                            } else if (entry.getKey().equals("DEVICE")) {
                                device = entry.getValue().toString();
                            } else if (entry.getKey().equals("TYPEDEVICE")) {
                                os = entry.getValue().toString();
                            }
                        }
                        if (!ip.equals(""))
                            break;
                    }

                    if (!ip.equals("")){
                        MainConfigCOUPLINGINFO(ip,tcp,udp,device,os);
                    }
                    else {
                        Toast.makeText(context, "No devices connected in TA2", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            rl_master.addView(bta2);

            // Layout CONNECTED DEVICES --> Button TB1
            RelativeLayout.LayoutParams lp_tb1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_tb1.setMargins(10 , 310, width/4 - 100, 10);// left, top, right, bottom
            // Title MOBILE INFO --> Button
            Button btb1 = new Button(context);
            btb1.setText("TB1");
            btb1.setTextSize(12);
            btb1.setBackgroundColor(Color.rgb(204,255,255));
            btb1.setPadding(10, 10, 10, 10);
            btb1.setGravity(Gravity.CENTER);
            btb1.setLayoutParams(lp_tb1);
            btb1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<HashMap<String, Object>> connected = exampleWorkTogether.getConected();// lista de connectados
                    String ip = "";
                    String tcp= "";
                    String udp= "";
                    String device= "";
                    String os = "";
                    for (HashMap<String,Object> data : connected) {
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            if (entry.getKey().equals("TB1")) {
                                ip = entry.getValue().toString();
                            } else if (entry.getKey().equals("TCP")) {
                                tcp = entry.getValue().toString();
                            } else if (entry.getKey().equals("UDP")) {
                                udp = entry.getValue().toString();
                            } else if (entry.getKey().equals("DEVICE")) {
                                device = entry.getValue().toString();
                            } else if (entry.getKey().equals("TYPEDEVICE")) {
                                os = entry.getValue().toString();
                            }
                        }
                        if (!ip.equals(""))
                            break;
                    }

                    if (!ip.equals("")){
                        MainConfigCOUPLINGINFO(ip,tcp,udp,device,os);
                    }
                    else {
                        Toast.makeText(context, "No devices connected in TB1", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            rl_master.addView(btb1);

            // Layout CONNECTED DEVICES --> Button TB2
            RelativeLayout.LayoutParams lp_tb2 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_tb2.setMargins(width/4 - 100, 310, 10, 10);// left, top, right, bottom
            // Title MOBILE INFO --> Button
            Button btb2 = new Button(context);
            btb2.setText("TB2");
            btb2.setTextSize(12);
            btb2.setBackgroundColor(Color.rgb(204,255,204));
            btb2.setPadding(10, 10, 10, 10);
            btb2.setGravity(Gravity.CENTER);
            btb2.setLayoutParams(lp_tb2);
            btb2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<HashMap<String, Object>> connected = exampleWorkTogether.getConected();// lista de connectados
                    String ip = "";
                    String tcp= "";
                    String udp= "";
                    String device= "";
                    String os = "";
                    for (HashMap<String,Object> data : connected) {
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            if (entry.getKey().equals("TB2")) {
                                ip = entry.getValue().toString();
                            } else if (entry.getKey().equals("TCP")) {
                                tcp = entry.getValue().toString();
                            } else if (entry.getKey().equals("UDP")) {
                                udp = entry.getValue().toString();
                            } else if (entry.getKey().equals("DEVICE")) {
                                device = entry.getValue().toString();
                            } else if (entry.getKey().equals("TYPEDEVICE")) {
                                os = entry.getValue().toString();
                            }
                        }
                        if (!ip.equals(""))
                            break;
                    }

                    if (!ip.equals("")){
                        MainConfigCOUPLINGINFO(ip,tcp,udp,device,os);
                    }
                    else {
                        Toast.makeText(context, "No devices connected in TB2", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            rl_master.addView(btb2);



            // Layout CONNECTED DEVICES --> Button TC1
            RelativeLayout.LayoutParams lp_rc1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_rc1.setMargins(10 , 420, width/4 - 100, 10);// left, top, right, bottom
            // Title MOBILE INFO --> Button
            Button btc1 = new Button(context);
            btc1.setText("TC1");
            btc1.setTextSize(12);
            btc1.setBackgroundColor(Color.rgb(255,255,204));
            btc1.setPadding(10, 10, 10, 10);
            btc1.setGravity(Gravity.CENTER);
            btc1.setLayoutParams(lp_rc1);
            btc1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<HashMap<String, Object>> connected = exampleWorkTogether.getConected();// lista de connectados
                    String ip = "";
                    String tcp= "";
                    String udp= "";
                    String device= "";
                    String os = "";
                    for (HashMap<String,Object> data : connected) {
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            if (entry.getKey().equals("TC1")) {
                                ip = entry.getValue().toString();
                            } else if (entry.getKey().equals("TCP")) {
                                tcp = entry.getValue().toString();
                            } else if (entry.getKey().equals("UDP")) {
                                udp = entry.getValue().toString();
                            } else if (entry.getKey().equals("DEVICE")) {
                                device = entry.getValue().toString();
                            } else if (entry.getKey().equals("TYPEDEVICE")) {
                                os = entry.getValue().toString();
                            }
                        }
                        if (!ip.equals(""))
                            break;
                    }

                    if (!ip.equals("")){
                        MainConfigCOUPLINGINFO(ip,tcp,udp,device,os);
                    }
                    else {
                        Toast.makeText(context, "No devices connected in TC1", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            rl_master.addView(btc1);

            // Layout CONNECTED DEVICES --> Button TC2
            RelativeLayout.LayoutParams lp_tc2 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_tc2.setMargins(width/4 - 100 , 420, 10, 10);// left, top, right, bottom
            // Title MOBILE INFO --> Button
            Button btc2 = new Button(context);
            btc2.setText("TC2");
            btc2.setTextSize(12);
            btc2.setBackgroundColor(Color.rgb(224,224,224));
            btc2.setPadding(10, 10, 10, 10);
            btc2.setGravity(Gravity.CENTER);
            btc2.setLayoutParams(lp_tc2);
            btc2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<HashMap<String, Object>> connected = exampleWorkTogether.getConected();// lista de connectados
                    String ip = "";
                    String tcp= "";
                    String udp= "";
                    String device= "";
                    String os = "";
                    for (HashMap<String,Object> data : connected) {
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            if (entry.getKey().equals("TC2")) {
                                ip = entry.getValue().toString();
                            } else if (entry.getKey().equals("TCP")) {
                                tcp = entry.getValue().toString();
                            } else if (entry.getKey().equals("UDP")) {
                                udp = entry.getValue().toString();
                            } else if (entry.getKey().equals("DEVICE")) {
                                device = entry.getValue().toString();
                            } else if (entry.getKey().equals("TYPEDEVICE")) {
                                os = entry.getValue().toString();
                            }
                        }
                        if (!ip.equals(""))
                            break;
                    }

                    if (!ip.equals("")){
                        MainConfigCOUPLINGINFO(ip,tcp,udp,device,os);
                    }
                    else {
                        Toast.makeText(context, "No devices connected in TC2", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            rl_master.addView(btc2);


            //Layout x
            RelativeLayout.LayoutParams lp_x = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_x.setMargins(10, 450, 10, 0);// left, top, right, bottom
            // Title x
            TextView title_x = new TextView(context);
            title_x.setText("");
            title_x.setBackgroundColor(Color.TRANSPARENT);
            title_x.setPadding(10, 10, 10, 10);
            title_x.setGravity(Gravity.CENTER);
            title_x.setTextColor(Color.WHITE);
            title_x.setTextSize(13);
            title_x.setLayoutParams(lp_x);
            rl_master.addView(title_x);




        }
        else {
            //Layout CONNECTED DEVICES IMAGE
            RelativeLayout.LayoutParams lp_cdimage = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_cdimage.setMargins(100, 210, 10, 0);// left, top, right, bottom
            // IMAGE PHONE
            ImageView phone = new ImageView(context);
            //phone.setImageBitmap(img);
            phone.setImageResource(R.drawable.phone_androidx);
            phone.setPadding(10, 10, 10, 10);
            phone.setLayoutParams(lp_cdimage);
            rl_master.addView(phone);

            // Layout CONNECTED DEVICES --> Button PA1
            RelativeLayout.LayoutParams lp_pa1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_pa1.setMargins(10 , 250, width/2 + 50, 5);// left, top, right, bottom
            // Title MOBILE INFO --> Button
            Button bpa1 = new Button(context);
            bpa1.setText("PA1");
            bpa1.setTextSize(12);
            bpa1.setBackgroundColor(Color.rgb(255, 153, 153));
            bpa1.setPadding(10, 10, 10, 10);
            bpa1.setGravity(Gravity.CENTER);
            bpa1.setLayoutParams(lp_pa1);
            bpa1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<HashMap<String, Object>> connected = exampleWorkTogether.getConected();// lista de connectados
                    String ip = "";
                    String tcp= "";
                    String udp= "";
                    String device= "";
                    String os = "";
                    for (HashMap<String,Object> data : connected) {
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            if (entry.getKey().equals("PA1")) {
                                ip = entry.getValue().toString();
                            } else if (entry.getKey().equals("TCP")) {
                                tcp = entry.getValue().toString();
                            } else if (entry.getKey().equals("UDP")) {
                                udp = entry.getValue().toString();
                            } else if (entry.getKey().equals("DEVICE")) {
                                device = entry.getValue().toString();
                            } else if (entry.getKey().equals("TYPEDEVICE")) {
                                os = entry.getValue().toString();
                            }
                        }
                        if (!ip.equals(""))
                            break;
                    }

                    if (!ip.equals("")){
                        MainConfigCOUPLINGINFO(ip,tcp,udp,device,os);
                        }
                    else {
                        Toast.makeText(context, "No devices connected in PA1", Toast.LENGTH_SHORT).show();
                        }
                }
            });
            rl_master.addView(bpa1);

            // Layout CONNECTED DEVICES --> Button PB1
            RelativeLayout.LayoutParams lp_pb1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_pb1.setMargins(10 , 335, width/2 + 50, 5);// left, top, right, bottom
            // Title MOBILE INFO --> Button
            Button bb1 = new Button(context);
            bb1.setText("PB1");
            bb1.setTextSize(12);
            bb1.setBackgroundColor(Color.rgb(153, 255, 255));
            bb1.setPadding(10, 10, 10, 10);
            bb1.setGravity(Gravity.CENTER);
            bb1.setLayoutParams(lp_pb1);
            bb1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<HashMap<String, Object>> connected = exampleWorkTogether.getConected();// lista de connectados
                    String ip = "";
                    String tcp= "";
                    String udp= "";
                    String device= "";
                    String os = "";
                    for (HashMap<String,Object> data : connected) {
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            if (entry.getKey().equals("PB1")) {
                                ip = entry.getValue().toString();
                            } else if (entry.getKey().equals("TCP")) {
                                tcp = entry.getValue().toString();
                            } else if (entry.getKey().equals("UDP")) {
                                udp = entry.getValue().toString();
                            } else if (entry.getKey().equals("DEVICE")) {
                                device = entry.getValue().toString();
                            } else if (entry.getKey().equals("TYPEDEVICE")) {
                                os = entry.getValue().toString();
                            }
                        }
                        if (!ip.equals(""))
                            break;
                    }

                    if (!ip.equals("")){
                        MainConfigCOUPLINGINFO(ip,tcp,udp,device,os);
                    }
                    else {
                        Toast.makeText(context, "No devices connected in PB1", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            rl_master.addView(bb1);

            // Layout CONNECTED DEVICES --> Button PC1
            RelativeLayout.LayoutParams lp_pc1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_pc1.setMargins(10 , 420, width/2 + 50, 5);// left, top, right, bottom
            // Title MOBILE INFO --> Button
            Button bpc1 = new Button(context);
            bpc1.setText("PC1");
            bpc1.setTextSize(12);
            bpc1.setBackgroundColor(Color.rgb(255, 255, 153));
            bpc1.setPadding(10, 10, 10, 10);
            bpc1.setGravity(Gravity.CENTER);
            bpc1.setLayoutParams(lp_pc1);
            bpc1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<HashMap<String, Object>> connected = exampleWorkTogether.getConected();// lista de connectados
                    String ip = "";
                    String tcp= "";
                    String udp= "";
                    String device= "";
                    String os = "";
                    for (HashMap<String,Object> data : connected) {
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            if (entry.getKey().equals("PC1")) {
                                ip = entry.getValue().toString();
                            } else if (entry.getKey().equals("TCP")) {
                                tcp = entry.getValue().toString();
                            } else if (entry.getKey().equals("UDP")) {
                                udp = entry.getValue().toString();
                            } else if (entry.getKey().equals("DEVICE")) {
                                device = entry.getValue().toString();
                            } else if (entry.getKey().equals("TYPEDEVICE")) {
                                os = entry.getValue().toString();
                            }
                        }
                        if (!ip.equals(""))
                            break;
                    }

                    if (!ip.equals("")){
                        MainConfigCOUPLINGINFO(ip,tcp,udp,device,os);
                    }
                    else {
                        Toast.makeText(context, "No devices connected in PC1", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            rl_master.addView(bpc1);

            // Layout CONNECTED DEVICES --> Button PC2
            RelativeLayout.LayoutParams lp_pc2 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_pc2.setMargins(10 , 506, width/2 + 50, 5);// left, top, right, bottom
            // Title MOBILE INFO --> Button
            Button bpc2 = new Button(context);
            bpc2.setText("PC2");
            bpc2.setTextSize(12);
            bpc2.setBackgroundColor(Color.rgb(153,255,153));
            bpc2.setPadding(10, 10, 10, 10);
            bpc2.setGravity(Gravity.CENTER);
            bpc2.setLayoutParams(lp_pc2);
            bpc2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<HashMap<String, Object>> connected = exampleWorkTogether.getConected();// lista de connectados
                    String ip = "";
                    String tcp= "";
                    String udp= "";
                    String device= "";
                    String os = "";
                    for (HashMap<String,Object> data : connected) {
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            if (entry.getKey().equals("PC2")) {
                                ip = entry.getValue().toString();
                            } else if (entry.getKey().equals("TCP")) {
                                tcp = entry.getValue().toString();
                            } else if (entry.getKey().equals("UDP")) {
                                udp = entry.getValue().toString();
                            } else if (entry.getKey().equals("DEVICE")) {
                                device = entry.getValue().toString();
                            } else if (entry.getKey().equals("TYPEDEVICE")) {
                                os = entry.getValue().toString();
                            }
                        }
                        if (!ip.equals(""))
                            break;
                    }

                    if (!ip.equals("")){
                        MainConfigCOUPLINGINFO(ip,tcp,udp,device,os);
                    }
                    else {
                        Toast.makeText(context, "No devices connected in PC2", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            rl_master.addView(bpc2);


            //Layout x
            RelativeLayout.LayoutParams lp_x = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_x.setMargins(10, 555, 10, 0);// left, top, right, bottom
            // Title x
            TextView title_x = new TextView(context);
            title_x.setText("");
            title_x.setBackgroundColor(Color.TRANSPARENT);
            title_x.setPadding(10, 10, 10, 10);
            title_x.setGravity(Gravity.CENTER);
            title_x.setTextColor(Color.WHITE);
            title_x.setTextSize(13);
            title_x.setLayoutParams(lp_x);
            rl_master.addView(title_x);

        }

        alert.setView(rl_master);
        alert.setPositiveButton("<--| ATRAS", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                return;
            }
        });


        // create an alert dialog
        AlertDialog alert1 = alert.create();

        alert1.show();
        //</editor-fold>
    }

    public void MainConfigCOUPLINGINFO(String ip,String tcp,String udp,String device,String os){
        //<editor-fold defaultstate="collapsed" desc="Show info of devices connected">
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);//outside activity
        int width = displaymetrics.widthPixels;


        AlertDialog.Builder alert = new AlertDialog.Builder(this.context);

        View view = new View(context);
        alert.setView(view);
        TextView title = new TextView(context);
        title.setText("DEVICE INFO");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        alert.setCustomTitle(title);

        RelativeLayout rl_master = new RelativeLayout(this.context);

        //Layout INFO
        RelativeLayout.LayoutParams lp_info = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_info.setMargins(10, 10, 10, 0);// left, top, right, bottom
        // Title INFO
        TextView title_info = new TextView(context);
        title_info.setText("INFORMATION");
        title_info.setBackgroundColor(Color.rgb(204,255,204));//(Color.LTGRAY);
        title_info.setPadding(10, 10, 10, 10);
        title_info.setGravity(Gravity.CENTER);
        title_info.setTextColor(Color.WHITE);
        title_info.setTextSize(13);
        title_info.setLayoutParams(lp_info);
        rl_master.addView(title_info);


        // Layout INFO --> TEXVIEW NAME
        RelativeLayout.LayoutParams lp_name = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_name.setMargins(10, 66, width/4 , 10);// left, top, right, bottom
        else
            lp_name.setMargins(10, 66, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW NAME
        TextView tex_name = new TextView(context);
        tex_name.setText("NAME:");
        tex_name.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_name.setPadding(10, 10, 10, 10);
        tex_name.setGravity(Gravity.CENTER);
        tex_name.setLayoutParams(lp_name);
        rl_master.addView(tex_name);

        // Layout INFO --> EditText NAME
        RelativeLayout.LayoutParams lp_namet = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_namet.setMargins(width/4 - 180, 66, 10 , 10);// left, top, right, bottom
        else
            lp_namet.setMargins(width/2 - 180, 66, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText NAME
        EditText tex_namet = new EditText(context);
        tex_namet.setText(device);
        tex_namet.setTextSize(16);
        tex_namet.setFocusable(false);
        tex_namet.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        tex_namet.setClickable(false);// user navigates with wheel and selects widget
        tex_namet.setBackgroundColor(Color.rgb(255, 229, 204));
        tex_namet.setPadding(10, 10, 10, 10);
        tex_namet.setGravity(Gravity.CENTER);
        tex_namet.setLayoutParams(lp_namet);
        rl_master.addView(tex_namet);

        // Layout INFO --> TEXVIEW OS
        RelativeLayout.LayoutParams lp_os = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_os.setMargins(10, 130, width/4 , 10);// left, top, right, bottom
        else
            lp_os.setMargins(10, 130, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW OS
        TextView tex_os = new TextView(context);
        tex_os.setText("OS: ");
        tex_os.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_os.setPadding(10, 10, 10, 10);
        tex_os.setGravity(Gravity.CENTER);
        tex_os.setLayoutParams(lp_os);
        rl_master.addView(tex_os);

        // Layout INFO --> EditText OS
        RelativeLayout.LayoutParams lp_ost = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_ost.setMargins(width/4 - 180, 130, 10 , 10);// left, top, right, bottom
        else
            lp_ost.setMargins(width/2 - 180, 130, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText OS
        EditText tex_ost = new EditText(context);
        tex_ost.setText(os);
        tex_ost.setTextSize(16);
        tex_ost.setFocusable(false);
        tex_ost.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        tex_ost.setClickable(false);// user navigates with wheel and selects widget
        tex_ost.setBackgroundColor(Color.rgb(255,229,204));
        tex_ost.setPadding(10, 10, 10, 10);
        tex_ost.setGravity(Gravity.CENTER);
        tex_ost.setLayoutParams(lp_ost);
        rl_master.addView(tex_ost);


        // Layout INFO --> TEXVIEW IP
        RelativeLayout.LayoutParams lp_ip = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_ip.setMargins(10, 194, width/4 , 10);// left, top, right, bottom
        else
            lp_ip.setMargins(10, 194, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW IP
        TextView tex_ip = new TextView(context);
        tex_ip.setText("IP: ");
        tex_ip.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_ip.setPadding(10, 10, 10, 10);
        tex_ip.setGravity(Gravity.CENTER);
        tex_ip.setLayoutParams(lp_ip);
        rl_master.addView(tex_ip);

        // Layout INFO --> EditText IP
        RelativeLayout.LayoutParams lp_ipt = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_ipt.setMargins(width/4 - 180, 194, 10 , 10);// left, top, right, bottom
        else
            lp_ipt.setMargins(width/2 - 180, 194, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText IP
        EditText tex_ipt = new EditText(context);
        tex_ipt.setText(ip);
        tex_ipt.setTextSize(16);
        tex_ipt.setFocusable(false);
        tex_ipt.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        tex_ipt.setClickable(false);// user navigates with wheel and selects widget
        tex_ipt.setBackgroundColor(Color.rgb(255, 229, 204));
        tex_ipt.setPadding(10, 10, 10, 10);
        tex_ipt.setGravity(Gravity.CENTER);
        tex_ipt.setLayoutParams(lp_ipt);
        rl_master.addView(tex_ipt);


        // Layout INFO --> TEXVIEW TCP
        RelativeLayout.LayoutParams lp_tcp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_tcp.setMargins(10, 258, width/4 , 10);// left, top, right, bottom
        else
            lp_tcp.setMargins(10, 258, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW TCP
        TextView tex_tcp = new TextView(context);
        tex_tcp.setText("TCP: ");
        tex_tcp.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_tcp.setPadding(10, 10, 10, 10);
        tex_tcp.setGravity(Gravity.CENTER);
        tex_tcp.setLayoutParams(lp_tcp);
        rl_master.addView(tex_tcp);

        // Layout INFO --> EditText TCP
        RelativeLayout.LayoutParams lp_tcpt = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_tcpt.setMargins(width/4 - 180, 258, 10 , 10);// left, top, right, bottom
        else
            lp_tcpt.setMargins(width/2 - 180, 258, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText TCP
        EditText tex_tcpt = new EditText(context);
        tex_tcpt.setText(tcp);
        tex_tcpt.setTextSize(16);
        tex_tcpt.setFocusable(false);
        tex_tcpt.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        tex_tcpt.setClickable(false);// user navigates with wheel and selects widget
        tex_tcpt.setBackgroundColor(Color.rgb(255, 229, 204));
        tex_tcpt.setPadding(10, 10, 10, 10);
        tex_tcpt.setGravity(Gravity.CENTER);
        tex_tcpt.setLayoutParams(lp_tcpt);
        rl_master.addView(tex_tcpt);

        // Layout INFO --> TEXVIEW UDP
        RelativeLayout.LayoutParams lp_udp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_udp.setMargins(10, 323, width/4 , 10);// left, top, right, bottom
        else
            lp_udp.setMargins(10, 323, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW UDP
        TextView tex_udp = new TextView(context);
        tex_udp.setText("UDP: ");
        tex_udp.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_udp.setPadding(10, 10, 10, 10);
        tex_udp.setGravity(Gravity.CENTER);
        tex_udp.setLayoutParams(lp_udp);
        rl_master.addView(tex_udp);

        // Layout INFO --> EditText UDP
        RelativeLayout.LayoutParams lp_udpt = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_udpt.setMargins(width/4 - 180, 323, 10 , 10);// left, top, right, bottom
        else
            lp_udpt.setMargins(width/2 - 180, 323, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText UDP
        EditText tex_udpt = new EditText(context);
        tex_udpt.setText(udp);
        tex_udpt.setTextSize(16);
        tex_udpt.setFocusable(false);
        tex_udpt.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        tex_udpt.setClickable(false);// user navigates with wheel and selects widget
        tex_udpt.setBackgroundColor(Color.rgb(255, 229, 204));
        tex_udpt.setPadding(10, 10, 10, 10);
        tex_udpt.setGravity(Gravity.CENTER);
        tex_udpt.setLayoutParams(lp_udpt);
        rl_master.addView(tex_udpt);


        //Layout x
        RelativeLayout.LayoutParams lp_x = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_x.setMargins(10, 350, 10, 0);// left, top, right, bottom
        // Title x
        TextView title_x = new TextView(context);
        title_x.setText("");
        title_x.setBackgroundColor(Color.TRANSPARENT);
        title_x.setPadding(10, 10, 10, 10);
        title_x.setGravity(Gravity.CENTER);
        title_x.setTextColor(Color.WHITE);
        title_x.setTextSize(13);
        title_x.setLayoutParams(lp_x);
        rl_master.addView(title_x);


        alert.setPositiveButton("<--| ATRAS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                return;
            }
        });


        alert.setView(rl_master);
        // create an alert dialog
        AlertDialog alert1 = alert.create();
        alert1.show();

        //</editor-fold>
    }

    public void MainINFO(String ip,String tcp,String udp,String device,String os){
        //<editor-fold defaultstate="collapsed" desc="Own information about the device">
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);//outside activity
        int width = displaymetrics.widthPixels;


        AlertDialog.Builder alert = new AlertDialog.Builder(this.context);

        View view = new View(context);
        alert.setView(view);
        TextView title = new TextView(context);
        title.setText("OWN DEVICE INFO");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        alert.setCustomTitle(title);

        RelativeLayout rl_master = new RelativeLayout(this.context);

        //Layout INFO
        RelativeLayout.LayoutParams lp_info = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_info.setMargins(10, 10, 10, 0);// left, top, right, bottom
        // Title INFO
        TextView title_info = new TextView(context);
        title_info.setText("OWN INFORMATION");
        title_info.setBackgroundColor(Color.rgb(204,255,204));//(Color.LTGRAY);
        title_info.setPadding(10, 10, 10, 10);
        title_info.setGravity(Gravity.CENTER);
        title_info.setTextColor(Color.WHITE);
        title_info.setTextSize(13);
        title_info.setLayoutParams(lp_info);
        rl_master.addView(title_info);


        // Layout INFO --> TEXVIEW NAME
        RelativeLayout.LayoutParams lp_name = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_name.setMargins(10, 66, width/4 , 10);// left, top, right, bottom
        else
            lp_name.setMargins(10, 66, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW NAME
        TextView tex_name = new TextView(context);
        tex_name.setText("NAME:");
        tex_name.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_name.setPadding(10, 10, 10, 10);
        tex_name.setGravity(Gravity.CENTER);
        tex_name.setLayoutParams(lp_name);
        rl_master.addView(tex_name);

        // Layout INFO --> EditText NAME
        RelativeLayout.LayoutParams lp_namet = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_namet.setMargins(width/4 - 180, 66, 10 , 10);// left, top, right, bottom
        else
            lp_namet.setMargins(width/2 - 180, 66, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText NAME
        EditText tex_namet = new EditText(context);
        tex_namet.setText(device);
        tex_namet.setTextSize(16);
        tex_namet.setFocusable(false);
        tex_namet.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        tex_namet.setClickable(false);// user navigates with wheel and selects widget
        tex_namet.setBackgroundColor(Color.rgb(255, 229, 204));
        tex_namet.setPadding(10, 10, 10, 10);
        tex_namet.setGravity(Gravity.CENTER);
        tex_namet.setLayoutParams(lp_namet);
        rl_master.addView(tex_namet);

        // Layout INFO --> TEXVIEW OS
        RelativeLayout.LayoutParams lp_os = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_os.setMargins(10, 130, width/4 , 10);// left, top, right, bottom
        else
            lp_os.setMargins(10, 130, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW OS
        TextView tex_os = new TextView(context);
        tex_os.setText("OS: ");
        tex_os.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_os.setPadding(10, 10, 10, 10);
        tex_os.setGravity(Gravity.CENTER);
        tex_os.setLayoutParams(lp_os);
        rl_master.addView(tex_os);

        // Layout INFO --> EditText OS
        RelativeLayout.LayoutParams lp_ost = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_ost.setMargins(width/4 - 180, 130, 10 , 10);// left, top, right, bottom
        else
            lp_ost.setMargins(width/2 - 180, 130, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText OS
        EditText tex_ost = new EditText(context);
        tex_ost.setText(os);
        tex_ost.setTextSize(16);
        tex_ost.setFocusable(false);
        tex_ost.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        tex_ost.setClickable(false);// user navigates with wheel and selects widget
        tex_ost.setBackgroundColor(Color.rgb(255,229,204));
        tex_ost.setPadding(10, 10, 10, 10);
        tex_ost.setGravity(Gravity.CENTER);
        tex_ost.setLayoutParams(lp_ost);
        rl_master.addView(tex_ost);


        // Layout INFO --> TEXVIEW IP
        RelativeLayout.LayoutParams lp_ip = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_ip.setMargins(10, 194, width/4 , 10);// left, top, right, bottom
        else
            lp_ip.setMargins(10, 194, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW IP
        TextView tex_ip = new TextView(context);
        tex_ip.setText("IP: ");
        tex_ip.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_ip.setPadding(10, 10, 10, 10);
        tex_ip.setGravity(Gravity.CENTER);
        tex_ip.setLayoutParams(lp_ip);
        rl_master.addView(tex_ip);

        // Layout INFO --> EditText IP
        RelativeLayout.LayoutParams lp_ipt = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_ipt.setMargins(width/4 - 180, 194, 10 , 10);// left, top, right, bottom
        else
            lp_ipt.setMargins(width/2 - 180, 194, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText IP
        EditText tex_ipt = new EditText(context);
        tex_ipt.setText(ip);
        tex_ipt.setTextSize(16);
        tex_ipt.setFocusable(false);
        tex_ipt.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        tex_ipt.setClickable(false);// user navigates with wheel and selects widget
        tex_ipt.setBackgroundColor(Color.rgb(255, 229, 204));
        tex_ipt.setPadding(10, 10, 10, 10);
        tex_ipt.setGravity(Gravity.CENTER);
        tex_ipt.setLayoutParams(lp_ipt);
        rl_master.addView(tex_ipt);


        // Layout INFO --> TEXVIEW TCP
        RelativeLayout.LayoutParams lp_tcp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_tcp.setMargins(10, 258, width/4 , 10);// left, top, right, bottom
        else
            lp_tcp.setMargins(10, 258, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW TCP
        TextView tex_tcp = new TextView(context);
        tex_tcp.setText("TCP: ");
        tex_tcp.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_tcp.setPadding(10, 10, 10, 10);
        tex_tcp.setGravity(Gravity.CENTER);
        tex_tcp.setLayoutParams(lp_tcp);
        rl_master.addView(tex_tcp);

        // Layout INFO --> EditText TCP
        RelativeLayout.LayoutParams lp_tcpt = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_tcpt.setMargins(width/4 - 180, 258, 10 , 10);// left, top, right, bottom
        else
            lp_tcpt.setMargins(width/2 - 180, 258, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText TCP
        EditText tex_tcpt = new EditText(context);
        tex_tcpt.setText(tcp);
        tex_tcpt.setTextSize(16);
        tex_tcpt.setFocusable(false);
        tex_tcpt.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        tex_tcpt.setClickable(false);// user navigates with wheel and selects widget
        tex_tcpt.setBackgroundColor(Color.rgb(255, 229, 204));
        tex_tcpt.setPadding(10, 10, 10, 10);
        tex_tcpt.setGravity(Gravity.CENTER);
        tex_tcpt.setLayoutParams(lp_tcpt);
        rl_master.addView(tex_tcpt);

        // Layout INFO --> TEXVIEW UDP
        RelativeLayout.LayoutParams lp_udp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_udp.setMargins(10, 323, width/4 , 10);// left, top, right, bottom
        else
            lp_udp.setMargins(10, 323, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW UDP
        TextView tex_udp = new TextView(context);
        tex_udp.setText("UDP: ");
        tex_udp.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_udp.setPadding(10, 10, 10, 10);
        tex_udp.setGravity(Gravity.CENTER);
        tex_udp.setLayoutParams(lp_udp);
        rl_master.addView(tex_udp);

        // Layout INFO --> EditText UDP
        RelativeLayout.LayoutParams lp_udpt = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_udpt.setMargins(width/4 - 180, 323, 10 , 10);// left, top, right, bottom
        else
            lp_udpt.setMargins(width/2 - 180, 323, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText UDP
        EditText tex_udpt = new EditText(context);
        tex_udpt.setText(udp);
        tex_udpt.setTextSize(16);
        tex_udpt.setFocusable(false);
        tex_udpt.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        tex_udpt.setClickable(false);// user navigates with wheel and selects widget
        tex_udpt.setBackgroundColor(Color.rgb(255, 229, 204));
        tex_udpt.setPadding(10, 10, 10, 10);
        tex_udpt.setGravity(Gravity.CENTER);
        tex_udpt.setLayoutParams(lp_udpt);
        rl_master.addView(tex_udpt);


        //Layout x
        RelativeLayout.LayoutParams lp_x = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_x.setMargins(10, 350, 10, 0);// left, top, right, bottom
        // Title x
        TextView title_x = new TextView(context);
        title_x.setText("");
        title_x.setBackgroundColor(Color.TRANSPARENT);
        title_x.setPadding(10, 10, 10, 10);
        title_x.setGravity(Gravity.CENTER);
        title_x.setTextColor(Color.WHITE);
        title_x.setTextSize(13);
        title_x.setLayoutParams(lp_x);
        rl_master.addView(title_x);


        alert.setPositiveButton("<--| ATRAS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                return;
            }
        });


        alert.setView(rl_master);
        // create an alert dialog
        AlertDialog alert1 = alert.create();
        alert1.show();
        //</editor-fold>
    }

    public void MainConfigSettings(String ip,String tcp,String udp,String sstatustime,
                                   String statusdiscovery,String statusaccelerometer,String statusswipe){
        //<editor-fold defaultstate="collapsed" desc="Configuration of the Framework">
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);//outside activity
        int width = displaymetrics.widthPixels;


        AlertDialog.Builder alert = new AlertDialog.Builder(this.context);

        View view = new View(context);
        alert.setView(view);
        TextView title = new TextView(context);
        title.setText("CONFIGURATION OF THE FRAMEWORK");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        alert.setCustomTitle(title);

        RelativeLayout rl_master = new RelativeLayout(this.context);

        //Layout INFO
        RelativeLayout.LayoutParams lp_info = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_info.setMargins(10, 10, 10, 0);// left, top, right, bottom
        // Title INFO
        TextView title_info = new TextView(context);
        title_info.setText("INFORMATION ABOUT FRAMEWORK");
        title_info.setBackgroundColor(Color.rgb(204,255,204));//(Color.LTGRAY);
        title_info.setPadding(10, 10, 10, 10);
        title_info.setGravity(Gravity.CENTER);
        title_info.setTextColor(Color.WHITE);
        title_info.setTextSize(13);
        title_info.setLayoutParams(lp_info);
        rl_master.addView(title_info);


        // Layout INFO --> TEXVIEW IP
        RelativeLayout.LayoutParams lp_ip = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_ip.setMargins(10, 66, width/4 , 10);// left, top, right, bottom
        else
            lp_ip.setMargins(10, 66, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW IP
        TextView tex_ip = new TextView(context);
        tex_ip.setText("IP: ");
        tex_ip.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_ip.setPadding(10, 10, 10, 10);
        tex_ip.setGravity(Gravity.CENTER);
        tex_ip.setLayoutParams(lp_ip);
        rl_master.addView(tex_ip);

        // Layout INFO --> EditText IP
        RelativeLayout.LayoutParams lp_ipt = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_ipt.setMargins(width/4 - 180, 66, 10 , 10);// left, top, right, bottom
        else
            lp_ipt.setMargins(width/2 - 80, 66, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText IP
        EditText tex_ipt = new EditText(context);
        tex_ipt.setText(ip);
        tex_ipt.setTextSize(16);
        tex_ipt.setFocusable(false);
        tex_ipt.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        tex_ipt.setClickable(false);// user navigates with wheel and selects widget
        tex_ipt.setBackgroundColor(Color.rgb(255, 229, 204));
        tex_ipt.setPadding(10, 10, 10, 10);
        tex_ipt.setGravity(Gravity.CENTER);
        tex_ipt.setLayoutParams(lp_ipt);
        rl_master.addView(tex_ipt);


        // Layout INFO --> TEXVIEW TCP
        RelativeLayout.LayoutParams lp_tcp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_tcp.setMargins(10, 130, width/4 , 10);// left, top, right, bottom
        else
            lp_tcp.setMargins(10, 130, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW TCP
        TextView tex_tcp = new TextView(context);
        tex_tcp.setText("TCP: ");
        tex_tcp.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_tcp.setPadding(10, 10, 10, 10);
        tex_tcp.setGravity(Gravity.CENTER);
        tex_tcp.setLayoutParams(lp_tcp);
        rl_master.addView(tex_tcp);

        // Layout INFO --> EditText TCP
        RelativeLayout.LayoutParams lp_tcpt = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_tcpt.setMargins(width/4 - 180, 130, 10 , 10);// left, top, right, bottom
        else
            lp_tcpt.setMargins(width/2 - 80, 138, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText TCP
        EditText tex_tcpt = new EditText(context);
        tex_tcpt.setText(tcp);
        tex_tcpt.setTextSize(16);
        tex_tcpt.setFocusable(false);
        tex_tcpt.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        tex_tcpt.setClickable(false);// user navigates with wheel and selects widget
        tex_tcpt.setBackgroundColor(Color.rgb(255, 229, 204));
        tex_tcpt.setPadding(10, 10, 10, 10);
        tex_tcpt.setGravity(Gravity.CENTER);
        tex_tcpt.setLayoutParams(lp_tcpt);
        rl_master.addView(tex_tcpt);

        // Layout INFO --> TEXVIEW UDP
        RelativeLayout.LayoutParams lp_udp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_udp.setMargins(10, 194, width/4 , 10);// left, top, right, bottom
        else
            lp_udp.setMargins(10, 194, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW UDP
        TextView tex_udp = new TextView(context);
        tex_udp.setText("UDP: ");
        tex_udp.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_udp.setPadding(10, 10, 10, 10);
        tex_udp.setGravity(Gravity.CENTER);
        tex_udp.setLayoutParams(lp_udp);
        rl_master.addView(tex_udp);

        // Layout INFO --> EditText UDP
        RelativeLayout.LayoutParams lp_udpt = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_udpt.setMargins(width/4 - 180, 194, 10 , 10);// left, top, right, bottom
        else
            lp_udpt.setMargins(width/2 - 80, 194, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText UDP
        EditText tex_udpt = new EditText(context);
        tex_udpt.setText(udp);
        tex_udpt.setTextSize(16);
        tex_udpt.setFocusable(false);
        tex_udpt.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        tex_udpt.setClickable(false);// user navigates with wheel and selects widget
        tex_udpt.setBackgroundColor(Color.rgb(255, 229, 204));
        tex_udpt.setPadding(10, 10, 10, 10);
        tex_udpt.setGravity(Gravity.CENTER);
        tex_udpt.setLayoutParams(lp_udpt);
        rl_master.addView(tex_udpt);


        // Layout INFO --> TEXVIEW STATE-TIME
        RelativeLayout.LayoutParams lp_stime = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_stime.setMargins(10, 258, width/4 , 10);// left, top, right, bottom
        else
            lp_stime.setMargins(10, 258, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW STATE-TIME
        TextView tex_stime = new TextView(context);
        tex_stime.setText("TIME-STATE: ");
        tex_stime.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_stime.setPadding(10, 10, 10, 10);
        tex_stime.setGravity(Gravity.CENTER);
        tex_stime.setLayoutParams(lp_stime);
        rl_master.addView(tex_stime);

        // Layout INFO --> EditText STATE-TIME
        RelativeLayout.LayoutParams lp_stimet = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_stimet.setMargins(width/4 - 180, 258, 10 , 10);// left, top, right, bottom
        else
            lp_stimet.setMargins(width/2 - 80, 258, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText STATE-TIME
        final EditText tex_stimet = new EditText(context);
        tex_stimet.setText(sstatustime);
        tex_stimet.setTextSize(16);
        tex_stimet.setInputType(InputType.TYPE_CLASS_NUMBER);
        tex_stimet.setImeOptions(EditorInfo.IME_ACTION_DONE);
        //tex_stimet.setFocusable(false);
        //tex_stimet.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        //tex_stimet.setClickable(false);// user navigates with wheel and selects widget
        tex_stimet.setBackgroundColor(Color.rgb(255, 229, 204));
        tex_stimet.setPadding(10, 10, 10, 10);
        tex_stimet.setGravity(Gravity.CENTER);
        tex_stimet.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // If the event is a key-down event on the "enter" button
                if (i == EditorInfo.IME_ACTION_SEARCH ||
                        i == EditorInfo.IME_ACTION_DONE) {
                    Log.d(aps.getTAG(), "App:Configuration Framework" + "::Texto:"+textView.getText());

                    InputMethodManager input = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    input.hideSoftInputFromWindow(tex_stimet.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });
        tex_stimet.setLayoutParams(lp_stimet);
        rl_master.addView(tex_stimet);



        // Layout INFO --> TEXVIEW TIME-DISCOVERY
        RelativeLayout.LayoutParams lp_discovery = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_discovery.setMargins(10, 323, width/4 , 10);// left, top, right, bottom
        else
            lp_discovery.setMargins(10, 323, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW TIME-DISCOVERY
        TextView tex_discovery = new TextView(context);
        tex_discovery.setText("TIME-DISCOVERY: ");
        tex_discovery.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_discovery.setPadding(10, 10, 10, 10);
        tex_discovery.setGravity(Gravity.CENTER);
        tex_discovery.setLayoutParams(lp_discovery);
        rl_master.addView(tex_discovery);

        // Layout INFO --> EditText TIME-DISCOVERY
        RelativeLayout.LayoutParams lp_discoveryt = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_discoveryt.setMargins(width/4 - 180, 323, 10 , 10);// left, top, right, bottom
        else
            lp_discoveryt.setMargins(width/2 - 80, 323, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText TIME-DISCOVERY
        final EditText tex_discoveryt = new EditText(context);
        tex_discoveryt.setText(statusdiscovery);
        tex_discoveryt.setTextSize(16);
        tex_discoveryt.setInputType(InputType.TYPE_CLASS_NUMBER);
        tex_discoveryt.setImeOptions(EditorInfo.IME_ACTION_DONE);
        //tex_discoveryt.setFocusable(false);
        //tex_discoveryt.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        //tex_discoveryt.setClickable(false);// user navigates with wheel and selects widget
        tex_discoveryt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // If the event is a key-down event on the "enter" button
                if (i == EditorInfo.IME_ACTION_SEARCH ||
                        i == EditorInfo.IME_ACTION_DONE) {
                    Log.d(aps.getTAG(), "App:Configuration Framework" + "::Texto:"+textView.getText());

                    InputMethodManager input = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    input.hideSoftInputFromWindow(tex_discoveryt.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });
        tex_discoveryt.setBackgroundColor(Color.rgb(255, 229, 204));
        tex_discoveryt.setPadding(10, 10, 10, 10);
        tex_discoveryt.setGravity(Gravity.CENTER);
        tex_discoveryt.setLayoutParams(lp_discoveryt);
        rl_master.addView(tex_discoveryt);



        // Layout INFO --> TEXVIEW DISCOVERY-STOP
        RelativeLayout.LayoutParams lp_sdiscovery = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_sdiscovery.setMargins(10, 400, width/4 , 10);// left, top, right, bottom
        else
            lp_sdiscovery.setMargins(10, 400, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW DISCOVERY-STOP
        TextView tex_sdiscovery = new TextView(context);
        tex_sdiscovery.setText("STATUS-DISCOVERY: ");
        tex_sdiscovery.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_sdiscovery.setPadding(10, 10, 10, 10);
        tex_sdiscovery.setGravity(Gravity.CENTER);
        tex_sdiscovery.setLayoutParams(lp_sdiscovery);
        rl_master.addView(tex_sdiscovery);

        // Layout INFO --> EditText DISCOVERY-STOP
        RelativeLayout.LayoutParams lp_sdiscoveryt = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_sdiscoveryt.setMargins(width/4 - 180, 400, 10 , 10);// left, top, right, bottom
        else
            lp_sdiscoveryt.setMargins(width/2 - 80, 400, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText DISCOVERY-STOP
        Button tex_sdicoveryt = new Button(context);
        tex_sdicoveryt.setText("STOP");
        tex_sdicoveryt.setTextSize(16);
        //tex_sdicoveryt.setFocusable(false);
        //tex_sdicoveryt.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        //tex_sdicoveryt.setClickable(false);// user navigates with wheel and selects widget
        tex_sdicoveryt.setBackgroundColor(Color.rgb(204,255,229));
        tex_sdicoveryt.setPadding(10, 10, 10, 10);
        tex_sdicoveryt.setGravity(Gravity.CENTER);
        tex_sdicoveryt.setLayoutParams(lp_sdiscoveryt);
        rl_master.addView(tex_sdicoveryt);


        // Layout INFO --> TEXVIEW STATUS-STOP
        RelativeLayout.LayoutParams lp_sstate = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_sstate.setMargins(10, 500, width/4 , 10);// left, top, right, bottom
        else
            lp_sstate.setMargins(10, 500, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW STATUS-STOP
        TextView tex_sstate = new TextView(context);
        tex_sstate.setText("STATUS-STATE: ");
        tex_sstate.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_sstate.setPadding(10, 10, 10, 10);
        tex_sstate.setGravity(Gravity.CENTER);
        tex_sstate.setLayoutParams(lp_sstate);
        rl_master.addView(tex_sstate);

        // Layout INFO --> EditText STATUS-STOP
        RelativeLayout.LayoutParams lp_sstatust = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_sstatust.setMargins(width/4 - 180, 500, 10 , 10);// left, top, right, bottom
        else
            lp_sstatust.setMargins(width/2 - 80, 500, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText STATUS-STOP
        Button tex_sstatust = new Button(context);
        tex_sstatust.setText("STOP");
        tex_sstatust.setTextSize(16);
        //tex_sstatust.setFocusable(false);
        //tex_sstatust.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        //tex_sstatust.setClickable(false);// user navigates with wheel and selects widget
        tex_sstatust.setBackgroundColor(Color.rgb(204,255,229));
        tex_sstatust.setPadding(10, 10, 10, 10);
        tex_sstatust.setGravity(Gravity.CENTER);
        tex_sstatust.setLayoutParams(lp_sstatust);
        rl_master.addView(tex_sstatust);


        // Layout INFO --> TEXVIEW ACCELEROMETER
        RelativeLayout.LayoutParams lp_saccelerometer = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_saccelerometer.setMargins(10, 580, width/4 , 10);// left, top, right, bottom
        else
            lp_saccelerometer.setMargins(10, 580, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW ACCELEROMETER
        TextView tex_saccelerometer = new TextView(context);
        tex_saccelerometer.setText("STATUS-ACCELEROMETER: ");
        tex_saccelerometer.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_saccelerometer.setPadding(10, 10, 10, 10);
        tex_saccelerometer.setGravity(Gravity.CENTER);
        tex_saccelerometer.setLayoutParams(lp_saccelerometer);
        rl_master.addView(tex_saccelerometer);

        // Layout INFO --> EditText ACCELEROMETER
        RelativeLayout.LayoutParams lp_saccelerometert = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_saccelerometert.setMargins(width/4 - 180 , 610, 10 , 10);// left, top, right, bottom
        else
            lp_saccelerometert.setMargins(width/2 - 80, 610, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText ACCELEROMETER
        EditText tex_saccelerometert = new EditText(context);
        tex_saccelerometert.setText(statusaccelerometer);
        tex_saccelerometert.setTextSize(16);
        tex_saccelerometert.setFocusable(false);
        tex_saccelerometert.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        tex_saccelerometert.setClickable(false);// user navigates with wheel and selects widget
        tex_saccelerometert.setBackgroundColor(Color.rgb(255, 229, 204));
        tex_saccelerometert.setPadding(10, 10, 10, 10);
        tex_saccelerometert.setGravity(Gravity.CENTER);
        tex_saccelerometert.setLayoutParams(lp_saccelerometert);
        rl_master.addView(tex_saccelerometert);


        // Layout INFO --> TEXVIEW SWIPE
        RelativeLayout.LayoutParams lp_sswipe = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_sswipe.setMargins(10, 680, width/4 , 10);// left, top, right, bottom
        else
            lp_sswipe.setMargins(10, 680, width/2 , 10);// left, top, right, bottom
        // Title INFO --> TEXVIEW SWIPE
        TextView tex_sswipe = new TextView(context);
        tex_sswipe.setText("STATUS-SWIPE: ");
        tex_sswipe.setTextSize(16);
        //bColor.setBackgroundColor(Color.rgb(179, 255, 255));
        tex_sswipe.setPadding(10, 10, 10, 10);
        tex_sswipe.setGravity(Gravity.CENTER);
        tex_sswipe.setLayoutParams(lp_sswipe);
        rl_master.addView(tex_sswipe);

        // Layout INFO --> EditText SWIPE
        RelativeLayout.LayoutParams lp_sswipet = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(isTabletorPhone())
            lp_sswipet.setMargins(width/4 - 180, 680, 10 , 10);// left, top, right, bottom
        else
            lp_sswipet.setMargins(width/2 - 80, 680, 10 , 10);// left, top, right, bottom
        // Title INFO --> EditText SWIPE
        EditText tex_sswipet = new EditText(context);
        tex_sswipet.setText(statusswipe);
        tex_sswipet.setTextSize(16);
        tex_sswipet.setFocusable(false);
        tex_sswipet.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        tex_sswipet.setClickable(false);// user navigates with wheel and selects widget
        tex_sswipet.setBackgroundColor(Color.rgb(255, 229, 204));
        tex_sswipet.setPadding(10, 10, 10, 10);
        tex_sswipet.setGravity(Gravity.CENTER);
        tex_sswipet.setLayoutParams(lp_sswipet);
        rl_master.addView(tex_sswipet);



        //Layout x
        RelativeLayout.LayoutParams lp_x = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_x.setMargins(10, 700, 10, 0);// left, top, right, bottom
        // Title x
        TextView title_x = new TextView(context);
        title_x.setText("");
        title_x.setBackgroundColor(Color.TRANSPARENT);
        title_x.setPadding(10, 10, 10, 10);
        title_x.setGravity(Gravity.CENTER);
        title_x.setTextColor(Color.WHITE);
        title_x.setTextSize(13);
        title_x.setLayoutParams(lp_x);
        rl_master.addView(title_x);


        alert.setPositiveButton("<--| ATRAS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                return;
            }
        });


        alert.setView(rl_master);
        // create an alert dialog
        AlertDialog alert1 = alert.create();
        alert1.show();
        //</editor-fold>
    }

    /**
     * function to add into root view UUID
     */
    private void setID(){
        //<editor-fold defaultstate="collapsed" desc="UUID">
        RelativeLayout.LayoutParams lpUUID = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        TextView tUUID = new TextView(this.context);
        tUUID.setLayoutParams(lpUUID);
        tUUID.setText("ID: "+aps.getiDSession());
        tUUID.setBackgroundColor(Color.TRANSPARENT);
        tUUID.setTextColor(Color.BLACK);
        tUUID.setPadding(25, 0, 0, 0);// in pixels (left, top, right, bottom)
        tUUID.setId(aps.getiDUUID());
        relativeLayout.addView(tUUID);
        ((Activity) this.context).setContentView(relativeLayout, rlp);
        //</editor-fold>
    }
    //setter
    public void seteID(UUID id){
        aps.setiD(id);
        TextView tUUID = (TextView) ((Activity) context).findViewById(aps.getiDUUID());
        tUUID.setText("ID: " + aps.getiDSession().toString().toUpperCase());
    }

    public void onChangedScreen(){
        //<editor-fold defaultstate="collapsed" desc="ON SCREEN CHANGED">
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);//outside activity
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        ArrayList<List<Integer>> myPointsList = new ArrayList<>();

        if (width <= 480 && height<= 820) {//min tam for screen
            myPointsList.add(Arrays.asList(width - 100, width - 170, width - 240, width - 100));//x
            myPointsList.add(Arrays.asList(height - 100, height - 100, height - 100, height - 170));//y
       }else {
            myPointsList.add(Arrays.asList(width - 120, width - 220, width - 320, width - 120));//x
            myPointsList.add(Arrays.asList(height - 120, height - 120, height - 120, height - 220));//y
        }

        int[] bsettings = aps.getiDSettings();
        for (int i = 0; i<bsettings.length;++i){
            CircularButton c = (CircularButton) ((Activity) context).findViewById(bsettings[i]);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(myPointsList.get(0).get(i), myPointsList.get(1).get(i), 0, 0);
            c.setLayoutParams(lp);

        }

        //checar  si es landscape o portrait ya que se llama esta parte
        // hay que verificar si es landscape o portrait
        if (isTabletorPhone()) {//tablet

            /*
            int[] PA1 = new int[]{width/6,0};
            int[] PA2 = new int[]{width/2 + width/6,0};
            int[] PB1 = new int[]{width/6,height-25};
            int[] PB2 = new int[]{width/2 + width/6,height-25};
            int[] PC1 = new int[]{0,height/4};
            int[] PC2 = new int[]{width-25,height/4};

            int[] A1 = new int[]{widtht/4,22};
            int[] A2 = new int[]{widtht/4,22};
            int[] B1 = new int[]{widtht/4,22};
            int[] B2 = new int[]{widtht/4,22};
            int[] C1 = new int[]{22,heigh/2};
            int[] C2 = new int[]{22,heigh/2};
             */
            ArrayList<List<Integer>> myPointsConnected = new ArrayList<>();
            myPointsConnected.add(Arrays.asList(width/6,width/2 + width/6,width/6,width/2 + width/6,0,width-25));//x
            myPointsConnected.add(Arrays.asList(0,0,height-25,height-25,height/4,height/4));//y

            //myPointsConnected.add(Arrays.asList(width-25,width-25,0,0,width/6,width/2+width/6));//x
            //myPointsConnected.add(Arrays.asList(height/6,height/2 +height/6,height/6,height/2,0,height-25));//y

            int[] tablet = aps.getiDConnectedForTablet();
            for (int i = 0; i<tablet.length;++i){
                ImageView img = (ImageView) ((Activity) context).findViewById(tablet[i]);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(myPointsConnected.get(0).get(i), myPointsConnected.get(1).get(i), 0, 0);
                img.setLayoutParams(lp);

            }
        }
        else {
            ArrayList<List<Integer>> myPointsConnected = new ArrayList<>();
            myPointsConnected.add(Arrays.asList(width/4, width/4, 0, width-25));//x
            myPointsConnected.add(Arrays.asList(0, height-25,25, 25));//y

            //int[] PA1 = new int[]{width/4,0};
            //int[] PB1 = new int[]{width/4,height-25};
            //int[] PC1 = new int[]{0,height/4};
            //int[] PC2 = new int[]{width-25,height/4};

            int[] phone = aps.getiDConnectedForPhone();
            for (int i = 0; i<phone.length;++i){
                ImageView img = (ImageView) ((Activity) context).findViewById(phone[i]);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(myPointsConnected.get(0).get(i), myPointsConnected.get(1).get(i), 0, 0);
                img.setLayoutParams(lp);

            }


        }







        //implementar un array mutable o dinamico  que guarde los bloques y con sus id y que se
        //cambien sobre la rotacion y si se rota y el elemnto se va en tonces que se mueva a una
        //area segura
//</editor-fold>
    }

    private boolean isTabletorPhone()
    {
        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels / displayMetrics.densityDpi;
        int height = displayMetrics.heightPixels / displayMetrics.densityDpi;

        double screenDiagonal = Math.sqrt(width * width + height * height);
        return (screenDiagonal >= 6.5);
    }

    private void setConnectedDevice(int[] color, int id){

        //Log.d(aps.getTAG(), "colorConectado:" + color);

        //setConnectedDeviceColor(color,id);

        /*if (dir.equals("TA1")){
            setConnectedDeviceColor(0,id);
        }
        else if (dir.equals("TA2")){
            setConnectedDeviceColor(1,id);
        }
        else if (dir.equals("TB1")){
            setConnectedDeviceColor(2,id);
        }
        else if (dir.equals("TB2")){
            setConnectedDeviceColor(3,id);
        }
        else if (dir.equals("TC1")){
            setConnectedDeviceColor(4,id);
        }
        else if (dir.equals("TC2")){
            setConnectedDeviceColor(5,id);
        }
        else if (dir.equals("PA1")){
            setConnectedDeviceColor(0,id);
        }
        else if (dir.equals("PB1")){
            setConnectedDeviceColor(1,id);
        }
        else if (dir.equals("PC1")){
            setConnectedDeviceColor(2,id);
        }
        else if (dir.equals("PC2")){
            setConnectedDeviceColor(3,id);
        }*/


    }
    public void setConnectedDeviceColor(int[] p, int id){

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        if (isTabletorPhone()) {

            int[] A1 = new int[]{width/4,22};
            int[] A2 = new int[]{width/4,22};
            int[] B1 = new int[]{width/4,22};
            int[] B2 = new int[]{width/4,22};
            int[] C1 = new int[]{22,height/2};
            int[] C2 = new int[]{22,height/2};

            //int[] colores = aps.getColorConnectedForTablet();
            //int[] colores = aps.getColorConnected();
            int[] idTablet = aps.getiDConnectedForTablet();
            int[] bitmapx = new int[]{A1[0],A2[0],B1[0],B2[0],C1[0],C2[0]};
            int[] bitmapy = new int[]{A1[1],A2[1],B1[1],B2[1],C1[1],C2[1]};

            ImageView img = (ImageView) ((Activity) context).findViewById(idTablet[id]);

            Bitmap bitmap = Bitmap.createBitmap(bitmapx[id], bitmapy[id], Bitmap.Config.ARGB_8888);
            //bitmap.eraseColor(colores[p]);
            bitmap.eraseColor(Color.rgb(p[0],p[1],p[2]));
            bitmap = addBorderToBitmap(bitmap, 3, Color.GRAY);

            img.setImageBitmap(bitmap);
        }
        else{
            int[] A1 = new int[]{width/2,22};
            int[] B1 = new int[]{width/2,22};
            int[] C1 = new int[]{22,height/2};
            int[] C2 = new int[]{22,height/2};


            //int[] colores = aps.getColorConnectedForPhone();
            int[] colores = aps.getColorConnected();
            int[] iDPhone = aps.getiDConnectedForPhone();
            int[] bitmapx = new int[]{A1[0], B1[0], C1[0], C2[0]};
            int[] bitmapy = new int[]{A1[1], B1[1], C1[1], C2[1]};

            ImageView img = (ImageView) ((Activity) context).findViewById(iDPhone[id]);

            Bitmap bitmap = Bitmap.createBitmap(bitmapx[id], bitmapy[id], Bitmap.Config.ARGB_8888);
            //bitmap.eraseColor(colores[p]);
            bitmap.eraseColor(Color.rgb(p[0],p[1],p[2]));
            bitmap = addBorderToBitmap(bitmap, 3, Color.GRAY);

            img.setImageBitmap(bitmap);
        }

    }

    public void setDisconnectedDevice(String direction){

            if (isTabletorPhone()) {
                switch (direction) {
                    case "TA1":
                        setDisconnectedDeviceColor(0);
                        break;
                    case "TA2":
                        setDisconnectedDeviceColor(1);
                        break;
                    case "TB1":
                        setDisconnectedDeviceColor(2);
                        break;
                    case "TB2":
                        setDisconnectedDeviceColor(3);
                        break;
                    case "TC1":
                        setDisconnectedDeviceColor(4);
                        break;
                    case "TC2":
                        setDisconnectedDeviceColor(5);
                        break;
                    default:
                        break;
                }
            } else {
                switch (direction) {
                    case "PA1":
                        setDisconnectedDeviceColor(0);
                        break;
                    case "PB1":
                        setDisconnectedDeviceColor(1);
                        break;
                    case "PC1":
                        setDisconnectedDeviceColor(2);
                        break;
                    case "PC2":
                        setDisconnectedDeviceColor(3);
                        break;
                    default:
                        break;
                }
            }
    }
    private void setDisconnectedDeviceColor(int p){

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        if (isTabletorPhone()) {

            int[] A1 = new int[]{width/4,22};
            int[] A2 = new int[]{width/4,22};
            int[] B1 = new int[]{width/4,22};
            int[] B2 = new int[]{width/4,22};
            int[] C1 = new int[]{22,height/2};
            int[] C2 = new int[]{22,height/2};

            int[] idTablet = aps.getiDConnectedForTablet();
            int[] bitmapx = new int[]{A1[0],A2[0],B1[0],B2[0],C1[0],C2[0]};
            int[] bitmapy = new int[]{A1[1],A2[1],B1[1],B2[1],C1[1],C2[1]};

            ImageView img = (ImageView) ((Activity) context).findViewById(idTablet[p]);

            Bitmap bitmap = Bitmap.createBitmap(bitmapx[p], bitmapy[p], Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.TRANSPARENT);
            bitmap = addBorderToBitmap(bitmap, 3, Color.TRANSPARENT);

            img.setImageBitmap(bitmap);
        }
        else{
            int[] A1 = new int[]{width/2,22};
            int[] B1 = new int[]{width/2,22};
            int[] C1 = new int[]{22,height/2};
            int[] C2 = new int[]{22,height/2};

            int[] iDPhone = aps.getiDConnectedForPhone();
            int[] bitmapx = new int[]{A1[0], B1[0], C1[0], C2[0]};
            int[] bitmapy = new int[]{A1[1], B1[1], C1[1], C2[1]};

            ImageView img = (ImageView) ((Activity) context).findViewById(iDPhone[p]);

            Bitmap bitmap = Bitmap.createBitmap(bitmapx[p], bitmapy[p], Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.TRANSPARENT);
            bitmap = addBorderToBitmap(bitmap, 3, Color.TRANSPARENT);

            img.setImageBitmap(bitmap);
        }

    }

    public void setBackGroundColor(int[] bc){
        aps.setBColor(bc);

        int[] ncolor = aps.getBColor();
        int r = (int) ncolor[0];
        int g = (int) ncolor[1];
        int b = (int) ncolor[2];
        relativeLayout.setBackgroundColor(Color.argb(230,r,g,b));

        //relativeLayout.setBackground(aps.getNewBorder());
    }
    public void setExampleWorkTogether(ExampleWorkTogether exampleWorkTogether){
        this.exampleWorkTogether = exampleWorkTogether;
    }
    //return name of device
    public String getNameDevice(){
        return aps.getDeviceName();
    }
    //return TAG
    public String getTAG() {
        return aps.getTAG();
    }
    //return BackGround Color
    public int[] getBackGroundColor(){
        return aps.getBColor();
    }
    public UUID getUUID(){
        return aps.getiDSession();
    }

    public void bloquesExternos(int x,int y,String text,int[] color, String ip){


        DisplayMetrics displaymetricst = new DisplayMetrics();
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(displaymetricst);//outside activity
        int widtht = displaymetricst.widthPixels;
        int height = displaymetricst.heightPixels;


        Paint paint = new Paint();
        Rect bounds = new Rect();

        int text_height = 0;
        int text_width = 0;

        paint.setTypeface(Typeface.DEFAULT);// your preference here
        if (widtht <= 480)
            paint.setTextSize(48);// have this the same as your text size
        else
            paint.setTextSize(54);
        //String text = "WORK";

        paint.getTextBounds(text, 0, text.length(), bounds);

        text_height =  bounds.height();
        text_width =  bounds.width();


        List<HashMap<String, String>> connected_copy = SharedResources.copy_CONNECTED_DEVICES();
        HashMap<String, String> map_connected = connected_copy.get(0);
        String side = "";
        for (Map.Entry<String,String> entry: map_connected.entrySet()){
                if (entry.getValue().equals(ip))
                    side = entry.getKey();
            }

        int xp = 30,yp = 30;

        if (isTabletorPhone()){
            if (side.equals("TC1")) {
                //xp = 10 + ((int)(text_width * 0.75) +10);
                xp = 10 + ((int)(text_width * 0.50));
                yp = height/2;
            }
            else if (side.equals("TC2")) {
                //xp = widtht - ((int)(text_width * 0.75) +10);
                xp = widtht - ((int)(text_width * 0.50));
                yp = height/2;
            }
            else if (side.equals("TA1")) {
                xp = widtht/4;
                yp = 60;
            }
            else if (side.equals("TA2")) {
                xp = widtht/4 +  widtht/2;
                yp = 60;
            }
            else if (side.equals("TB1")) {
                xp = widtht/4;
                yp = height - ((int)(text_height* 0.75 ) +60);
            }
            else if (side.equals("TB2")) {
                xp = widtht/4 +  widtht/2;
                yp =height - ((int)(text_height* 0.75 ) +60);
            }

        }else{
            if (side.equals("PC1")) {
                //xp = 10 + ((int)(text_width * 0.75) +10);
                xp = 10 + (int) (text_width * 0.5);
                yp = height/2;
            }
            else if (side.equals("PC2")) {
                //xp = widtht - ((int)(text_width * 0.75));
                xp = widtht - ((int)(text_width * 0.50));
                yp = height/2;
            }
            else if (side.equals("PA1")) {
                xp = widtht/2;
                yp = 60;
            }
            else if (side.equals("PB1")) {
                xp = widtht/2;
                yp =  height - ((int)(text_height* 0.75 ) +60);
            }
        }

        // el mas 30 es para que no lo agregue en la zona limite
        //despues cuando esten los 4 lados modificar
        addBlockParamsExtern(0, xp, yp, text, color);
    }

    public void  restoreObjects(String obj,String ip){

        if (!obj.equals("")) {
            String[] objStatus = obj.split("#");

            for (int i = 0; i < objStatus.length; i++) {
                //Log.d(aps.getTAG(), "BLOOOO:" + objStatus);
                String[] objStatusOne = objStatus[i].split("=");
                String text = objStatusOne[0];
                String[] color = objStatusOne[1].split(":");
                final int[] colorRGB = new int[color.length];//array color rgb received
                colorRGB[0] = Integer.parseInt(color[0]);//R
                colorRGB[1] = Integer.parseInt(color[1]);//G
                colorRGB[2] = Integer.parseInt(color[2]);//B

                bloquesExternosRestore(0, 0, text, colorRGB, ip);
            }
        }
    }
    public void bloquesExternosRestore(int x,int y,String text,int[] color, String ip){

        DisplayMetrics displaymetricst = new DisplayMetrics();
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(displaymetricst);//outside activity
        int widtht = displaymetricst.widthPixels;
        int height = displaymetricst.heightPixels;


        Paint paint = new Paint();
        Rect bounds = new Rect();

        int text_height = 0;
        int text_width = 0;

        paint.setTypeface(Typeface.DEFAULT);// your preference here
        if (widtht <= 480)
            paint.setTextSize(48);// have this the same as your text size
        else
            paint.setTextSize(54);


        paint.getTextBounds(text, 0, text.length(), bounds);

        text_height =  bounds.height();
        text_width =  bounds.width();

        int xp = widtht/2 - text_width;
        int yp = height/2 - text_height;

        addBlockParamsExtern(0, xp, yp, text, color);
    }

}
