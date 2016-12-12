package com.fiftyfive.cargo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by louis on 04/11/15.
 * Copyright © 2016 fifty-five All rights reserved.
 *
 * The TagHandlerManager stores all the registered handlers.
 * Since the class implements ActivityLifecycleCallbacks,
 * it calls on the associated method in each handler when a callback is triggered.
 */
public class TagHandlerManager implements Application.ActivityLifecycleCallbacks {

/* *********************************** Variables declaration ************************************ */

   /** The list which stores all the registered handlers */
    private List<AbstractTagHandler> handlers = new ArrayList<AbstractTagHandler>();


/* ********************************* Register handlers method *********************************** */

    /**
     * This method is called from the Cargo method register(Class<AbstractTagHandler> tagHandler)
     * It initializes each handler with its own initialize() method
     * If the initialization is being done without error, stores the handler in the List<AbstractTagHandler>
     *
     * @param tagHandler The handler being registered
     */
    public void registerHandler(AbstractTagHandler tagHandler) {
        tagHandler.initialize();
        if(tagHandler.valid) {
            handlers.add(tagHandler);
        }
    }


/* ******************************** ActivityLifeCycle callbacks ********************************* */

    /**
     * A callback triggered when an activity starts,
     * which calls the associated method for all the registered handlers.
     *
     * @param activity The activity being actually started
     */
    @Override
    public void onActivityStarted(Activity activity) {
        for(AbstractTagHandler handler : handlers){
            handler.onActivityStarted(activity);
        }
    }

    /**
     * A callback triggered when an activity is resumed,
     * which calls the associated method for all the registered handlers.
     *
     * @param activity The activity being actually resumed
     */
    @Override
    public void onActivityResumed(Activity activity) {
        for(AbstractTagHandler handler : handlers){
            handler.onActivityResumed(activity);
        }
    }

    /**
     * A callback triggered when an activity is paused,
     * which calls the associated method for all the registered handlers.
     *
     * @param activity The activity being actually paused
     */
    @Override
    public void onActivityPaused(Activity activity) {
        for(AbstractTagHandler handler : handlers){
            handler.onActivityPaused(activity);
        }
    }

    /**
     * A callback triggered when an activity is stopped,
     * which calls the associated method for all the registered handlers.
     *
     * @param activity The activity being actually stopped
     */
    @Override
    public void onActivityStopped(Activity activity) {
        for(AbstractTagHandler handler : handlers){
            handler.onActivityStopped(activity);
        }
    }


/* ***************************** Unused ActivityLifeCycle callbacks ***************************** */

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }


/* ********************************************************************************************** */
}
