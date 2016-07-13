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

import static com.fiftyfive.cargo.ModelsUtils.getString;


/**
 * Author : louis
 * Created: 03/11/15
 *
 *  * The class which handles interactions with the Facebook SDK
 */
public class FirebaseHandler extends AbstractTagHandler {


    private FirebaseAnalytics mFirebaseAnalytics;

    /**
     * Init properly the SDK, not needed here
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
        container.registerFunctionCallTagCallback("Firebase_tagEvent", this);
        container.registerFunctionCallTagCallback("Firebase_identify", this);
    }


    @Override
    public void execute(String s, Map<String, Object> map) {

        switch (s) {
            case "Firebase_tagEvent":
                tagEvent(map);
                break;
            case "Firebase_identify":
                identify(map);
                break;
            default:
                Log.i("Cargo TuneHandler", "Function "+s+" is not registered");
        }
    }
    
    private void tagEvent(Map<String, Object> map) {

        if (map.containsKey(Event.EVENT_NAME)) {
            String eventName = map.remove(Event.EVENT_NAME).toString();
            Bundle params = new Bundle();

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof String)
                    params.putString(entry.getKey(), map.remove(entry.getKey()).toString());
                else if (entry.getValue() instanceof Long)
                    params.putLong(entry.getKey(), (long)map.remove(entry.getKey()));
            }
            mFirebaseAnalytics.logEvent(eventName, params);
        }
        else
            Log.w("Cargo FirebaseHandler", " in order to create an event, " +
                    "an eventName is required. The event hasn't been created.");
    }

    private void identify(Map<String, Object> map) {
        if (map.containsKey(User.USER_ID)) {
            mFirebaseAnalytics.setUserId(getString(map, User.USER_ID));
            map.remove(User.USER_ID);
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            mFirebaseAnalytics.setUserProperty(entry.getKey(), getString(map, entry.getKey()));
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }


    @Override
    public void onActivityResumed(Activity activity) {
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, new Bundle());
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }


}
