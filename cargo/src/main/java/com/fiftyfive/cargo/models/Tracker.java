package com.fiftyfive.cargo.models;

/**
 * Created by louis on 04/11/15.
 */
public class Tracker extends CargoModel  {

    private boolean enableDebug;
    private boolean optOut;
    private boolean dryRun;
    private int trackerDispatchPeriod;

    public boolean isEnableDebug() {
        return enableDebug;
    }

    public void setEnableDebug(boolean enableDebug) {
        this.enableDebug = enableDebug;
    }

    public boolean isOptOut() {
        return optOut;
    }

    public void setOptOut(boolean optOut) {
        this.optOut = optOut;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public int getTrackerDispatchPeriod() {
        return trackerDispatchPeriod;
    }

    public void setTrackerDispatchPeriod(int trackerDispatchPeriod) {
        this.trackerDispatchPeriod = trackerDispatchPeriod;
    }
}
