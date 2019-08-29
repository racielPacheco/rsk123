package com.cinvestav.worktogether.setup;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.cinvestav.worktogether.gestures.swipe.SwipeGesture;
import com.cinvestav.worktogether.gestures.accelerometer.AccelerometerGesture;
import com.cinvestav.worktogether.global.DeviceFeatures;
import com.cinvestav.worktogether.global.SharedResources;
import com.cinvestav.worktogether.global.Utilities;
import com.cinvestav.worktogether.services.couplingcontrol.CouplingSettings;
import com.cinvestav.worktogether.services.couplingcontrol.LongClickDetector;
import com.cinvestav.worktogether.services.couplingcontrol.SendFeaturesToTheGroup;
import com.cinvestav.worktogether.services.couplingcontrol.WorkspaceTouchEventListener;
import com.cinvestav.worktogether.services.devicediscovery.DiscoveryCollectService;
import com.cinvestav.worktogether.services.devicediscovery.DiscoveryPublishService;
import com.cinvestav.worktogether.services.devicediscovery.DiscoveryServiceListener;
import com.cinvestav.worktogether.services.devicediscovery.DiscoverySettings;
import com.cinvestav.worktogether.services.state.StateCheckService;
import com.cinvestav.worktogether.services.state.StateCollectService;
import com.cinvestav.worktogether.services.state.StatePublishService;
import com.cinvestav.worktogether.services.state.StateSettings;
import com.cinvestav.worktogether.services.updatedistribution.CustomListener;
import com.cinvestav.worktogether.services.updatedistribution.UpdateDistributionService;
import com.cinvestav.worktogether.services.updatedistribution.UpdateDistributionServiceListener;
import com.cinvestav.worktogether.services.updatedistribution.UpdateSettings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Class WorkTogetherSetup
 */
public class WorkTogetherSetup {

    private DiscoverySettings           discovery_settings;
    private CouplingSettings            coupling_settings;
    private UpdateSettings              updating_settings;
    private StateSettings               state_settings;
    private DeviceFeatures              features;
    private DiscoveryCollectService     CollectingService;
    private DiscoveryPublishService     PublishingService;
    private StatePublishService         PublishingServiceState;
    private StateCollectService         CollectServiceState;
    private StateCheckService           CheckServiceState;
    //private DiscoveryShutdownService    ShutdownService;
    public UpdateDistributionService    UpdatingService;

    private Configuration               configuration;


    public static Boolean is_TabletorPhone;
    private AccelerometerGesture AAccelerometerGesture;
    private View                        WorkspaceView;
    Activity                            Act;

    public WorkTogetherSetup( Activity mainActivity, View workspaceView,
                                DiscoveryServiceListener DiscoverListener,
                                final WorkspaceTouchEventListener TapEventListener,
                                UpdateDistributionServiceListener Updatelistener,
                                final CustomListener customListener) {
        try {

            Act             = mainActivity;
            WorkspaceView   = workspaceView;
            is_TabletorPhone = isTabletorPhone();
            //never turn off screen or screen lock
            this.Act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            /*
                1.- Inicializar los recursos compartidos entre cada una de las clases, las configuraciones de cada
                    uno de los servicios y extraer las características del dispositivo.
                        (No importa el orden)
            */
            SharedResources.init_SharedResources();
            features = new DeviceFeatures( Act );
            /*
                2.- Inicializar las configuraciones para cada uno de los servicios.
                    No importa el orden.
            */
            discovery_settings  = new DiscoverySettings( );
            coupling_settings   = new CouplingSettings( );
            updating_settings   = new UpdateSettings( );
            state_settings      = new StateSettings( );
            /*
                3.- Arrancar el servicio de distribución de actualizaciones, el cual se encarga de actualizar la lista
                    de grupo después de un acoplamiento exitoso, así como de distribuir y recibir objetos de otros
                    dispositivos que son parte de la lógica de la aplicación final.
            */
            //init configuration
            configuration       = new Configuration(updating_settings,discovery_settings,state_settings);
            /* Código adicional */
            UpdatingService = new UpdateDistributionService( Updatelistener, updating_settings, customListener);

            UpdatingService.startService();
            /*
                4.0- Crear el listener para cuando un dispositivo se une o abandona la aplicación.
            */
            DiscoveryServiceListener discoveryServiceListener = DiscoverListener;
            /*
                4.1- Arrancar los servicios de descubrimiento.
                     PublishingService realiza el broadcast de los mensajes.
                     CollectingService atrapa esos mensajes (incuídos los mensajes propios, pero se pueden filtrar
                     por IP).
                     ShutdownService comprueba la lista de presencia cada ciertos segundos.
            */
            PublishingService   = new DiscoveryPublishService( discovery_settings, updating_settings );
            CollectingService   = new DiscoveryCollectService( discovery_settings, discoveryServiceListener );
            PublishingServiceState = new StatePublishService( state_settings);
            CollectServiceState = new StateCollectService(customListener, state_settings);
            CheckServiceState   = new StateCheckService(customListener, state_settings);
            //ShutdownService     = new DiscoveryShutdownService( discovery_settings );

            //el .execute() solo permite un thread por app
            //el .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR) permite mas de un hilo a la vez
            //PublishingService.execute();
            PublishingService.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //CollectingService.ejecutar();
            CollectingService.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //ShutdownService.execute();
            PublishingServiceState.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            CollectServiceState.startStateCollectService();
            CheckServiceState.startCheckService();
            //CollectServiceState.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //CheckServiceState.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            /*
                5.1.- Inicializar el detector de clics largos.
            */
            final LongClickDetector LongClickChecker = new LongClickDetector( Act, 1000 );
            /*
                5.2.- Utilizar uno o varios métodos de acoplamiento.
            */

            final SwipeGesture SSwipeGesture = new SwipeGesture( Act );

            AAccelerometerGesture = new AccelerometerGesture( Act );
            /*
                5.3.- Incrustarlo en el view que reconocerá los gestos de acoplamiento.
            */
            WorkspaceView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    boolean longClickDetected = LongClickChecker.verifyLongClick( motionEvent );
                    /*
                        5.4.- Si alguno de los gestos es válido, enviar un intento de acoplamiento.
                    */
                    boolean validSwipeGesture = false;

                    //aqui solo se debe ejecutar uno solo este es el de long touch
                    if( configuration.getSwipe() ) {
                        if( SSwipeGesture.validateGesture( coupling_settings, updating_settings, motionEvent, longClickDetected, features, customListener ) )
                            validSwipeGesture = true;
                    }
                    /*
                        Se ejecutará solamente cuando se levante el dedo y si el pinch gesture no se llevó a cabo o es falso y
                        si ya se detectó el click largo.
                    */
                    if( configuration.getAccelerometer() && !validSwipeGesture ) {
                        AAccelerometerGesture.validateGesture(coupling_settings, updating_settings, motionEvent, longClickDetected, features, customListener);
                    }
                    /*
                        Procesar el gesto si es que el usuario definió gestos propios o algún comportamiento específico
                        y si no se detectó un click largo.
                    */
                    if( !longClickDetected )
                        TapEventListener.workspaceTapEventHappened( view, motionEvent );
                    // Debe ser true para que se reconozca no solamente el ACTION_DOWN, sino el MOVE y UP también
                    return true;
                }
            });
        } catch (Exception e) {
            Log.d(Utilities.TAG, e.toString());
        }
    }

    private boolean isTabletorPhone() {
        Display display = ((Activity)this.Act).getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels / displayMetrics.densityDpi;
        int height = displayMetrics.heightPixels / displayMetrics.densityDpi;

        double screenDiagonal = Math.sqrt(width * width + height * height);
        return (screenDiagonal >= 6.5);
    }

    public void onConfigurationChanged(){
        //<editor-fold defaultstate="collapsed" desc="Enviar las nuevas especificaciones al rotar la pantalla">
        features.setAllFeatures( Act );
        new Thread(new SendFeaturesToTheGroup( Act, features )).start();
        //</editor-fold>
    }

    public void onPause(){
        //<editor-fold defaultstate="collapsed" desc="Pausar el acelerómetro si estaba midiendo">
        try{
            if( AAccelerometerGesture != null )
                AAccelerometerGesture.onPause();
        }catch(Exception e){
            Log.d(Utilities.TAG, "WorkTogetherSetup::onPause(): " + e.toString());
        }
        //</editor-fold>
    }

    public void onStop(){
        //<editor-fold defaultstate="collapsed" desc="Detener servicios, el servidor y borrar variables">
        try {
            CollectingService.stopService();
            PublishingService.stopService();
            //ShutdownService.stopService();
            UpdatingService.stopService();
            PublishingServiceState.stopService();
            CollectServiceState.stopService();
            CheckServiceState.stopService();

            //disable screen lock
            this.Act.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            discovery_settings  = null;
            coupling_settings   = null;

            SharedResources.freeResources();
        } catch( Exception e ){
            Log.d( Utilities.TAG, "WorkTogetherSetup::onStop(): " + e.toString() );
        }
        //</editor-fold>
    }

    public List<HashMap<String, Object>> getConected(){
        return configuration.getConected();
    }
    public HashMap<String, String> getOwnInfo(){
        return configuration.getOwnInfo();
    }
    public HashMap<String, String> getInfoConfig(){
        return configuration.getInfoConfig();
    }
    public void setupCoupling(Boolean setAccelerometer, Boolean setSwipe){
        configuration.setupCoupling(setAccelerometer,setSwipe);
    }
    public String[] getCoupling (){
        return configuration.getCoupling();
    }
    public void setStateObjects(String stateObjects){
            SharedResources.STATE_OBJECTS = stateObjects;
    }
}
