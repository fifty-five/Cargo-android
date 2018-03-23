package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.fiftyfive.cargo.AbstractTagHandler;
import com.adobe.mobile.*;
import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.CargoLocation;
import com.fiftyfive.cargo.models.Event;
import com.fiftyfive.cargo.models.Screen;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.fiftyfive.cargo.ModelsUtils.getBoolean;
import static com.fiftyfive.cargo.ModelsUtils.getDouble;
import static com.fiftyfive.cargo.ModelsUtils.getString;

/**
 * Created by Julien Gil on 29/01/2018.
 * Copyright 2018 fifty-five All rights reserved.
 *
 * The class which handles interactions with the Adobe Analytics SDK
 */

public class AdobeHandler extends AbstractTagHandler {

/* ************************************ Variables declaration *********************************** */

    /** Constants used to define callbacks in the register and in the execute method */
    private final String ADB_INIT = "ADB_init";
    private final String ADB_TAG_SCREEN = "ADB_tagScreen";
    private final String ADB_TAG_EVENT = "ADB_tagEvent";
    private final String ADB_TRACK_LOCATION = "ADB_trackLocation";
    private final String ADB_SET_PRIVACY = "ADB_setPrivacyStatus";
    private final String ADB_TRACK_TIME_START = "ADB_trackTimeStart";
    private final String ADB_TRACK_TIME_END = "ADB_trackTimeEnd";
    private final String ADB_TRACK_TIME_UPDATE = "ADB_trackTimeUpdate";
    private final String ADB_INCREASE_LIFETIME_VALUE = "ADB_increaseLifetimeValue";
    private final String ADB_SEND_QUEUE_HITS = "ADB_sendQueueHits";
    private final String ADB_CLEAR_QUEUE = "ADB_clearQueue";

    /** Constants used as parameters for the AT Internet SDK */
    private static final String PRIVACY_STATUS = "privacyStatus";
    private static final String ACTION_NAME = "actionName";
    private static final String ADDITIONAL_LIFETIME_VALUE = "additionalLifetimeValue";

    private Activity latestActivity;
    private boolean needOverrideConfigPath = false;


/* ************************************ Handler core methods ************************************ */

    /**
     * Called by the TagHandlerManager, initialize the core of the handler
     */
    @Override
    public void initialize() {
        super.initialize("ADB", "Adobe Analytics");
        Config.setContext(Cargo.getInstance().getAppContext());
        validate(true);
    }

    /**
     * A callback method for the registered callbacks method name mentioned in the register method.
     *
     * @param s     The method name called through the container (defined in the GTM interface)
     * @param map   A map key-object used as a way to give parameters to the class method aimed here
     */
    @Override
    public void execute(String s, Map<String, Object> map) {
        logReceivedFunction(s, map);

        if (ADB_INIT.equals(s)) {
            init(map);
        }
        else if (isInitialized()) {
            switch (s) {
                case ADB_TAG_EVENT:
                    tagEvent(map);
                    break;
                case ADB_TAG_SCREEN:
                    tagScreen(map);
                    break;
                case ADB_SET_PRIVACY:
                    setPrivacy(map);
                    break;
                case ADB_TRACK_LOCATION:
                    trackLocation(map);
                    break;
                case ADB_TRACK_TIME_START:
                    trackTimeStart(map);
                    break;
                case ADB_TRACK_TIME_END:
                    trackTimeEnd(map);
                    break;
                case ADB_TRACK_TIME_UPDATE:
                    trackTimeUpdate(map);
                    break;
                case ADB_INCREASE_LIFETIME_VALUE:
                    increaseVisitorLifetimeValue(map);
                    break;
                case ADB_SEND_QUEUE_HITS:
                    sendQueueHits();
                    break;
                case ADB_CLEAR_QUEUE:
                    clearQueue();
                    break;
                default:
                    logUnknownFunction(s);
            }
        }
        else {
            logUninitializedFramework();
        }
    }

/* ************************************* SDK initialization ************************************* */

private void init(Map<String, Object> params){
    String overrideConfigPath = "overrideConfigPath";
    Boolean debug = getBoolean(params, "enableDebug", false);
    String configPath = getString(params, overrideConfigPath);

    this.initialized = true;
    Config.setDebugLogging(debug);
    if (configPath != null) {
        if (this.needOverrideConfigPath) {
            try {
                InputStream configInput = Cargo.getInstance().getAppContext().getAssets().open(configPath + ".json");
                Config.overrideConfigStream(configInput);
                this.needOverrideConfigPath = false;
                this.onActivityResumed(latestActivity);
                logParamSetWithSuccess(overrideConfigPath, configPath+".json");
                setInitialized(true);
            } catch (IOException ex) {
                Log.e(this.key, "overrideConfigPath failed: " + ex);
                setInitialized(false);
            }
        }
        else {
            setInitialized(true);
            Log.w(this.key,
                    "overrideConfigPath failed since the default config file has been used already (ADBMobileConfig.json). " +
                            "In order to use another config file, delete the default one in the assets folder.");
        }
    }
    else if (this.needOverrideConfigPath) {
        setInitialized(false);
        Log.w(this.key,
                "Unable to find the default config file (ADBMobileConfig.json) and no other file has been provided." +
                        "Either provide ADBMobileConfig.json or setup a replacement file name in the GTM container." +
                        "The config file has to be saved in the assets folder of your app.");
    }
}


/* ****************************************** Tracking ****************************************** */

    /**
     * Method used to create and fire an event to the Adobe Analytics interface
     * The mandatory parameter is EVENT_NAME.
     * Without this parameter, the event won't be built.
     *
     * @param params    the parameters given at the moment of the dataLayer.push(),
     *                  passed through the GTM container and the execute method.
     *                  * eventName (String) : the name for this event.
     *                  * the rest of the map will be used as is (eventName omitted)
     *                    as the event parameters
     */
    private void tagEvent(Map<String, Object> params) {
        String eventName = getString(params, Event.EVENT_NAME);

        if (eventName != null) {
            params.remove(Event.EVENT_NAME);
            HashMap<String, Object> contextData = params.size() > 0 ? new HashMap<>(params) : null;
            Analytics.trackAction(eventName, contextData);
            logParamSetWithSuccess(Event.EVENT_NAME, eventName);
            if (contextData != null) {
                logParamSetWithSuccess("eventParameters", contextData);
            }
        }
        else {
            logMissingParam(new String[]{Event.EVENT_NAME}, ADB_TAG_EVENT);
        }
    }

    /**
     * Method used to create and fire a screen view to Adobe Analytics
     * The mandatory parameter is SCREEN_NAME
     *
     * @param params    the parameters given at the moment of the dataLayer.push(),
     *                  passed through the GTM container and the execute method.
     *                  * screenName (String) : the name of the screen that has been seen
     *                  * the rest of the map will be used as is (screenName omitted)
     *                    as the screen parameters
     */
    private void tagScreen(Map<String, Object> params) {
        String screenName = getString(params, Screen.SCREEN_NAME);

        if (screenName != null) {
            params.remove(Screen.SCREEN_NAME);
            HashMap<String, Object> contextData = params.size() > 0 ? new HashMap<>(params) : null;
            Analytics.trackState(screenName, contextData);
            logParamSetWithSuccess(Screen.SCREEN_NAME, screenName);
            if (contextData != null) {
                logParamSetWithSuccess("screenParameters", contextData);
            }
        }
        else {
            logMissingParam(new String[]{Screen.SCREEN_NAME}, ADB_TAG_SCREEN);
        }
    }

    /**
     * Sends the current latitude, longitude, and location in a defined point of interest.
     *
     * @param params    the parameters given at the moment of the dataLayer.push(),
     *                  passed through the GTM container and the execute method.
     *                  * Map : the additional context to set for this location
     */
    private void trackLocation(Map<String, Object> params) {

        if (CargoLocation.locationIsSet()) {
            Location currentLocation = CargoLocation.getLocation();
            HashMap<String, Object> contextData = params.size() > 0 ? new HashMap<>(params) : null;

            Analytics.trackLocation(currentLocation, contextData);
            logParamSetWithSuccess("location", currentLocation);
            if (contextData != null) {
                logParamSetWithSuccess(ADB_TRACK_LOCATION, contextData);
            }
        }
        else {
            logMissingParam(new String[]{"user location"}, ADB_TRACK_LOCATION);
        }

    }

    /**
     * The timed actions measure the in-app time and total time between the start and the end of
     * an action. You can use timed actions to define segments and compare time to purchase,
     * pass level, checkout flow, and so on.
     * Start the counter for the mentioned actionName.
     *
     * @param params    the parameters given at the moment of the dataLayer.push(),
     *                  passed through the GTM container and the execute method.
     *                  * actionName (String) : the timed-action name
     *                  * the rest of the map will be used as is (actionName omitted)
     *                    as the timed action parameters
     */
    private void trackTimeStart(Map<String, Object> params) {
        String actionName = getString(params, ACTION_NAME);

        if (actionName != null) {
            params.remove(ACTION_NAME);
            HashMap<String, Object> contextData = params.size() > 0 ? new HashMap<>(params) : null;
            Analytics.trackTimedActionStart(actionName, contextData);
            logParamSetWithSuccess(ACTION_NAME, actionName);
            if (contextData != null) {
                logParamSetWithSuccess(ADB_TRACK_TIME_START, contextData);
            }
        }
        else {
            logMissingParam(new String[]{ACTION_NAME}, ADB_TRACK_TIME_START);
        }
    }

    /**
     * The timed actions measure the in-app time and total time between the start and the end of
     * an action. You can use timed actions to define segments and compare time to purchase,
     * pass level, checkout flow, and so on.
     * Stop the counter for the mentioned actionName.
     *
     * @param params    the parameters given at the moment of the dataLayer.push(),
     *                  passed through the GTM container and the execute method.
     *                  * actionName (String) : the timed-action name
     *                  * successfulAction (Boolean) : whether you want to send the hit or not
     *                  * the rest of the map will be used as is (actionName omitted)
     *                    as the timed action parameters
     */
    private void trackTimeEnd(Map<String, Object> params) {
        final String actionName = getString(params, ACTION_NAME);
        final Boolean sendHit = getBoolean(params, "successfulAction", true);

        if (actionName != null) {
            params.remove(ACTION_NAME);
            params.remove("successfulAction");
            final HashMap<String, Object> cData = new HashMap<>(params);

            Analytics.trackTimedActionEnd(actionName, new Analytics.TimedActionBlock<Boolean>() {
                @Override
                public Boolean call(long inAppDuration, long totalDuration, Map<String, Object> contextData) {
                    contextData.putAll(cData);
                    String message = sendHit ? "have been" : "haven't been";
                    Log.v(AdobeHandler.super.key+"_handler", actionName+" trackTimeEnd hit "+message+" sent");
                    logParamSetWithSuccess(ACTION_NAME, actionName);
                    if (cData.size() > 0) {
                        logParamSetWithSuccess(ADB_TRACK_TIME_END, cData);
                    }
                    return sendHit; // return true to send the hit, false to cancel
                }
            });
        }
        else {
            logMissingParam(new String[]{ACTION_NAME}, ADB_TRACK_TIME_END);
        }
    }

    /**
     * Can be called at any point with the timed action name to add additional context data.
     *
     * @param params    the parameters given at the moment of the dataLayer.push(),
     *                  passed through the GTM container and the execute method.
     *                  * actionName (String) : the timed-action name to add context to
     *                  * the rest of the map will be used as is (actionName omitted)
     *                    as the context data to add to the action
     */
    private void trackTimeUpdate(Map<String, Object> params) {
        String actionName = getString(params, ACTION_NAME);

        if (actionName != null) {
            params.remove(ACTION_NAME);
            HashMap<String, Object> contextData = params.size() > 0 ? new HashMap<>(params) : null;
            Analytics.trackTimedActionUpdate(actionName, contextData);
            logParamSetWithSuccess(ACTION_NAME, actionName);
            if (contextData != null) {
                logParamSetWithSuccess(ADB_TRACK_TIME_UPDATE, contextData);
            }
        }
        else {
            logMissingParam(new String[]{ACTION_NAME}, ADB_TRACK_TIME_UPDATE);
        }
    }

    /**
     * The lifetime value allows you to measure and target on a lifetime value for each Android user.
     * The value can be used to store lifetime purchases, ad views, video completes, and so on.
     * Each time you send in a value with this method, it is added to the existing value.
     * Lifetime value is stored on device.
     *
     * @param params    the parameters given at the moment of the dataLayer.push(),
     *                  passed through the GTM container and the execute method.
     *                  * additionalLifetimeValue (Double) : the lifetime value to add to the existing one.
     */
    private void increaseVisitorLifetimeValue(Map<String, Object> params) {
        Double additionalLifetimeValue = getDouble(params, ADDITIONAL_LIFETIME_VALUE, 0);

        if (additionalLifetimeValue > 0) {
            params.remove(ADDITIONAL_LIFETIME_VALUE);
            HashMap<String, Object> cData = params.size() > 0 ? new HashMap<>(params) : null;
            Analytics.trackLifetimeValueIncrease(BigDecimal.valueOf(additionalLifetimeValue), cData);
            logParamSetWithSuccess(ADDITIONAL_LIFETIME_VALUE, additionalLifetimeValue);
            if (cData != null) {
                logParamSetWithSuccess(ADB_INCREASE_LIFETIME_VALUE, cData);
            }
        }
        else {
            logMissingParam(new String[]{ADDITIONAL_LIFETIME_VALUE}, ADB_INCREASE_LIFETIME_VALUE);
        }
    }


/* ****************************************** Utility ******************************************* */

    /**
     * Method used to update the privacy status of the user to one of the following values:
     *  - OPT_IN, where the hits are sent immediately.
     *  - OPT_OUT, where the hits are discarded.
     *  - UNKNOWN, where if your report suite is timestamp enabled,
     *             hits are saved until the privacy status changes to opt-in (hits are sent)
     *             or opt-out (hits are discarded). If your report suite is not timestamp enabled,
     *             hits are discarded until the privacy status changes to opt in.
     *
     * @param params    the parameters given at the moment of the dataLayer.push(),
     *                  passed through the GTM container and the execute method.
     *                  * privacyStatus (String) : set to one of the values previously mentioned.
     */
    private void setPrivacy(Map<String, Object> params){
        String privacyStatus = getString(params, PRIVACY_STATUS);
        boolean set = true;

        if ("OPT_IN".equals(privacyStatus)) {
            Config.setPrivacyStatus(MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_OPT_IN);
        }
        else if ("OPT_OUT".equals(privacyStatus)) {
            Config.setPrivacyStatus(MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_OPT_OUT);
        }
        else if ("UNKNOWN".equals(privacyStatus)) {
            Config.setPrivacyStatus(MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_UNKNOWN);
        }
        else {
            logNotFoundValue(PRIVACY_STATUS, privacyStatus, new String[]{"OPT_IN", "OPT_OUT", "UNKNOWN"});
            set = false;
        }

        if (set) {
            logParamSetWithSuccess(PRIVACY_STATUS, privacyStatus);
        }
        else {
            logMissingParam(new String[]{PRIVACY_STATUS}, ADB_SET_PRIVACY);
        }
    }

    /**
     * Regardless of how many hits are queued,
     * this method forces the library to send all hits in the offline queue.
     */
    private void sendQueueHits() {
        long queueSize = Analytics.getQueueSize();

        Analytics.sendQueuedHits();
        Log.v(this.key+"_handler", "Forced to send "+Long.toString(queueSize)+" hits from queue");
    }

    /**
     * Clears all the hits from the offline queue.
     * Use it with caution. This process cannot be reversed.
     */
    private void clearQueue() {
        long queueSize = Analytics.getQueueSize();

        Analytics.clearQueue();
        Log.v(this.key+"_handler", "Cleared "+Long.toString(queueSize)+" hits from queue");
    }

    /**
     * A callback triggered when an activity starts
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityStarted(Activity activity) {

    }

    /**
     * A callback triggered when an activity is resumed
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityResumed(Activity activity) {
        if (!this.isInitialized()) {
            try {
                Cargo.getInstance().getAppContext().getAssets().open("ADBMobileConfig.json");
            } catch (IOException e) {
                this.needOverrideConfigPath = true;
                this.latestActivity = activity;
                return;
            }
        }
        Config.collectLifecycleData(activity);
    }

    /**
     * A callback triggered when an activity is paused
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityPaused(Activity activity) {
        Config.pauseCollectingLifecycleData();
    }

    /**
     * A callback triggered when an activity stops
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityStopped(Activity activity) {

    }
}
