package com.fiftyfive.cargo.models;

import java.util.List;

/**
 * Created by louis on 04/11/15.
 */
public class Transaction extends CargoModel {


    //transaction
    private String transactionId;
    private String transactionTotal;

    private List<TransactionProduct> transactionProducts;


    //user
    private String userGoogleId;

    //screen
    private String screenName;

    //event
    private String eventName;

    //trackers properties
    private String enableDebug;


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionTotal() {
        return transactionTotal;
    }

    public void setTransactionTotal(String transactionTotal) {
        this.transactionTotal = transactionTotal;
    }

    public List<TransactionProduct> getTransactionProducts() {
        return transactionProducts;
    }

    public void setTransactionProducts(List<TransactionProduct> transactionProducts) {
        this.transactionProducts = transactionProducts;
    }

    public String getUserGoogleId() {
        return userGoogleId;
    }

    public void setUserGoogleId(String userGoogleId) {
        this.userGoogleId = userGoogleId;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEnableDebug() {
        return enableDebug;
    }

    public void setEnableDebug(String enableDebug) {
        this.enableDebug = enableDebug;
    }


}
