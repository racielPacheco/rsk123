package com.cinvestav.worktogether.gestures.accelerometer;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.cinvestav.worktogether.global.DeviceFeatures;
import com.cinvestav.worktogether.global.SharedResources;
import com.cinvestav.worktogether.global.Utilities;
import com.cinvestav.worktogether.services.couplingcontrol.CouplingSettings;
import com.cinvestav.worktogether.services.couplingcontrol.ListenOnePairingRequest;
import com.cinvestav.worktogether.services.couplingcontrol.SendOnePairingRequest;
import com.cinvestav.worktogether.services.updatedistribution.CustomListener;
import com.cinvestav.worktogether.services.updatedistribution.UpdateSettings;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author
 */
public class AccelerometerGesture {
    /*
        Variables para la utilización del acelerómetro y el manejo del hilo que realiza las mediciones
    */
    private final SensorManager                 mSensorManager;
    private final Sensor                        mAccelerometer;
    private final Handler                       handler;
    private final GetDirectionWithAccelerometer listener;
    private final int                           SAMPLING_TIME;
    private String                              gestureDirection;
    /*
        Variables para el bloqueo de la pantalla durante el gesto del acelerómetro1
    */
    private final Activity                      mainActivity;
    private int                                 rotation;
    /*
        Variables utilizadas en la clase GetDirectionWithAccelerometer
    */
    private boolean         first_measure;
    private float           meanX, meanY;
    private final int       axisSwap[][] = { { 1, -1, 0, 1 },  // ROTATION_0 
                                             {-1, -1, 1, 0 },  // ROTATION_90 
                                             {-1, 1, 0, 1 },   // ROTATION_180 
                                             { 1, 1, 1, 0 } }; // ROTATION_270
    private final float     ABOVE_THIS_IS_NOISE     = 9.8f; // Cota máxima para considerar si la medición es válida o es ruido (valor de la gravedad)
    private final float     MIN_VALID_INCLINATION   = 2.0f; // Proporción de la gravedad que debe marcar el acelerómetro en un eje para considerar
                                                            // que la inclinación es válida e intencional hacia ese lado
    
    private boolean         accelerometerRunning;
    
    public AccelerometerGesture(Activity MainActivity){
        //<editor-fold defaultstate="collapsed" desc="Constructor">
        this.mainActivity       = MainActivity;
        listener                = new GetDirectionWithAccelerometer();
        handler                 = new Handler();
        mSensorManager          = (SensorManager) MainActivity.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer          = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelerometerRunning    = false;
        SAMPLING_TIME           = 1000;
        //</editor-fold>
    }
    
    public void onPause(){
        mSensorManager.unregisterListener(listener);
    }
    
    public boolean validateGesture( final CouplingSettings coupling_settings, final UpdateSettings updating_settings, final MotionEvent event, final boolean longClickDetected, final DeviceFeatures features, final CustomListener customListener){
        //<editor-fold defaultstate="collapsed" desc="validateGesture">
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        
        if( !accelerometerRunning && action == MotionEvent.ACTION_UP && longClickDetected ){
            try{
                accelerometerRunning = true;
                first_measure        = true;
                // Bloquear la orientación para evitar que el dispositivo la rote cuando se está realizando el acoplamiento
                Utilities.enableScreenRotation(mainActivity, false);
                rotation = Utilities.getRotation(mainActivity);
                // Tomar mediciones del acelerómetro por 1 segundo y pararlo con el Handler
                mSensorManager.registerListener(listener, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                handler.postDelayed( new Runnable(){
                    public void run() {
                        mSensorManager.unregisterListener(listener);
                        boolean validGesture = false;
                        // Determinar hacia qué lado está inclinado el dispositivo, el eje que presente la mayor inclinación será el que se acoplará
                        if (Math.abs(meanX) > Math.abs(meanY)) {
                            // Ahora determinar el lado de ese eje a acoplar
                            if (meanX < -MIN_VALID_INCLINATION){         gestureDirection = "R"; validGesture = true;}
                            else if (meanX > MIN_VALID_INCLINATION){     gestureDirection = "L"; validGesture = true;}
                        } else {
                            if (meanY < -MIN_VALID_INCLINATION){         gestureDirection = "D"; validGesture = true;}
                            else if (meanY > MIN_VALID_INCLINATION){     gestureDirection = "U"; validGesture = true;}
                        }
                        
                        // Si el seguro está puesto, existe alguna solicitud ejecutándose, i.e. no se puede enviar otra solicitud cuando aún
                        // se sigue esperando la solicitud de otro dispositivo ( socket.receive(packet) en ListenPairingRequest).
                        if( validGesture /*&& !SharedResources.LOCKED_REQUESTS */){
                            //SharedResources.LOCKED_REQUESTS = true;
                            new ListenOnePairingRequest( mainActivity, coupling_settings, updating_settings, gestureDirection, features, customListener ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            new SendOnePairingRequest( mainActivity, coupling_settings, updating_settings, gestureDirection, features ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            Log.d(Utilities.TAG, "SENDING PARING REQUEST BY --> ACCELEROMETER");
                            //toToast("ENVIANDO PETICION DE ACOPLAMIENTO");
                            somethingHappenedOne();
                        } else{
                            Log.d(Utilities.TAG, "SAFETY LOCK ON OR INCORRECT ACCELEROMETER GESTURE");
                            //toToast("SEGURO PUESTO O GESTO DE INCLINACION INCORRECTO");
                            somethingHappenedTwo();
                            //Log.d(Utilities.TAG, "LOCK:"+ SharedResources.LOCKED_REQUESTS);
                        }
                        accelerometerRunning    = false;
                        try {
                            //Log.d(Utilities.TAG, "WAITING 2.5 SECONDS ....");
                            Thread.sleep(2500);
                            //Log.d(Utilities.TAG, "READY!!");
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AccelerometerGesture.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            Utilities.enableScreenRotation(mainActivity, true);
                        }
                    }
                }, SAMPLING_TIME);
            }catch(Exception e){
                Log.d(Utilities.TAG, e.toString());
            }
        }
        //</editor-fold>
        return true;
    }
    
    private class GetDirectionWithAccelerometer implements SensorEventListener {
        //<editor-fold defaultstate="collapsed" desc="Obtener el promedio del valor de la gravedad en X y Y">
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x;// = event.values[0];
            float y;// = event.values[1];
            
            // Transformar los puntos obtenidos de acuerdo a la orientación de la pantalla
            x = (float) axisSwap[rotation][0] * event.values[ axisSwap[rotation][2]];
            y = (float) axisSwap[rotation][1] * event.values[ axisSwap[rotation][3]];
            
            if (first_measure) {
                // Se busca tener un valor válido inicial, evitamos picos o desplazamientos bruscos (ruido)
                if (Math.abs(x) > ABOVE_THIS_IS_NOISE || Math.abs(y) > ABOVE_THIS_IS_NOISE) {
                    return;
                }
                meanX = x;
                meanY = y;
                first_measure = false;
            } else {
                // Filtro para evitar medidas del acelerómetro que se disparan repentinamente (ruido)
                if (Math.abs(x) > ABOVE_THIS_IS_NOISE || Math.abs(y) > ABOVE_THIS_IS_NOISE) {
                    return;
                }
                // Cálculo del promedio
                meanX = (meanX + x) / 2;
                meanY = (meanY + y) / 2;
                //Log.d(Utilities.TAG, "meanX: " + meanX + ", meanY: " + meanY);
            }
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {}
        //</editor-fold>
    }
    public void toToast(String msg){
        //<editor-fold defaultstate="collapsed" desc="TOAST">
        Toast toast = Toast.makeText(this.mainActivity, msg, Toast.LENGTH_SHORT);
        toast.show();
        //</editor-fold>
    }
    private void somethingHappenedOne()
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        toToast("ENVIANDO PETICION DE ACOPLAMIENTO");
                    }
                }
        );
    }
    private void somethingHappenedTwo()
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        toToast("SEGURO PUESTO O GESTO DE INCLINACION INCORRECTO");
                    }
                }
        );
    }
}
