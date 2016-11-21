
package com.fiftyfive.cargo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tagmanager.Container;

import java.util.Map;

/**
 * Author : louis
 * Created: 03/11/15
 *
 * An Abstract class which TagHandlerManager extends from.
 * It defines what methods should be implemented in each handler.
 */
public abstract class AbstractTagHandler implements Container.FunctionCallTagCallback {

/* ************************************ Variables declaration *********************************** */

    /** A boolean which validates or not if the handler has been initialized */
    public boolean valid;

    /** A boolean which validates or not if the third part SDK has been initialized */
    public boolean initialized;

    /** A reference to the Cargo instance, to retrieve easily the context, among other things */
    public Cargo cargo;

    /** The name of the handler for the logs */
    protected String name;



/* ************************************ Handlers core methods *********************************** */

    /**
     * The only implemented method in this class, stores the Cargo instance as a variable to
     * allow the handler to access to variables of the cargo instance
     * as they are often needed to setup SDK
     */
    public void initialize(){
        cargo = Cargo.getInstance();
        this.valid = true;
        this.initialized = false;
    }

    /**
     * Link a callback at a specific trigger for this handler.
     * eg. of the calls you make :  container.registerFunctionCallTagCallback("handler_init", this);
     *
     * @param container     the container you register your callbacks to
     */
    public abstract void register(Container container);

    /**
     * This method which will allow you to redistribute the callbacks suscribed
     * in the register method into your code
     *
     * @param s         the string you used to register your callback (eg. "handler_init")
     * @param map       a map of the arguments which have been sent with the datalayer.push()
     */
    @Override
    public abstract void execute(String s, Map<String, Object> map);



/* ************************************ Variables declaration *********************************** */

    protected void logMissingParam(String[] parameter, String methodName){
        Log.w(this.name, "Parameter '"+ parameter.toString() +"' is required " +
                "in method '"+ methodName +"'");
    }

    protected void logUninitializedFramework() {
        Log.i(this.name, "You must initialize the framework before using it");
    }

    protected void logParamWithSuccess(String parameter, Object value) {
        Log.v(this.name, "Parameter '"+parameter+"' has been set to '"+
                value.toString()+"' with success");
    }

    protected void logNotFoundValue(String key, String value, Object[] values) {
        Log.w(this.name, "Value '"+value+"' for key '"+key+"' is not found " +
                "among possible values " + values.toString());
    }


/* ***************************** ActivityLifeCycle callback methods ***************************** */

    /**
     * A callback which is called everytime onStart is called in an activity.
     *
     * @param activity  The activity where onStart has been called
     */
    public abstract void onActivityStarted(Activity activity);

    /**
     * A callback which is called everytime onResume is called in an activity.
     *
     * @param activity  The activity where onResume has been called
     */
    public abstract void onActivityResumed(Activity activity);

    /**
     * A callback which is called everytime onPause is called in an activity.
     *
     * @param activity  The activity where onPause has been called
     */
    public abstract void onActivityPaused(Activity activity);

    /**
     * A callback which is called everytime onStop is called in an activity.
     *
     * @param activity  The activity where onStop has been called
     */
    public abstract void onActivityStopped(Activity activity);

/* ********************************************************************************************** */

}
