package com.cinvestav.worktogether.services.updatedistribution.networkclasses;

/**
 *
 * @author
 */
public class Net_Message {
    
    public Integer  color[];
    public int      counter;
    
    public Net_Message(){
        color   = new Integer[3];
        counter = 0;
    }
    
    public Net_Message(Integer rec[], int counter){
        color           = rec;
        this.counter    = counter;
    }
}
