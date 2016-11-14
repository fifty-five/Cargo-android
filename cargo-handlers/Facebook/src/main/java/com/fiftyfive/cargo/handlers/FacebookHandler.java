package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.appevents.AppEventsLogger;
import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.models.Event;
import com.fiftyfive.cargo.models.Tracker;
import com.fiftyfive.cargo.models.Transaction;
import com.google.android.gms.tagmanager.Container;
import com.facebook.FacebookSdk;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.Set;

import static com.fiftyfive.cargo.ModelsUtils.getBoolean;
import static com.fiftyfive.cargo.ModelsUtils.getDouble;
import static com.fiftyfive.cargo.ModelsUtils.getInt;
import static com.fiftyfive.cargo.ModelsUtils.getString;

/**
 * Author : louis
 * Created: 03/11/15
 *
 * The class which handles interactions with the Facebook SDK
 */
public class FacebookHandler extends AbstractTagHandler {

/* ************************************ Variables declaration *********************************** */

    /** The AppEventsLogger allows to log various types of events back to Facebook. */
    protected AppEventsLogger facebookLogger;

    /** A boolean which defines if the instance has been correctly initialized */
    private boolean init = false;

    /** Constants used to define callbacks in the register and in the execute method */
    private final String FB_INIT = "FB_init";
    private final String FB_TAG_EVENT = "FB_tagEvent";
    private final String FB_PURCHASE = "FB_purchase";
    private final String VALUE_TO_SUM = "valueToSum";


/* ************************************ Handler core methods ************************************ */

    /**
     * Called by the TagHandlerManager, initialize the core of the handler
     */
    @Override
    public void initialize() {
        super.initialize();

        FacebookSdk.sdkInitialize(cargo.getApplication());
        facebookLogger = AppEventsLogger.newLogger(cargo.getApplication());

        this.valid = FacebookSdk.isInitialized();
    }

    /**
     * Register the callbacks to the container. After a dataLayer.push(),
     * these will trigger the execute method of this handler.
     *
     * @param container The instance of the GTM container we register the callbacks to
     */
    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback(FB_INIT, this);
        container.registerFunctionCallTagCallback(FB_TAG_EVENT, this);
        container.registerFunctionCallTagCallback(FB_PURCHASE, this);

    }

    /**
     * A callback method for the registered callbacks method name mentioned in the register method.
     *
     * @param s     The method name called through the container (defined in the GTM interface)
     * @param map   A map key-object used as a way to give parameters to the class method aimed here
     */
    @Override
    public void execute(String s, Map<String, Object> map) {

        // a check fo the init method
        if (s.equals(FB_INIT))
            init(map);
        // if the SDK hasn't been initialized, logs a warning
        else if (!init) {
            Log.w("Cargo FacebookHandler", " the handler hasn't be initialized, " +
                    "please do so before doing anything else.");
        }
        // if the SDK is properly initialized, check for which method is called
        else {
            switch (s) {
                case FB_TAG_EVENT:
                    tagEvent(map);
                    break;
                case FB_PURCHASE:
                    purchase(map);
                    break;
                default:
                    Log.i("Cargo FacebookHandler", " Function "+s+" is not registered");
            }
        }
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

        if(map.containsKey(Tracker.APPLICATION_ID)){
            FacebookSdk.setApplicationId(getString(map, Tracker.APPLICATION_ID));
            init = true;
        }
        FacebookSdk.setIsDebugEnabled(getBoolean(map, Tracker.ENABLE_DEBUG, false));
    }

    /**
     * The getter for the init boolean, returning if the tagHandler has been initialized
     *
     * @return the boolean
     */
    public boolean isInitialized() { return init; }



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

        String eventName;
        double valueToSum;
        Bundle parameters;

        if (!map.containsKey(Event.EVENT_NAME)) {
            Log.w("Cargo FacebookHandler", " in order to create an event, " +
                    "an eventName is mandatory. The event hasn't been created.");
            return ;
        }

        eventName = getString(map, Event.EVENT_NAME);

        // attach a valueToSum to the event if it exists
        if (map.containsKey(VALUE_TO_SUM)) {
            valueToSum = getDouble(map, VALUE_TO_SUM, 0);
            map.remove(VALUE_TO_SUM);

            // check for parameters and set them to the event if they exist.
            if (map.size() > 1) {
                parameters = eventParamBuilder(map);
                // fire the tag with the given parameters & valueToSum
                facebookLogger.logEvent(eventName, valueToSum, parameters);
                return ;
            }
            // fire the tag with the given valueToSum
            facebookLogger.logEvent(eventName, valueToSum);
            return ;
        }

        // attach parameters to the event if they exist
        if (map.size() > 1) {
            parameters = eventParamBuilder(map);
            // fire the tag with the given parameters
            facebookLogger.logEvent(eventName, parameters);
            return ;
        }

        // fire the tag
        facebookLogger.logEvent(eventName);
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
        if (!map.containsKey(Transaction.TRANSACTION_TOTAL) || !map.containsKey(Transaction.TRANSACTION_CURRENCY_CODE)) {
            Log.w("Cargo FacebookHandler", " in order to log a purchase, you have to " +
                    "set a moneySpent and a currencyCode parameters. Operation has been cancelled");
            return ;
        }

        double price = getDouble(map, Transaction.TRANSACTION_TOTAL, -1);
        String currency = getString(map, Transaction.TRANSACTION_CURRENCY_CODE);
        facebookLogger.logPurchase(BigDecimal.valueOf(price), Currency.getInstance(currency));
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
        String eventName = getString(map, Event.EVENT_NAME);
        map.remove(Event.EVENT_NAME);

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
                Log.i("Cargo FacebookHandler", " parameter with key " + key + " isn't " +
                    "recognize as String, Boolean or int and will be ignored for event " + eventName);
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

    /**
     * This setter is made for testing purpose and shouldn't be used outside of the test class.
     *
     * @param value the boolean value you want the "init" attribute to be set with.
     */
    protected void setInitialize(boolean value) {
        this.init = value;
    }


/* ********************************************************************************************** */

}
