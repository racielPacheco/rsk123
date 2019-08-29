/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cinvestav.worktogether.services.updatedistribution;

import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author
 */
public interface CustomListener {
    UUID retrieveSessionID();
    void setSessionID(UUID id);

    int[] retrieveBackGroundColor();
    void setBackGroundColor(int[] backGroundColor);
    void setColorConnected(int[] dir,int ownDiriD);
    void setRestoreObjects(String restoreObjects, String ip);

    public void DiscoveryServiceDeviceLeft( HashMap<String, Object> deviceThatLeft,
                                            boolean deviceWasOnDiscoveredDevicesList, boolean deviceWasOnGroupList);
    void setColorDisonnected(final String direction);
}
