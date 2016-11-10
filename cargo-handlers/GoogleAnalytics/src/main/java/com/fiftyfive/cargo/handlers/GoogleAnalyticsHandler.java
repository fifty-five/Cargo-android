package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;


import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.models.Tracker;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.tagmanager.Container;

import java.util.Map;

import static com.fiftyfive.cargo.ModelsUtils.getBoolean;
import static com.fiftyfive.cargo.ModelsUtils.getInt;

/**
 * Created by dali on 25/11/15.
 *
 * The class which handles interactions with the Google Analytics SDK.
 */
public class GoogleAnalyticsHandler extends AbstractTagHandler {

/* ************************************ Variables declaration *********************************** */

    /** The tracker of the Google Analytics SDK which send the events */
    protected GoogleAnalytics analytics;

    /** Constants used to define callbacks in the register and in the execute method */
    private final String GA_INIT = "GA_init";



/* ************************************ Handler core methods ************************************ */

    /**
     * Called by the TagHandlerManager, initialize the core of the handler
     */
    @Override
    public void initialize() {
        super.initialize();
        Context context = cargo.getApplication();
        analytics = GoogleAnalytics.getInstance(context);
        this.valid = true;
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
    }

    /**
     * A callback method for the registered callbacks method name mentioned in the register method.
     *
     * @param s     The method name called through the container (defined in the GTM interface)
     * @param map   A map key-object used as a way to give parameters to the class method aimed here
     */
    @Override
    public void execute(String s, Map<String, Object> map) {
        switch (s) {
            case GA_INIT:
                init(map);
                break;
            default:
                Log.i("55", "Function " + s + " is not registered");
        }
    }



/* ************************************* SDK initialization ************************************* */

    /**
     * This method is used to override SDK settings.
     *
     * @param parameters    the parameters given at the moment of the dataLayer.push(),
     *                      passed through the GTM container and the execute method.
     *                      * enableOptOut (boolean) : whether you want to enable opt out or not.
     *                      * disableTracking (boolean) : disable tracking if set to true.
     *                      * dispatchPeriod (int) : a period in seconds after which the events
     *                                               will be sent to GA interface
     */
    private void init(Map<String, Object> parameters){

        boolean enable = getBoolean(parameters, Tracker.ENABLE_OPT_OUT, false);
        boolean dryRun = getBoolean(parameters, Tracker.DISABLE_TRACKING, false);
        int localDispatch = getInt(parameters, Tracker.DISPATCH_PERIOD, 30);

        if (parameters.containsKey(Tracker.ENABLE_OPT_OUT))
            analytics.setAppOptOut(enable);
        if (parameters.containsKey(Tracker.DISABLE_TRACKING))
            analytics.setDryRun(dryRun);
        if (parameters.containsKey(Tracker.DISPATCH_PERIOD))
            analytics.setLocalDispatchPeriod(localDispatch);
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
