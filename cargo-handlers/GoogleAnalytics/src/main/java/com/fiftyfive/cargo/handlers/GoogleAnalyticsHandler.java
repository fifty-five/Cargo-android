package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;


import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.models.Tracker;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.tagmanager.Container;

import java.util.Map;

import static com.fiftyfive.cargo.ModelsUtils.getBoolean;
import static com.fiftyfive.cargo.ModelsUtils.getInt;

/**
 * Created by dali on 25/11/15.
 */

public class GoogleAnalyticsHandler extends AbstractTagHandler {

    public GoogleAnalytics analytics;


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
    public void initialize() {
        super.initialize();
        Context context = cargo.getApplication();
        analytics = GoogleAnalytics.getInstance(context);

        //todo : check permissions

        this.valid = true;

    }


    private void init(Map<String, Object> parameters){

        analytics.setAppOptOut(getBoolean(parameters, Tracker.ENABLE_OPT_OUT, false));
        analytics.setDryRun(getBoolean(parameters, Tracker.DISABLE_TRACKING, false));
        analytics.setLocalDispatchPeriod(getInt(parameters, Tracker.DISPATCH_PERIOD, 30));
    }

    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback("GA_init", this);
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
