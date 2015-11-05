package com.fiftyfive.cargo.models;

/**
 * Created by louis on 04/11/15.
 */
public class User extends CargoModel {


    private String userId;
    private String userGoogleId;
    private String userFacebookId;

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserGoogleId() {
        return userGoogleId;
    }

    public void setUserGoogleId(Object userGoogleId) {
        this.userGoogleId = userGoogleId.toString();
    }

    public String getUserFacebookId() {
        return userFacebookId;
    }

    public void setUserFacebookId(String userFacebookId) {
        this.userFacebookId = userFacebookId;
    }
}
