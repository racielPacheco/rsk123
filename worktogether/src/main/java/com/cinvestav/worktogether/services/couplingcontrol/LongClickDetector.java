package com.cinvestav.worktogether.services.couplingcontrol;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;
//import com.cinvestav.worktogether.collab.R;
import com.cinvestav.worktogether.R;
import com.cinvestav.worktogether.global.Utilities;

/**
 *
 * @author
 */
public class LongClickDetector {

    private int downX, downY;          // Valores auxiliares que respaldan las coordenadas en ACTION_DOWN

    private final Handler       handler;
    private final LongClick     LongCD;
    private boolean             longClickDetected;
    private final int           MOVEMENT_GAP = 15; // px
    private final Context       ctx;
    private final int           LONG_CLICK_PROC_TIMER;

    public LongClickDetector( Context ctx ){
        this.ctx                = ctx;
        handler                 = new Handler();
        LongCD                  = new LongClick();
        LONG_CLICK_PROC_TIMER   = 1000;
    }

    public LongClickDetector( Context ctx, int LONG_CLICK_PROC_TIMER ){
        this.ctx                    = ctx;
        handler                     = new Handler();
        LongCD                      = new LongClick();
        this.LONG_CLICK_PROC_TIMER  = LONG_CLICK_PROC_TIMER;
    }

    public boolean verifyLongClick(MotionEvent event){
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                // Guardar coordenadas cuando comienza el clic para determinar si se 
                // comenzó con un clic largo.
                downX = (int) event.getX();
                downY = (int) event.getY();

                longClickDetected = false;
                handler.removeCallbacks(LongCD);
                handler.postDelayed(LongCD, LONG_CLICK_PROC_TIMER);
                break;

            case MotionEvent.ACTION_MOVE:
                // Si se superó el límite de movimiento para considerarlo un arrastre cancelar el clic largo
                if (Math.abs(event.getX() - downX) > MOVEMENT_GAP || Math.abs(event.getY() - downY) > MOVEMENT_GAP)
                    handler.removeCallbacks(LongCD);
                break;

            case MotionEvent.ACTION_UP:
                handler.removeCallbacks(LongCD);
                break;
        }
        return longClickDetected;
    }

    //<editor-fold defaultstate="collapsed" desc="Detección de clics largos">
    private class LongClick implements Runnable {

        public void run() {
            longClickDetected = true;

            try{
                MediaPlayer mp = MediaPlayer.create(ctx, R.raw.start);
                mp.setVolume(Utilities.volume, Utilities.volume);
                mp.start();
            }catch(Exception e){
                Toast.makeText(ctx, e.toString(), Toast.LENGTH_LONG).show();
            }
            try{
                long patern[] = {0, 100, 50, 100};
                Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(patern, -1);
                //v.vibrate(200);
            }catch( Exception e ) {
                Log.d(Utilities.TAG, e.toString());
            }
        }
    }
    //</editor-fold>
}