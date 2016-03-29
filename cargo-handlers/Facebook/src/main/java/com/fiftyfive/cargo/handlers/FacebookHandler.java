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
 * Created by louis on 03/11/15.
 */

public class FacebookHandler extends AbstractTagHandler {

    public AppEventsLogger facebookLogger;
    private boolean init = false;

    public Cargo cargo;



    @Override
    public void initialize() {
        this.cargo = Cargo.getInstance();
        facebookLogger = AppEventsLogger.newLogger(cargo.getApplication());

        //todo : check permissions
        this.valid =true;
    }


    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback("FB_init", this);
        container.registerFunctionCallTagCallback("FB_tagEvent", this);

    }

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



    private void init(Map<String, Object> map) {

        FacebookSdk.sdkInitialize(cargo.getApplication());
        if(map.containsKey("applicationId")){
            FacebookSdk.setApplicationId(map.remove("applicationId").toString());
        }
        FacebookSdk.setIsDebugEnabled(getBoolean(map, Tracker.ENABLE_DEBUG, false));

        init = FacebookSdk.isInitialized();

    }


    private void tagEvent(Map<String, Object> map){

        facebookLogger.logEvent(getString(map, Event.EVENT_NAME));
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        AppEventsLogger.activateApp(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        AppEventsLogger.deactivateApp(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }




}
