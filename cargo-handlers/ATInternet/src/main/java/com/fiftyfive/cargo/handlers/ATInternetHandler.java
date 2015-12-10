package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.util.Log;

import com.atinternet.tracker.ATInternet;
import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.models.Screen;
import com.google.android.gms.tagmanager.Container;
import com.atinternet.tracker.Tracker;
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

    @Override
    public void execute(String s, Map<String, Object> map) {

        switch (s) {
            case "AT_tagScreen":
                tagScreen(map);
                break;
            default:
                Log.i("55", "Function "+s+" is not registered");
        }
    }

    @Override
    public void initialize() {
        Cargo cargo = Cargo.getInstance();
        atTracker = ((ATInternet) cargo.getApplication()).getDefaultTracker();
        //todo : check permissions
        this.valid = true;
    }


    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback("AT_tagScreen", this);
        container.registerFunctionCallTagCallback("AT_tagTransaction", this);
    }

    private void tagScreen(Map<String, Object> parameters){

        String screenName = getString(parameters, Screen.SCREEN_NAME);

        if(parameters.containsKey(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM1)
                && parameters.containsKey(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM2)) {

            final String customDim1 = getString(parameters, com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM1);
            final String customDim2 = getString(parameters, com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM2);

            atTracker.CustomObjects().add(new HashMap<String, Object>() {{
                put(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM1, customDim1);put(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM2, customDim2);
            }});
        }
        atTracker.Screens().add(screenName).setLevel2(getInt(parameters, com.fiftyfive.cargo.models.Tracker.LEVEL2, 0)).sendView();

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
