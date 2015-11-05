package com.fiftyfive.cargo.models;

/**
 * Created by louis on 04/11/15.
 */
public class Screen extends CargoModel{

    private String screenName;


    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(Object screenName) {
        this.screenName = screenName.toString();
    }

}
