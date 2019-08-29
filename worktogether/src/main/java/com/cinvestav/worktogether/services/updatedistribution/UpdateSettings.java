package com.cinvestav.worktogether.services.updatedistribution;

import com.cinvestav.worktogether.global.Utilities;

/**
 *
 * @author
 */
public class UpdateSettings {

    // Puertos en los cuales UpdateDistributionService recibirá los intentos de 
    // acoplamiento y los mensajes enviados al grupo de trabajo.

    public int UPDATE_DISTRIBUTION_TCP_PORT;
    public int UPDATE_DISTRIBUTION_UDP_PORT;

    public UpdateSettings(){
        int ports[] = Utilities.getTwoFreePorts();
        UPDATE_DISTRIBUTION_TCP_PORT = ports[0];
        UPDATE_DISTRIBUTION_UDP_PORT = ports[1];
    }
    public int getTCP_PORT(){
        return UPDATE_DISTRIBUTION_TCP_PORT;
    }
    public int getUDP_PORT(){
        return UPDATE_DISTRIBUTION_UDP_PORT;
    }
}
