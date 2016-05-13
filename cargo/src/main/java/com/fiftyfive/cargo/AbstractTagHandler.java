package com.fiftyfive.cargo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.google.android.gms.tagmanager.Container;

import java.util.Map;

/**
 * Author : louis
 * Created: 03/11/15
 *
 * An Abstract class which defines what are the methods which should be implemented in each handler
 */
public abstract class AbstractTagHandler implements Container.FunctionCallTagCallback {

    @Override
    public abstract void execute(String s, Map<String, Object> map);

    public boolean valid;

    public Cargo cargo;

    /**
     * The only implemented method in this class, stores the Cargo instance as a variable to
     * allow the handler to access to variables of the cargo instance
     * as they are always needed to setup SDK
     */
    public void initialize(){
        cargo = Cargo.getInstance();
    }

    public abstract void register(Container container);

    public abstract void onActivityStarted(Activity activity);

    public abstract void onActivityResumed(Activity activity);

    public abstract void onActivityPaused(Activity activity);

    public abstract void onActivityStopped(Activity activity);


}
