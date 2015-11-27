package com.fiftyfive.cargo.models;

/**
 * Created by louis on 04/11/15.
 */
public class Tracker extends CargoModel  {

    private boolean enableDebug;

    public boolean isEnableDebug() {
        return enableDebug;
    }

    public void setEnableDebug(boolean enableDebug) {
        this.enableDebug = enableDebug;
    }
}
