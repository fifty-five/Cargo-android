package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.util.Log;

import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.models.Screen;
import com.fiftyfive.cargo.models.Tracker;
import com.fiftyfive.cargo.models.User;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.tagmanager.Container;

import java.util.Map;

import static com.fiftyfive.cargo.ModelsUtils.getBoolean;
import static com.fiftyfive.cargo.ModelsUtils.getInt;
import static com.fiftyfive.cargo.ModelsUtils.getLong;
import static com.fiftyfive.cargo.ModelsUtils.getString;

/**
 * Created by dali on 25/11/15.
 *
 * The class which handles interactions with the Google Analytics SDK.
 */
public class GoogleAnalyticsHandler extends AbstractTagHandler {

/* ************************************ Variables declaration *********************************** */

    /** The tracker of the Google Analytics SDK which send the events */
    protected GoogleAnalytics analytics;
    protected com.google.android.gms.analytics.Tracker tracker;

    /** Constants used to define callbacks in the register and in the execute method */
    private final String GA_INIT = "GA_init";
    private final String GA_SET = "GA_set";
    private final String GA_IDENTIFY = "GA_identify";
    private final String GA_TAG_SCREEN = "GA_tagScreen";
    private final String GA_TAG_EVENT = "GA_tagEvent";

    private final String ALLOW_IDFA_COLLECTION = "allowIdfaCollection";
    private final String EVENT_ACTION = "eventAction";
    private final String EVENT_CATEGORY = "eventCategory";
    private final String EVENT_LABEL = "eventLabel";
    private final String EVENT_VALUE = "eventValue";
    private final String NON_INTERACTION = "setNonInteraction";



/* ************************************ Handler core methods ************************************ */

    /**
     * Called by the TagHandlerManager, initialize the core of the handler
     */
    @Override
    public void initialize() {
        super.initialize("GA", "Google Analytics");
        analytics = GoogleAnalytics.getInstance(cargo.getApplication());

        validate(analytics != null);
    }

    /**
     * Register the callbacks to the container. After a dataLayer.push(),
     * these will trigger the execute method of this handler.
     *
     * @param container The instance of the GTM container we register the callbacks to
     */
    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback(GA_INIT, this);
        container.registerFunctionCallTagCallback(GA_SET, this);
        container.registerFunctionCallTagCallback(GA_IDENTIFY, this);
        container.registerFunctionCallTagCallback(GA_TAG_SCREEN, this);
        container.registerFunctionCallTagCallback(GA_TAG_EVENT, this);
    }

    /**
     * A callback method for the registered callbacks method name mentioned in the register method.
     *
     * @param s     The method name called through the container (defined in the GTM interface)
     * @param map   A map key-object used as a way to give parameters to the class method aimed here
     */
    @Override
    public void execute(String s, Map<String, Object> map) {
        if (GA_INIT.equals(s))
            init(map);
        else if (initialized) {
            switch (s) {
                case GA_SET:
                    set(map);
                    break;
                case GA_IDENTIFY:
                    identify(map);
                    break;
                case GA_TAG_SCREEN:
                    tagScreen(map);
                    break;
                case GA_TAG_EVENT:
                    tagEvent(map);
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
     *The method you have to call first, because it initializes
     * the Google Analytics tracker with the parameters you give.
     *
     * @param parameters    the parameters given at the moment of the dataLayer.push(),
     *                      passed through the GTM container and the execute method.
     *                      * applicationId : UAID (you got it when you create a new GA account)
     */
    private void init(Map<String, Object> parameters) {
        String UAID = getString(parameters, Tracker.APPLICATION_ID);

        if (UAID != null) {
            if (UAID.startsWith("UA-")) {
                tracker = analytics.newTracker(UAID);
                this.initialized = true;
                logParamSetWithSuccess(Tracker.APPLICATION_ID, UAID);
            }
            else
                Log.w(this.key+"_handler", "The Universal Analytics Id doesn't seem " +
                        "to correspond to the 'UA-XXXXX-Y' format");
        }
        else
            logMissingParam(new String[]{Tracker.APPLICATION_ID}, GA_INIT);
    }

    /**
     * This method is used to set optional SDK settings.
     *
     * @param parameters    the parameters given at the moment of the dataLayer.push(),
     *                      passed through the GTM container and the execute method.
     *                      * enableOptOut (boolean) : if you want to disable (true) the tracking.
     *                                                 (default = false)
     *                      * disableTracking (boolean) : disable tracking if set to true.
     *                                                   (default = false)
     *                      * dispatchInterval (int) : a period in seconds after which the events
     *                                               will be sent to GA interface (default = 30)
     *                      * allowIdfaCollection (boolean) : enable advertising id (default = true)
     */
    private void set(Map<String, Object> parameters) {

        boolean enableOptOut = getBoolean(parameters, Tracker.ENABLE_OPT_OUT, false);
        analytics.setAppOptOut(enableOptOut);
        logParamSetWithSuccess(Tracker.ENABLE_OPT_OUT, enableOptOut);

        boolean dryRun = getBoolean(parameters, Tracker.DISABLE_TRACKING, false);
        analytics.setDryRun(dryRun);
        logParamSetWithSuccess(Tracker.DISABLE_TRACKING, dryRun);

        int dispatchInterval = getInt(parameters, Tracker.DISPATCH_INTERVAL, 30);
        analytics.setLocalDispatchPeriod(dispatchInterval);
        logParamSetWithSuccess(Tracker.DISPATCH_INTERVAL, dispatchInterval);

        boolean allowIdfaCollection = getBoolean(parameters, ALLOW_IDFA_COLLECTION, true);
        analytics.enableAdvertisingIdCollection(allowIdfaCollection);
        logParamSetWithSuccess(ALLOW_IDFA_COLLECTION, allowIdfaCollection);
    }



/* ****************************************** Tracking ****************************************** */

    /**
     * Used to setup the userId when the user logs in
     *
     * @param parameters    the parameters given at the moment of the dataLayer.push(),
     *                      passed through the GTM container and the execute method.
     *                      * userId: the user Id used for GA
     */
    private void identify(Map<String, Object> parameters) {
        String userId = getString(parameters, User.USER_ID);

        if (userId != null) {
            tracker.set("&uid", userId);
            logParamSetWithSuccess(User.USER_ID, userId);
        }
        else
            logMissingParam(new String[]{User.USER_ID}, GA_IDENTIFY);
    }

    /**
     * Used to build and send a screen event to Google Analytics.
     * Requires a screenName parameter.
     *
     * @param parameters    the parameters given at the moment of the dataLayer.push(),
     *                      passed through the GTM container and the execute method.
     *                      * screenName: the name of the screen you want to register
     */
    private void tagScreen(Map<String, Object> parameters) {
        String screenName = getString(parameters, Screen.SCREEN_NAME);

        if (screenName != null) {
            tracker.setScreenName(screenName);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
            logParamSetWithSuccess(Screen.SCREEN_NAME, screenName);
        }
        else
            logMissingParam(new String[]{Screen.SCREEN_NAME}, GA_TAG_SCREEN);
    }

    /**
     * Method used to create and fire an event to the Google Analytics interface
     * The mandatory parameters are eventCategory and eventAction.
     * eventLabel, eventValue and setNonInteraction are optional.
     *
     * @param parameters    the parameters given at the moment of the dataLayer.push(),
     *                      passed through the GTM container and the execute method.
     *                      * eventCategory: the category the event belongs to
     *                      * eventAction: the type of event
     *                      * eventLabel: a label for this event (optional)
     *                      * eventValue: a value as Long for this event (optional)
     *                      * setNonInteraction: set to true for a non interactive event (optional)
     */
    private void tagEvent(Map<String, Object> parameters) {
        String eventAction = getString(parameters, EVENT_ACTION);
        String eventCategory = getString(parameters, EVENT_CATEGORY);
        String eventLabel = getString(parameters, EVENT_LABEL);

        if (eventAction != null && eventCategory != null) {
            EventBuilderGA event = new EventBuilderGA();
            event.setAction(eventAction);
            event.setCategory(eventCategory);
            logParamSetWithSuccess(EVENT_ACTION, eventAction);
            logParamSetWithSuccess(EVENT_CATEGORY, eventCategory);

            if (eventLabel != null) {
                event.setLabel(eventLabel);
                logParamSetWithSuccess(EVENT_LABEL, eventLabel);
            }
            if (parameters.containsKey(EVENT_VALUE)) {
                Long eventValue = getLong(parameters, EVENT_VALUE, 0);
                event.setValue(eventValue);
                logParamSetWithSuccess(EVENT_VALUE, eventValue);
            }
            if (parameters.containsKey(NON_INTERACTION)) {
                Boolean nonInteraction = getBoolean(parameters, NON_INTERACTION, false);
                event.setNonInteraction(nonInteraction);
                logParamSetWithSuccess(NON_INTERACTION, nonInteraction);
            }

            tracker.send(event.getEvent().build());
        }
        else
            logMissingParam(new String[]{EVENT_ACTION, EVENT_CATEGORY}, GA_TAG_EVENT);
    }



/* ****************************************** Utility ******************************************* */

    /**
     * A callback triggered when an activity starts
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityStarted(Activity activity) {

    }

    /**
     * A callback triggered when an activity is resumed
     * Used to get referring package name and url scheme from and for the session measurement
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityResumed(Activity activity) {

    }

    /**
     * A callback triggered when an activity is paused
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityPaused(Activity activity) {

    }

    /**
     * A callback triggered when an activity stops
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityStopped(Activity activity) {

    }


/* ********************************************************************************************** */

}

