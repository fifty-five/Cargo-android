package com.fiftyfive.cargo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.google.android.gms.tagmanager.Container;

import java.util.Map;

/**
 * Created by louis on 03/11/15.
 */
public abstract class AbstractTagHandler implements Container.FunctionCallTagCallback {

    @Override
    public abstract void execute(String s, Map<String, Object> map);

    public boolean valid;

    public Cargo cargo;


    public void initialize(){
        cargo = Cargo.getInstance();
    }

    public abstract void register(Container container);

    public abstract void onActivityStarted(Activity activity);

    public abstract void onActivityResumed(Activity activity);

    public abstract void onActivityPaused(Activity activity);

    public abstract void onActivityStopped(Activity activity);


}
