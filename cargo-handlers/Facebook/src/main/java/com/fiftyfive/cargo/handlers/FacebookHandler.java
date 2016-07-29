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

    /**
     * From facebook doc :
     * The AppEventsLogger class allows the developer to log various types of events back to Facebook.
     * (https://developers.facebook.com/docs/reference/android/current/class/AppEventsLogger/)
     */
    protected AppEventsLogger facebookLogger;

    final String FB_init = "FB_init";
    final String FB_tagEvent = "FB_tagEvent";
    final String FB_purchase = "FB_purchase";
    final String VALUE_TO_SUM = "valueToSum";

    /**
     * Init properly facebook SDK
     */
    @Override
    public void initialize() {
        // call on initialize() in AbstractTagHandler class in order to get cargo instance
        super.initialize();

        FacebookSdk.sdkInitialize(cargo.getApplication());
        facebookLogger = AppEventsLogger.newLogger(cargo.getApplication());

        //todo : check permissions
        this.valid = FacebookSdk.isInitialized();
    }


    /**
     * Register the callbacks to GTM. These will be triggered after a tag has been sent
     *
     * @param container The instance of the GTM container we register the callbacks to
     */
    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback(FB_init, this);
        container.registerFunctionCallTagCallback(FB_tagEvent, this);
        container.registerFunctionCallTagCallback(FB_purchase, this);

    }

    /**
     * This one will be called after a tag has been sent
     *
     * @param s     The method you aime to call (this should be define in GTM interface)
     * @param map   A map key-object used as a way to give parameters to the class method aimed here
     */
    @Override
    public void execute(String s, Map<String, Object> map) {

        switch (s) {
            case FB_init:
                init(map);
                break;
            case FB_tagEvent:
                tagEvent(map);
                break;
            case FB_purchase:
                purchase(map);
                break;
            default:
                Log.i("Cargo FacebookHandler", " Function "+s+" is not registered");
        }
    }


    /**
     * The method you need to call first. Register your facebook app id to the facebook SDK
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method
     *              * application id : the app id facebook gives you when you setup your fb app
     *              * Tracker.ENABLE_DEBUG : the value of the bool to turn on/off the facebook debug
     */
    private void init(Map<String, Object> map) {

        if(map.containsKey(Tracker.APPLICATION_ID)){
            FacebookSdk.setApplicationId(map.remove(Tracker.APPLICATION_ID).toString());
        }
        FacebookSdk.setIsDebugEnabled(getBoolean(map, Tracker.ENABLE_DEBUG, false));

    }

    /**
     * The method used to send an event to your facebook app
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method.
     *              * event name : the only parameter requested here
     *              * valueToSum : When reported, all of the valueToSum properties will be summed
     *                              together. It is an arbitrary number that can represent any value
     *                              (e.g., a price or a quantity).
     *              * parameters : any other key in the map will be taken as a parameter linked
     *                              to the event. You can set up to 25 parameters for a given event.
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
        map.remove(Event.EVENT_NAME);

        // attach a valueToSum to the event if it exists
        if (map.containsKey(VALUE_TO_SUM)) {
            valueToSum = getDouble(map, VALUE_TO_SUM, 0);
            map.remove(VALUE_TO_SUM);

            // check for parameters and set them to the event if they exist.
            if (map.size() > 0) {
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
        if (map.size() > 0) {
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
     *              * cartPrice : represents the amount of money spent on the purchase
     *              * currencyCode : the code of the currency the purchase has been registered with
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

    /**
     * A simple method to put the parameters given in a bundle and to return it
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method.
     * @return      the bundle containing all the parameters initially given for an event
     *
     */
    protected Bundle eventParamBuilder(Map<String, Object> map) {

        Bundle bundle = new Bundle();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof String)
                bundle.putString(entry.getKey(), getString(map, entry.getKey()));
            else if (entry.getValue() instanceof Boolean) {
                if (getBoolean(map, entry.getKey(), false))
                    bundle.putInt(entry.getKey(), 1);
                else
                    bundle.putInt(entry.getKey(), 0);
            }
            else if (entry.getValue() instanceof Integer)
                bundle.putInt(entry.getKey(), getInt(map, entry.getKey(), 0));
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




}
