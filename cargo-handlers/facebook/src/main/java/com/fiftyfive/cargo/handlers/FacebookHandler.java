package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.models.Event;
import com.fiftyfive.cargo.models.Tracker;
import com.fiftyfive.cargo.models.Transaction;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.Set;

import static com.fiftyfive.cargo.ModelsUtils.*;

/**
 * Created by louis on 03/11/15.
 * Copyright 2016 fifty-five All rights reserved.
 *
 * The class which handles interactions with the Facebook SDK
 */
public class FacebookHandler extends AbstractTagHandler {

/* ************************************ Variables declaration *********************************** */

    /** The AppEventsLogger allows to log various types of events back to Facebook. */
    protected AppEventsLogger facebookLogger;

    /** Constants used to define callbacks in the register and in the execute method */
    private final String FB_INIT = "FB_init";
    private final String FB_TAG_EVENT = "FB_tagEvent";
    private final String FB_PURCHASE = "FB_tagPurchase";

    private final String VALUE_TO_SUM = "valueToSum";


/* ************************************ Handler core methods ************************************ */

    /**
     * Called by the TagHandlerManager, initialize the core of the handler
     */
    @Override
    public void initialize() {
        super.initialize("FB", "Facebook", false);

        validate(true);
    }

    /**
     * A callback method for the registered callbacks method name mentioned in the register method.
     *
     * @param s     The method name called through the container (defined in the GTM interface)
     * @param map   A map key-object used as a way to give parameters to the class method aimed here
     */
    public void execute(String s, Map<String, Object> map) {
        logReceivedFunction(s, map);

        // a check fo the init method
        if (FB_INIT.equals(s))
            init(map);
        // if the SDK is properly initialized, check for which method is called
        else if (initialized) {
            switch (s) {
                case FB_TAG_EVENT:
                    tagEvent(map);
                    break;
                case FB_PURCHASE:
                    purchase(map);
                    break;
                default:
                    logUnknownFunction(s);
            }
        }
        else
            logUninitializedFramework();
    }



/* ************************************* SDK initialization ************************************* */

    /**
     * The method you need to call first. Register your facebook app id to the facebook SDK
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method
     *              * applicationId (String) : the app id facebook gives when you register your app
     *              * enableDebug (bool) : the value of the bool to turn on/off the facebook debug
     */
    private void init(Map<String, Object> map) {

        Double appIdDouble = getDouble(map, Tracker.APPLICATION_ID, 0);
        Long appIdLong = appIdDouble.longValue();
        String applicationId = Long.toString(appIdLong);

        if(appIdLong != 0 && applicationId != null) {
            // Since the applicationId isn't declared in the AndroidManifest, it is a necessity to
            // set it before initializing the FacebookSDK, or it will throw an error.
            FacebookSdk.setApplicationId(applicationId);
            FacebookSdk.sdkInitialize(cargo.getAppContext());
            // Initialization of the logger which will send the events to the Fb Analytics interface
            facebookLogger = AppEventsLogger.newLogger(cargo.getAppContext());
            AppEventsLogger.activateApp(cargo.getAppContext());
            logParamSetWithSuccess(Tracker.APPLICATION_ID, applicationId);
            setInitialized(FacebookSdk.isInitialized());
        }
        else {
            logMissingParam(new String[]{Tracker.APPLICATION_ID}, FB_INIT);
        }
        FacebookSdk.setIsDebugEnabled(getBoolean(map, Tracker.ENABLE_DEBUG, false));
        Log.d(this.key+"_handler", "debug enabled : " + Boolean.toString(FacebookSdk.isDebugEnabled()));
    }



/* ****************************************** Tracking ****************************************** */

    /**
     * The method used to send an event to your facebook app
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method.
     *              * eventName (String) :  the only parameter requested here
     *              * valueToSum (Double) : When reported, all of the valueToSum properties will
     *                                      be summed together. It is an arbitrary number that can
     *                                      represent any value (e.g., a price or a quantity).
     *              * parameters : any other key in the map will be taken as a parameter linked
     *                             to the event. You can set up to 25 parameters for a given event.
     *
     */
    private void tagEvent(Map<String, Object> map){

        String eventName = getString(map, Event.EVENT_NAME);
        double valueToSum = getDouble(map, VALUE_TO_SUM, -1);
        Bundle parameters;

        if (eventName != null) {
            map.remove(Event.EVENT_NAME);

            // attach a valueToSum to the event if it exists
            if (valueToSum >= 0) {
                map.remove(VALUE_TO_SUM);

                // check for parameters and set them to the event if they exist.
                if (map.size() > 1) {
                    parameters = eventParamBuilder(map);
                    // fire the tag with the given parameters & valueToSum
                    facebookLogger.logEvent(eventName, valueToSum, parameters);
                    logParamSetWithSuccess(Event.EVENT_NAME, eventName);
                    logParamSetWithSuccess(VALUE_TO_SUM, valueToSum);
                    logParamSetWithSuccess("parameters", parameters);
                }
                else {
                    // fire the tag with the given valueToSum
                    facebookLogger.logEvent(eventName, valueToSum);
                    logParamSetWithSuccess(Event.EVENT_NAME, eventName);
                    logParamSetWithSuccess(VALUE_TO_SUM, valueToSum);
                }
            }
            // attach parameters to the event if they exist
            else if (map.size() > 1) {
                parameters = eventParamBuilder(map);
                // fire the tag with the given parameters
                facebookLogger.logEvent(eventName, parameters);
                logParamSetWithSuccess(Event.EVENT_NAME, eventName);
                logParamSetWithSuccess("parameters", parameters);
            }
            else {
                // fire the tag
                facebookLogger.logEvent(eventName);
                logParamSetWithSuccess(Event.EVENT_NAME, eventName);
            }
        }
        else {
            logMissingParam(new String[]{Event.EVENT_NAME}, FB_TAG_EVENT);
        }
    }

    /**
     * The method used to report a purchase event to your facebook app
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method.
     *              * transactionTotal (Double) : represents the total amount of the purchase
     *              * transactionCurrencyCode (String) : the code of the currency the purchase
     *                                                   was made with
     *
     */
    private void purchase (Map<String, Object> map) {

        double total = getDouble(map, Transaction.TRANSACTION_TOTAL, -1);
        String currency = getString(map, Transaction.TRANSACTION_CURRENCY_CODE);

        if (total >= 0 && currency != null){
            facebookLogger.logPurchase(BigDecimal.valueOf(total), Currency.getInstance(currency));
            logParamSetWithSuccess(Transaction.TRANSACTION_TOTAL, total);
            logParamSetWithSuccess(Transaction.TRANSACTION_CURRENCY_CODE, currency);
        }
        else {
            logMissingParam(new String[]{
                    Transaction.TRANSACTION_TOTAL,
                    Transaction.TRANSACTION_CURRENCY_CODE
            }, FB_PURCHASE);
        }
    }



/* ****************************************** Utility ******************************************* */

    /**
     * A simple method to put the parameters given in a bundle and to return it
     * This method is just called from tagEvent method
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method.
     * @return      the bundle containing all the parameters initially given for an event
     *
     */
    private Bundle eventParamBuilder(Map<String, Object> map) {

        Bundle bundle = new Bundle();

        Set<String> keys = map.keySet();
        for (String key : keys) {
            if (map.get(key) instanceof String)
                bundle.putString(key, getString(map, key));
            else if (map.get(key) instanceof Boolean) {
                if (getBoolean(map, key, false))
                    bundle.putInt(key, 1);
                else
                    bundle.putInt(key, 0);
            }
            else if (map.get(key) instanceof Integer)
                bundle.putInt(key, getInt(map, key, 0));
            else
                logUncastableParam(key, "String/Boolean/Int");
        }
        return bundle;
    }

    /**
     * A callback triggered when an activity starts
     *
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityStarted(Activity activity) {

    }

    /**
     * A callback triggered when an activity is resumed
     * Logs the resume on facebook SDK to measure sessions
     *
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityResumed(Activity activity) {
        if (isInitialized())
            AppEventsLogger.activateApp(activity);
    }

    /**
     * A callback triggered when an activity is paused
     * Logs the pause on facebook SDK to measure sessions
     *
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityPaused(Activity activity) {
        if (isInitialized())
            AppEventsLogger.deactivateApp(activity);
    }

    /**
     * A callback triggered when an activity stops
     *
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityStopped(Activity activity) {

    }


/* ********************************************************************************************** */

}
