package com.cinvestav.worktogether.services.state;

/**
 * Created by
 */
public class StateSettings {
    public int STATE_SERVICES_PORT;
    public int STATE_SERVICES_PORT_RECEIVE;
    public int PUBLISHING_DELAY;
    public int PUBLISHING_DELAY_PER_DEVICE;
    public int MESSAGE_SIZE;
    //public int SHUTDOWNSERVICE_CHECK_DELAY;

    public StateSettings( int collecting_port, int receive_port, int publishing_delay, int publishing_delay_device, int message_size, long leaving_delay,
                              int leaving_alert_messages, int shutdownservice_delay ){

        STATE_SERVICES_PORT         = collecting_port;  // Debe ser el mismo en todos los dispositivos.
        STATE_SERVICES_PORT_RECEIVE = receive_port;
        PUBLISHING_DELAY            = publishing_delay; // Tiempo que debe transcurrir antes de volver a difundir la información del dispositivo.
        PUBLISHING_DELAY_PER_DEVICE = publishing_delay_device; //tiempo de difusion por dispositivos
        MESSAGE_SIZE                = message_size;     // Tamaño en bytes del mensaje tipo broadcast a difundir.
        // que se está abandonando la aplicación.
       // SHUTDOWNSERVICE_CHECK_DELAY   = shutdownservice_delay;  // Milisegundos que tarda el hilo entre una checada y otra.
    }

    // Valores por omisión
    public StateSettings(){
        STATE_SERVICES_PORT         = 11002;
        STATE_SERVICES_PORT_RECEIVE  = 11009;// no ocupado
        PUBLISHING_DELAY            = 30000;// 30 seg
        PUBLISHING_DELAY_PER_DEVICE = 1000;// 1seg
        MESSAGE_SIZE                = 500000;
        //SHUTDOWNSERVICE_CHECK_DELAY = 8000;
    }

    public String getDelay(){
        return String.valueOf(PUBLISHING_DELAY/1000);
    }
}