package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.util.Log;

import com.fiftyfive.cargo.AbstractTagHandler;
import com.google.android.gms.tagmanager.Container;

import java.util.Map;

/**
 * Created by Julien Gil on 12/10/16.
 */

public class AccengageHandler extends AbstractTagHandler {

    final String ACC_init = "ACC_init";

    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback(ACC_init, this);
    }

    @Override
    public void execute(String s, Map<String, Object> map) {

        switch (s) {
            case ACC_init:
                init(map);
                break;
            default:
                Log.i("55", "Function "+s+" is not registered");
        }
    }

    @Override
    public void initialize() {
        //todo : check permissions
        this.valid = true;
    }

    public void init(Map<String, Object> map) {

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
