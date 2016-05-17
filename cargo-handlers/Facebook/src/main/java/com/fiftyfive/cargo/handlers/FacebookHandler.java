package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.util.Log;

import com.facebook.appevents.AppEventsLogger;
import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.models.Event;
import com.fiftyfive.cargo.models.Tracker;
import com.google.android.gms.tagmanager.Container;
import com.facebook.FacebookSdk;


import java.util.Map;

import static com.fiftyfive.cargo.ModelsUtils.getBoolean;
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
    public AppEventsLogger facebookLogger;


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
        container.registerFunctionCallTagCallback("FB_init", this);
        container.registerFunctionCallTagCallback("FB_tagEvent", this);

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
            case "FB_init":
                init(map);
                break;
            case "FB_tagEvent":
                tagEvent(map);
                break;
            default:
                Log.i("55", "Function "+s+" is not registered");
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

        if(map.containsKey("applicationId")){
            FacebookSdk.setApplicationId(map.remove("applicationId").toString());
        }
        FacebookSdk.setIsDebugEnabled(getBoolean(map, Tracker.ENABLE_DEBUG, false));

    }

    /**
     * The method used to send an event to your facebook app
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method.
     *              The only parameter here is the event name
     */
    private void tagEvent(Map<String, Object> map){

        facebookLogger.logEvent(getString(map, Event.EVENT_NAME));
    }


    @Override
    public void onActivityStarted(Activity activity) {

    }

    /**
     * Logs the launch of an activity
     *
     * @param activity The current activity
     */
    @Override
    public void onActivityResumed(Activity activity) {
        AppEventsLogger.activateApp(activity);
    }

    /**
     * Logs the pause of an activity
     *
     * @param activity The current activity
     */
    @Override
    public void onActivityPaused(Activity activity) {
        AppEventsLogger.deactivateApp(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }




}
