package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.fiftyfive.cargo.AbstractTagHandler;
import com.google.android.gms.tagmanager.Container;

import java.util.Map;

import static com.fiftyfive.cargo.ModelsUtils.getString;

/**
 * Created by Julien Gil on 12/10/16.
 */

public class AccengageHandler extends AbstractTagHandler {

    final String ACC_init = "ACC_init";

    private String partnerId = null;
    private String privateKey = null;

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
        super.initialize();
        //todo : check permissions
        this.valid = true;
    }

    private void init(Map<String, Object> map) {
        if (!map.containsKey("privateKey") || !map.containsKey("partnerId")) {
            Log.w("Cargo AccengageHandler", " partnerId and/or privateKey is missing for " +
                    "the Accengage SDK initialization");
        }
        else {
            privateKey = getString(map, "privateKey");
            partnerId = getString(map, "partnerId");
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
