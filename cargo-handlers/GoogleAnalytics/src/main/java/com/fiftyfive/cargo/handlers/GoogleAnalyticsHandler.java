package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.models.Tracker;
import com.fiftyfive.cargo.models.User;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.tagmanager.Container;

import java.util.Map;

/**
 * Created by dali on 25/11/15.
 */

public class GoogleAnalyticsHandler extends AbstractTagHandler {

    public GoogleAnalytics analytics = null;

    @Override
    public void execute(String s, Map<String, Object> map) {
        switch (s) {
            case "GA_init":
                init(map);
                break;
            default:
                Log.i("55", "Function " + s + " is not registered");
        }
    }

    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback("GA_init", this);
    }

    private void init(Map<String, Object> map){

        final ObjectMapper mapper = new ObjectMapper();
        Tracker tracker = mapper.convertValue(map, Tracker.class);

        if(map.containsKey("enableOptOut")){
            analytics.setAppOptOut(tracker.isEnableOptOut());
        }
        if(map.containsKey("disableTracking")){
            analytics.setDryRun(tracker.isDisableTracking());
        }
        if(map.containsKey("trackerDispatchPeriod")){
            analytics.setLocalDispatchPeriod(tracker.getTrackerDispatchPeriod());
        }
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
