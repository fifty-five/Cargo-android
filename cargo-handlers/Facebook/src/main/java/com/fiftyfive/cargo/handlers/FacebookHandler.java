package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.util.Log;

import com.facebook.appevents.AppEventsLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.models.Event;
import com.fiftyfive.cargo.models.Tracker;
import com.google.android.gms.tagmanager.Container;
import com.facebook.FacebookSdk;


import java.util.Map;

/**
 * Created by louis on 03/11/15.
 */

public class FacebookHandler extends AbstractTagHandler {

    public AppEventsLogger facebookLogger;
    private boolean init = false;

    public Cargo cargo = Cargo.getInstance();

    @Override
    public void execute(String s, Map<String, Object> map) {

        switch (s) {
            case "FB_init":
                init(map);
                break;
            case "FB_tagEvent":
                tagEvent(map);
                break;
            case "FB_tagTracker":
                tagTracker(map);
                break;

            default:
                Log.i("55", "Function "+s+" is not registered");
        }
    }



    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback("FB_init", this);
        container.registerFunctionCallTagCallback("FB_tagEvent", this);

    }


    private void init(Map<String, Object> map) {

        FacebookSdk.sdkInitialize(cargo.getApplication());
        if(map.containsKey("applicationId")){
            FacebookSdk.setApplicationId(map.remove("applicationId").toString());
        }
        init = FacebookSdk.isInitialized();

      }


    private void tagEvent(Map<String, Object> map){
        final ObjectMapper mapper = new ObjectMapper();
        Event event = mapper.convertValue(map, Event.class);

         facebookLogger.logEvent(event.getEventName());
    }

    private void tagTracker(Map<String, Object> map){
        final ObjectMapper mapper = new ObjectMapper();
        Tracker tracker = mapper.convertValue(map, Tracker.class);

        FacebookSdk.setIsDebugEnabled(tracker.isEnableDebug());
    }



    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        AppEventsLogger.activateApp(cargo.getApplication());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        AppEventsLogger.deactivateApp(cargo.getApplication());
    }

    public boolean isInitialized(){
        return init;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }




}
