package com.fiftyfive.cargo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : louis
 * Created: 04/11/15
 *
 * The TagHandlerManager class allows each handler to override several methods
 * which are called corresponding to the life cycle of the app.
 */
public class TagHandlerManager implements Application.ActivityLifecycleCallbacks {

    /**
     * A List of the handlers registered in the method registerHandlers() of the Cargo class
     */
    private List<AbstractTagHandler> handlers = new ArrayList<AbstractTagHandler>();


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    /**
     * A callback triggered when an activity starts
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
     * A callback triggered when an activity is resumed
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
     * A callback triggered when an activity is paused
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
     * A callback triggered when an activity is stopped
     *
     * @param activity The activity being actually stopped
     */
    @Override
    public void onActivityStopped(Activity activity) {
        for(AbstractTagHandler handler : handlers){
            handler.onActivityStopped(activity);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


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
}
