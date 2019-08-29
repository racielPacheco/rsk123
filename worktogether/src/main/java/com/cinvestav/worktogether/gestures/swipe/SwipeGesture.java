package com.cinvestav.worktogether.gestures.swipe;

import android.app.Activity;
import android.os.AsyncTask;
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

/**
 * Created by
 */
public class SwipeGesture {
    
    private int             MIN_PINCH_PX_DISTANCE; // En px
    private final Activity  mainActivity;
    
    //<editor-fold defaultstate="collapsed" desc="Constructores, setters y getters">
    public SwipeGesture(Activity MainActivity) {
        this.mainActivity       = MainActivity;
        MIN_PINCH_PX_DISTANCE   = 100;
    }
    
    public SwipeGesture(Activity MainActivity, int min_pinch_px_distance) {
        this.mainActivity       = MainActivity;
        MIN_PINCH_PX_DISTANCE   = min_pinch_px_distance;
    }
    
    public int getMIN_PINCH_PX_DISTANCE() {
        return MIN_PINCH_PX_DISTANCE;
    }
    
    public void setMIN_PINCH_PX_DISTANCE(int MIN_PINCH_PX_DISTANCE) {
        this.MIN_PINCH_PX_DISTANCE = MIN_PINCH_PX_DISTANCE;
    }
//</editor-fold>
    
    private int downX, downY;          // Valores auxiliares que respaldan las coordenadas en ACTION_DOWN
    
    private String gestureDirection; // Dirección final del gesto.

    /**
     * <br/><br/>Pinch check implementation.
     * 
     * @param coupling_settings (settings to be used by the coupling intent services)
     * @param updating_settings (settings to be used by the coupling intent services (server ports))
     * @param event (MotionEvent of the current touch event)
     * @param longClickDetected (boolean obtained through LongClickDetectorObject.verifyLongClick(MotionEventObj))
     * @param features (DeviceFeatures that will be sent along the coupling intent message and stored if such intent is valid)
     * @param customListener (Part of the GerberaExample app. This listener is defined in gerbera.services.updatedistribution.CustomListener)
     * @return An object which contains the results of processing the current event. This will depend upon the gesture
     * analyzed.
     */
    public boolean validateGesture( final CouplingSettings coupling_settings, final UpdateSettings updating_settings, final MotionEvent event, final boolean longClickDetected, final DeviceFeatures features,final CustomListener customListener) {
        //<editor-fold defaultstate="collapsed" desc="validateGesture">
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                // Guardar coordenadas cuando comienza el clic para determinar si el gesto es correcto y si se 
                // comenzó con un clic largo.
                downX = (int) event.getX();
                downY = (int) event.getY();
                break;
                
            case MotionEvent.ACTION_UP:
                if( longClickDetected ) {
                    // Revisar si existió un gesto de tipo pinch
                    int deltaX = downX - (int) event.getX();
                    int deltaY = downY - (int) event.getY();
                    boolean validGesture = false;

                    if (Math.abs(deltaX) > MIN_PINCH_PX_DISTANCE) { // Pinch horizontal
                        if (deltaX < 0)
                            gestureDirection = "R"; // right
                        else
                            gestureDirection = "L"; // left
                        validGesture = true;
                    } else if (Math.abs(deltaY) > MIN_PINCH_PX_DISTANCE) { //Pinch vertical
                        if (deltaY < 0)
                            gestureDirection = "D"; // down
                        else
                            gestureDirection = "U"; // up
                        validGesture = true;
                    }
                    // Si el seguro está puesto, existe alguna solicitud ejecutándose, i.e. no se puede enviar otra solicitud cuando aún
                    // se sigue esperando la solicitud de otro dispositivo ( socket.receive(packet) en ListenPairingRequest).
                    if ( validGesture /*&& !SharedResources.LOCKED_REQUESTS*/ ) {
                        //SharedResources.LOCKED_REQUESTS = true;

                        mainActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                new ListenOnePairingRequest(mainActivity, coupling_settings, updating_settings, gestureDirection, features, customListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        });


                        //new ListenOnePairingRequest( mainActivity, coupling_settings, updating_settings, gestureDirection, features, customListener ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        new SendOnePairingRequest( mainActivity, coupling_settings, updating_settings, gestureDirection, features).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        Log.d(Utilities.TAG, "SENDING PARING REQUEST BY --> SWIPE");
                        toToast("ENVIANDO PETICION DE ACOPLAMIENTO");
                        return true;
                    } else {
                        //SharedResources.LOCKED_REQUESTS = false;
                        Log.d(Utilities.TAG, "SAFETY LOCK ON OR INCORRECT SWIPE GESTURE");
                        toToast("SEGURO PUESTO O GESTO DE ARRASTRE INCORRECTO");
                        //Log.d(Utilities.TAG, "LOCK:"+ SharedResources.LOCKED_REQUESTS);
                    }
                }
                break;
        }
        //</editor-fold>
        return false;
    }
    public void toToast(String msg){
        //<editor-fold defaultstate="collapsed" desc="TOAST">
        Toast toast = Toast.makeText(this.mainActivity, msg, Toast.LENGTH_SHORT);
        toast.show();
        //</editor-fold>
    }
}