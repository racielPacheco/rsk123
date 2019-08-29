package com.cinvestav.worktogether.services.couplingcontrol;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
//import com.cinvestav.worktogether.collab.R;
import com.cinvestav.worktogether.R;
import com.cinvestav.worktogether.global.DeviceFeatures;
import com.cinvestav.worktogether.global.SharedResources;
import com.cinvestav.worktogether.global.Utilities;
import com.cinvestav.worktogether.services.updatedistribution.UpdateSettings;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import org.json.JSONObject;

/*
 *
 * @author
 */
public class SendOnePairingRequest extends AsyncTask<Object, Void, Boolean> {

    private final CouplingSettings  coupling_settings;
    private final UpdateSettings    updating_settings;
    private final String            ownDirection;
    private final Context           ctx;
    private final DeviceFeatures    features;

    public SendOnePairingRequest(Context ctx, CouplingSettings param, UpdateSettings updating_settings, String ownDir, DeviceFeatures features) {
        this.ctx                = ctx;
        this.features           = features;
        coupling_settings       = param;
        this.updating_settings  = updating_settings;
        ownDirection            = ownDir;
    }

    @Override
    @SuppressWarnings({"UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch"})
    protected Boolean doInBackground(Object... args) {

        try {
            DatagramSocket socket;
            socket = new DatagramSocket( );
            socket.setBroadcast( true );
            JSONObject obj = new JSONObject( );
            obj.put("IP", Utilities.getIPAddress( true ));
            obj.put("TCP", updating_settings.UPDATE_DISTRIBUTION_TCP_PORT );
            obj.put("UDP", updating_settings.UPDATE_DISTRIBUTION_UDP_PORT );
            obj.put("DIR", ownDirection);
            obj.put("SPECS", features.deviceFeatures);
            //obj.put("DATE", SharedResources.SYSTEM_DATE);
            String data = obj.toString( );
            //Log.d( Utilities.TAG, "onStop()444444: " + SharedResources.SYSTEM_DATE );
            /*
                Enviar solicitud de acoplamiento.
             */
            DatagramPacket packet = new DatagramPacket( data.getBytes(), data.length(), Utilities.getBroadcastAddress(),
                    coupling_settings.COUPLING_SERVICE_PORT );
            socket.send(packet);
            socket.close( );

            try{
                MediaPlayer mp = MediaPlayer.create(ctx, R.raw.start);
                mp.setVolume(Utilities.volume, Utilities.volume);
                mp.start();
            }catch(Exception e){
                Toast.makeText(ctx, e.toString(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Log.d( Utilities.TAG, "Pairing Request Ex: " + ex.toString() );
        }
        Log.d( Utilities.TAG, ownDirection + " pairing request done..." );
        return true;
    }
}