package com.fiftyfive.cargo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by louis on 04/11/15.
 */
public class TagHandlerManager implements Application.ActivityLifecycleCallbacks {

    private List<AbstractTagHandler> handlers = new ArrayList<AbstractTagHandler>();


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        for(AbstractTagHandler handler : handlers){
            handler.onActivityStarted(activity);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        for(AbstractTagHandler handler : handlers){
            handler.onActivityResumed(activity);
        }

    }

    @Override
    public void onActivityPaused(Activity activity) {
        for(AbstractTagHandler handler : handlers){
            handler.onActivityPaused(activity);
        }
    }

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


    public void registerHandler(AbstractTagHandler tagHandler) {
        tagHandler.initialize();
        if(tagHandler.valid) {
            handlers.add(tagHandler);
        }
    }
}
