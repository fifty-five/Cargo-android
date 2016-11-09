package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.models.Event;
import com.fiftyfive.cargo.models.User;
import com.google.android.gms.tagmanager.Container;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Map;
import java.util.Set;

import static com.fiftyfive.cargo.ModelsUtils.getBoolean;
import static com.fiftyfive.cargo.ModelsUtils.getLong;
import static com.fiftyfive.cargo.ModelsUtils.getString;


/**
 * Author : louis
 * Created: 03/11/15
 *
 *  * The class which handles interactions with the Firebase SDK
 */
public class FirebaseHandler extends AbstractTagHandler {

    private final String Firebase_init = "Firebase_init";
    private final String Firebase_tagEvent = "Firebase_tagEvent";
    private final String Firebase_identify = "Firebase_identify";
    private final String ENABLE_COLLECTION = "enableCollection";

    protected FirebaseAnalytics mFirebaseAnalytics;


    /**
     * Init properly the SDK, getting the instance of Firebase.
     */
    @Override
    public void initialize() {
        // call on initialize() in AbstractTagHandler class in order to get cargo instance
        super.initialize();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(cargo.getApplication());

        this.valid = true;
    }

    /**
     * Register the callbacks to GTM. These will be triggered after a dataLayer.push()
     *
     * @param container The instance of the GTM container we register the callbacks to
     */
    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback(Firebase_init, this);
        container.registerFunctionCallTagCallback(Firebase_identify, this);
        container.registerFunctionCallTagCallback(Firebase_tagEvent, this);
    }

    /**
     * This one will be called after an event has been pushed to the dataLayer
     *
     * @param s     The method you aime to call (this should be define in GTM interface)
     * @param map   A map key-object used as a way to give parameters to the class method aimed here
     */
    @Override
    public void execute(String s, Map<String, Object> map) {

        switch (s) {
            case Firebase_init:
                init(map);
                break;
            case Firebase_identify:
                identify(map);
                break;
            case Firebase_tagEvent:
                tagEvent(map);
                break;
            default:
                Log.i("Cargo TuneHandler", "Function "+s+" is not registered");
        }
    }

    /**
     * The method you may call first if you want to disable the Firebase analytics collection
     * This setting is persisted across app sessions. By default it is enabled.
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method
     *              * ANALYTICS_COLLECTION : a boolean true/false for collection enabled/disabled
     */
    private void init(Map<String, Object> map) {

        if (map.containsKey(ENABLE_COLLECTION)) {
            boolean enabled = getBoolean(map, ENABLE_COLLECTION, true);
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(enabled);
        }
    }

    /**
     * In order to identify the user as a unique visitor, with a custom ID
     * You may supply up to 25 unique UserProperties per app, and you can use the name
     * and value of your choosing for each one. UserProperty names can be up to 24 characters
     * long, may only contain alphanumeric characters and underscores ("_"), and must start
     * with an alphabetic character. UserProperty values can be up to 36 characters long.
     * The "firebase_" prefix is reserved and should not be used.
     * For more information, please have a look at http://tinyurl.com/h6asbgr
     *
     * @param map    the parameters given at the moment of the dataLayer.push(),
     *               passed through the GTM container and the execute method.
     *               * USER_ID is the only parameter requested here
     *               * some user properties you may want to set
     */
    private void identify(Map<String, Object> map) {
        if (map.containsKey(User.USER_ID)) {
            mFirebaseAnalytics.setUserId(getString(map, User.USER_ID));
            map.remove(User.USER_ID);
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            mFirebaseAnalytics.setUserProperty(entry.getKey(), getString(map, entry.getKey()));
        }
    }

    /**
     * Method used to create and fire an event to the Firebase Console
     * The mandatory parameters is EVENT_NAME which is a necessity to build the event
     * Without this parameter, the event won't be built.
     * After the creation of the event object, some attributes can be added,
     * using the map obtained from the gtm container.
     *
     * Events are custom here, but there is also some templated events you'd like to know.
     * For more information about the Firebase events : http://tinyurl.com/hvyksd5
     * and their parameters : http://tinyurl.com/jdv9xsf
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method.
     *              * EVENT_NAME : the only parameter requested here
     */
    private void tagEvent(Map<String, Object> map) {

        // check for the eventName parameter. If it doesn't exist, the tag is aborted
        if (map.containsKey(Event.EVENT_NAME)) {
            String eventName = map.remove(Event.EVENT_NAME).toString();

            // if the map contains other parameters than the event name, loop on the parameters and
            // put them in a bundle in order to send them as event parameters
            if (map.size() > 0) {

                Bundle params = new Bundle();
                Set<String> keys = map.keySet();

                for (String key : keys) {
                    if (map.get(key) instanceof String)
                        params.putString(key, getString(map, key));
                    else if (map.get(key) instanceof Long)
                        params.putLong(key, getLong(map, key, 0));
                    else
                        Log.i("Cargo FirebaseHandler", " parameter with key " + key + " isn't " +
                                "recognize as String or long and will be ignored for event " + eventName);
                }
                mFirebaseAnalytics.logEvent(eventName, params);
                return;
            }
            // use null which means that there is no parameters
            mFirebaseAnalytics.logEvent(eventName, null);
        }
        else
            Log.w("Cargo FirebaseHandler", " in order to create an event, " +
                    "an eventName is required. The event hasn't been created.");
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
     * Is used to measure session
     *
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityResumed(Activity activity) {
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, new Bundle());
    }

    /**
     * A callback triggered when an activity is paused
     *
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityPaused(Activity activity) {

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
