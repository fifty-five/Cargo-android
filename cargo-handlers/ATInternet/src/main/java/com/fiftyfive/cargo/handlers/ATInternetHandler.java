package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.util.Log;

import com.atinternet.tracker.ATInternet;
import com.atinternet.tracker.Gesture;
import com.atinternet.tracker.Tracker;
import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.models.Event;
import com.fiftyfive.cargo.models.Screen;
import com.fiftyfive.cargo.models.User;
import com.google.android.gms.tagmanager.Container;

import java.util.HashMap;
import java.util.Map;

import static com.fiftyfive.cargo.ModelsUtils.getInt;
import static com.fiftyfive.cargo.ModelsUtils.getString;

/**
 * Created by dali on 04/12/15.
 */

public class ATInternetHandler extends AbstractTagHandler {

    /** The AT Tracker */
    public Tracker atTracker;
    public Cargo cargo;

    @Override
    public void execute(String s, Map<String, Object> map) {

        switch (s) {
            case "AT_tagScreen":
                tagScreen(map);
                break;
            case "AT_identify":
                identify(map);
                break;
            case "AT_tagEvent":
                tagEvent(map);
            default:
                Log.i("55", "Function "+s+" is not registered");
        }
    }

    @Override
    public void initialize() {
        cargo = Cargo.getInstance();
        atTracker = ((ATInternet) cargo.getApplication()).getDefaultTracker();
        //todo : check permissions
        this.valid = true;
    }

    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback("AT_tagScreen", this);
        container.registerFunctionCallTagCallback("AT_tagEvent", this);
        container.registerFunctionCallTagCallback("AT_identify", this);
    }



    // as we look for unique visitor, we use the android ID which is unique for each android device
    private void identify(Map<String, Object> parameters){

        final String android_id = getString(parameters, User.USER_ID);

        if (android_id == null) {
            Log.w("CARGO ATInternetHandler", " in identify() missing USER_ID (android_id) parameter. USER_ID hasn't been set");
            return ;
        }

        atTracker.setConfig("identifier", android_id, null);
    }


    private void tagScreen(Map<String, Object> parameters){

        String screenName = getString(parameters, Screen.SCREEN_NAME);

        if(parameters.containsKey(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM1)
                && parameters.containsKey(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM2)) {

            final String customDim1 = getString(parameters, com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM1);
            final String customDim2 = getString(parameters, com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM2);

            atTracker.CustomObjects().add(new HashMap<String, Object>() {{
                put(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM1, customDim1);
                put(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM2, customDim2);
            }});
        }
        atTracker.Screens()
                .add(screenName)
                .setLevel2(getInt(parameters, com.fiftyfive.cargo.models.Tracker.LEVEL2, 0))
                .sendView();
    }


    private void tagEvent(Map<String, Object> parameters){
        String eventName = getString(parameters, Event.EVENT_NAME);
        String eventType = getString(parameters, Event.EVENT_TYPE);

        if (eventType == null) {
            Log.w("CARGO ATInternetHandler", "in tagEvent() no EVENT_TYPE given, event hasn't been sent");
            return ;
        }

        Gesture gesture = setChapters(eventName, parameters);
        gesture.setLevel2(getInt(parameters, com.fiftyfive.cargo.models.Tracker.LEVEL2, 0));

        switch (eventType) {
            case "sendTouch":
                gesture.sendTouch();
                break;
            case "sendNavigation":
                gesture.sendNavigation();
                break;
            case "sendDownload":
                gesture.sendDownload();
                break;
            case "sendExit":
                gesture.sendExit();
                break;
            case "sendSearch":
                gesture.sendSearch();
                break;
            default:
                Log.w("CARGO ATInternetHandler", "in tagEvent() wrong EVENT_TYPE given, event hasn't been sent");
                break;
        }
    }


    private Gesture setChapters(String eventName, Map<String, Object> parameters){
        String chapter1 = getString(parameters, "Chapter1");
        String chapter2 = getString(parameters, "Chapter2");
        String chapter3 = getString(parameters, "Chapter3");

        if (chapter1 == null)
            return (atTracker.Gestures().add(eventName));
        else if (chapter2 == null)
            return (atTracker.Gestures().add(eventName, chapter1));
        else if (chapter3 == null)
            return (atTracker.Gestures().add(eventName, chapter1, chapter2));
        else
            return (atTracker.Gestures().add(eventName, chapter1, chapter2, chapter3));
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
