package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.util.Log;

import com.fiftyfive.cargo.AbstractTagHandler;
import com.google.android.gms.tagmanager.Container;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Map;


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
    }


    @Override
    public void execute(String s, Map<String, Object> map) {

        switch (s) {
            case "Firebase_tagEvent":
                tagEvent(map);
                break;
            default:
                Log.i("Cargo TuneHandler", "Function "+s+" is not registered");
        }
    }


    private void tagEvent(Map<String, Object> map) {

    }


    @Override
    public void onActivityStarted(Activity activity) {

    }


    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }


}
