package com.cinvestav.worktogether.services.devicediscovery;

import java.util.HashMap;

/**
 *
 * @author
 */
public interface DiscoveryServiceListener {
    public void DiscoveryServiceDeviceJoined( HashMap<String, Object> deviceThatJoined );
    public void DiscoveryServiceDeviceLeft( HashMap<String, Object> deviceThatLeft, 
            boolean deviceWasOnDiscoveredDevicesList, boolean deviceWasOnGroupList);
    void setColorDisonnected(final String direction);
}
