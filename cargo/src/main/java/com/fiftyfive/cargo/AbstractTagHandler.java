
package com.fiftyfive.cargo;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.tagmanager.Container;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by louis on 03/11/15.
 * Copyright © 2016 fifty-five All rights reserved.
 *
 * An Abstract class which TagHandlerManager extends from.
 * It defines what methods should be implemented in each handler.
 */
public abstract class AbstractTagHandler implements Container.FunctionCallTagCallback {

/* ************************************ Variables declaration *********************************** */

    /** A boolean which validates or not if the handler has been instantiated */
    public boolean valid = false;

    /** A boolean which validates or not if the third part SDK has been initialized */
    protected boolean initialized = false;

    /** A reference to the Cargo instance, to retrieve easily the context, among other things */
    public Cargo cargo;

    /** The name of the handler for the logs */
    protected String name;

    /** The key of the handler for the logs */
    protected String key;



/* ************************************ Handlers core methods *********************************** */

    /**
     * Default method.
     */
    public void initialize(){

    }

    /**
     * Stores name and key of the handler, plus the Cargo instance as a variable to
     * allow the handler to access to variables of the cargo instance
     * as they are often needed to setup SDK
     *
     * @param hKey  key as a string for this handler
     * @param hName name of the handler
     */
    public void initialize(String hKey, String hName){
        this.cargo = Cargo.getInstance();
        this.key = hKey;
        this.name = hName;
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
    public abstract void execute(String s, Map<String, Object> map);

    public void validate(boolean isValid) {
        this.valid = isValid;
        if (isValid)
            Log.v(this.key+"_handler", this.name+" SDK has started without error");
        else
            Log.e(this.key+"_handler", "Failed to start the "+this.name+" SDK.");
    }

    /**
     * Sets initialized boolean to true if the handler has been correctly initialized
     *
     * @param value the boolean value you want the "init" attribute to be set with.
     */
    public void setInitialized(boolean value) {
        initialized = value;
        if (initialized) {
            Log.d(this.key + "_handler",
                    "The handler has been correctly initialized and is ready to use");
        }
        else {
            Log.w(this.key + "_handler",
                    "DUH ! Something went wrong, the handler hasn't been initialized");
        }
    }

    /**
     * The getter for the initialized boolean, returning whether the third part SDK has been
     * initialized or not.
     *
     * @return the boolean
     */
    public boolean isInitialized() { return initialized; }

/* ************************************ Variables declaration *********************************** */

    /**
     * Logs a warning about a mandatory parameter missing in a method call.
     * Prints the name of the handler it happens in.
     *
     * @param parameter the missing parameter.
     * @param methodName the tag of the method the parameter is missing in.
     */
    protected void logMissingParam(String[] parameter, String methodName){
        Log.w(this.key+"_handler", "Parameter '"+ parameter.toString() +"' is required " +
                "in method '"+ methodName +"'");
    }

    /**
     * Logs an info about the need to initialize the third part SDK before using it.
     * Prints the name of the handler it happens in.
     */
    protected void logUninitializedFramework() {
        Log.i(this.key+"_handler", "You must initialize the " +
                this.name + " framework before using it");
    }

    /**
     * Verbose log when a parameter is successfully set to a value.
     * Prints the name of the handler it happens in.
     *
     * @param parameter the name of the parameter which has been set.
     * @param value the value the parameter has been set to.
     */
    protected void logParamSetWithSuccess(String parameter, Object value) {
        Log.v(this.key+"_handler", "Parameter '"+parameter+"' has been set to '"+
                value.toString()+"' with success");
    }

    /**
     * Logs a warning about a parameter which seems to have a wrong value compared to the
     * possible value set. Prints the name of the handler it happens in.
     *
     * @param key the key of the parameter the value doesn't match.
     * @param value the value missing in the preset.
     * @param values the set of the possible values.
     */
    protected void logNotFoundValue(String key, String value, Object[] values) {
        Log.w(this.key+"_handler", "Value '"+value+"' for key '"+key+"' is not found " +
                "among possible values " + values.toString());
    }

    /**
     * Debug log which shows if a function tag doesnt match any implemented method in the handler.
     * Prints the name of the handler it happens in.
     *
     * @param functionTag the function tag which doesn't match any method.
     */
    protected void logUnknownFunction(String functionTag) {
        Log.d(this.key+"_handler", "Unable to find a method matching the function tag ["+functionTag+"].");
    }

    protected void logReceivedFunction(String functionTag, Map<String, Object> map) {
        Log.i(this.key+"_handler", "Received function "+ functionTag
                +" with parameters "+ Arrays.toString(map.entrySet().toArray())+".");
    }

    protected void logUncastableParam(String parameter, String type) {
        Log.e(this.key+"_handler", "Parameter "+ parameter +" cannot be casted to "+ type +".");
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
