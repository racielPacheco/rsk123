package com.cinvestav.worktogether.services.devicediscovery;

/**
 *
 * @author
 */
public class DiscoverySettings {
    public int DISCOVERY_SERVICES_PORT;
    public int PUBLISHING_DELAY;
    public int MESSAGE_SIZE;
    public long LEAVING_DELAY;
    public int LEAVING_ALERT_MESSAGES;
    public int SHUTDOWNSERVICE_CHECK_DELAY;

    public DiscoverySettings( int collecting_port, int publishing_delay, int message_size, long leaving_delay,
                              int leaving_alert_messages, int shutdownservice_delay ){

        DISCOVERY_SERVICES_PORT = collecting_port;  // Debe ser el mismo en todos los dispositivos.
        PUBLISHING_DELAY        = publishing_delay; // Tiempo que debe transcurrir antes de volver a difundir la información del dispositivo.
        MESSAGE_SIZE            = message_size;     // Tamaño en bytes del mensaje tipo broadcast a difundir.
        LEAVING_DELAY           = leaving_delay;    // Tiempo que debe transcurrir antes de volver a enviar el mensaje de que
        // se está abandonando la aplicación. Dicho mensaje se difunde LEAVING_ALERT_MESSAGES veces.
        LEAVING_ALERT_MESSAGES  = leaving_alert_messages; // Número de mensajes tipo broadcast que se enviarán para avisar
        // que se está abandonando la aplicación.
        SHUTDOWNSERVICE_CHECK_DELAY   = shutdownservice_delay;  // Milisegundos que tarda el hilo entre una checada y otra.
    }

    // Valores por omisión
    public DiscoverySettings(){
        DISCOVERY_SERVICES_PORT     = 11001;
        PUBLISHING_DELAY            = 1000;
        MESSAGE_SIZE                = 2000;
        LEAVING_DELAY               = 300;
        LEAVING_ALERT_MESSAGES      = 5;
        SHUTDOWNSERVICE_CHECK_DELAY = 8000;
    }

    public String getDelay(){
        return String.valueOf(PUBLISHING_DELAY/1000);
    }
}