package com.fiftyfive.cargo.models;

/**
 * Created by louis on 04/11/15.
 */
public class Tracker extends CargoModel  {

    private boolean enableDebug;
    private boolean enableOptOut;
    private boolean disableTracking;
    private int trackerDispatchPeriod;

    public boolean isEnableDebug() {
        return enableDebug;
    }

    public void setEnableDebug(boolean enableDebug) {
        this.enableDebug = enableDebug;
    }

    public boolean isEnableOptOut() {
        return enableOptOut;
    }

    public void setEnableOptOut(boolean enableOptOut) {
        this.enableOptOut = enableOptOut;
    }

    public boolean isDisableTracking() {
        return disableTracking;
    }

    public void setDisableTracking(boolean disableTracking) {
        this.disableTracking = disableTracking;
    }

    public int getTrackerDispatchPeriod() {
        return trackerDispatchPeriod;
    }

    public void setTrackerDispatchPeriod(int trackerDispatchPeriod) {
        this.trackerDispatchPeriod = trackerDispatchPeriod;
    }
}
