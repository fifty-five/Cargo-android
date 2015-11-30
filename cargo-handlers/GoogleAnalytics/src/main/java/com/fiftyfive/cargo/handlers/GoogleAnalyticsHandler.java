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
            case "GA_setOptOut":
                setOptOut(map);
                break;
            case "GA_setDryRun":
                setDryRun(map);
                break;
            case "GA_setTrackerDispatchPeriod":
                setTrackerDispatchPeriod(map);
                break;
            default:
                Log.i("55", "Function " + s + " is not registered");
        }
    }

    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback("GA_setOptOut", this);
        container.registerFunctionCallTagCallback("GA_setDryRun", this);
        container.registerFunctionCallTagCallback("GA_setTrackerDispatchPeriod", this);
    }

    private void setOptOut(Map<String, Object> parameters){
        final ObjectMapper mapper = new ObjectMapper();
        Tracker tracker = mapper.convertValue(parameters, Tracker.class);
        analytics.setAppOptOut(tracker.isOptOut());
    }

    private void setDryRun(Map<String, Object> parameters){
        final ObjectMapper mapper = new ObjectMapper();
        Tracker tracker = mapper.convertValue(parameters, Tracker.class);
        analytics.setDryRun(tracker.isDryRun());
    }

    private void setTrackerDispatchPeriod(Map<String, Object> parameters){
        final ObjectMapper mapper = new ObjectMapper();
        Tracker tracker = mapper.convertValue(parameters, Tracker.class);
        analytics.setLocalDispatchPeriod(tracker.getTrackerDispatchPeriod());
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
