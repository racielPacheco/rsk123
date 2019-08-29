package com.cinvestav.worktogether.services.couplingcontrol;

/**
 *
 * @author
 */
public class CouplingSettings {
    /*
        Sección general del acoplamiento
    */
    public int COUPLING_SERVICE_PORT;
    public int COUPLING_WAITING_TIME; // Tiempo aproximado que se tiene en milisegundos para
    // recibir un intento de acoplamiento de otro dispositivo.
    public int COUPLING_MESSAGE_SIZE; // Tamaño en bytes (aproximado) del mensaje enviado en el intento de acoplamiento.
    // Recomendación: Debe estar en potencias de 2.

    public CouplingSettings( int pairing_port, int coupling_waiting_time, int servertcpport, int serverudpport,
                             int coupling_message_size){

        COUPLING_SERVICE_PORT   = pairing_port;
        COUPLING_WAITING_TIME   = coupling_waiting_time;
        COUPLING_MESSAGE_SIZE   = coupling_message_size;
    }

    public CouplingSettings(){
        COUPLING_SERVICE_PORT   = 11003;
        COUPLING_WAITING_TIME   = 3500;
        COUPLING_MESSAGE_SIZE   = 12288; // 12 kbs
    }
}
