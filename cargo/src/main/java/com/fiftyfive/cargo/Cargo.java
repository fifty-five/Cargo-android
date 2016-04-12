package com.fiftyfive.cargo;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.tagmanager.Container;

/**
 * Created by louis on 03/11/15.
 */
public class Cargo {

    private static Cargo instance = null;
    private static final String TAG = "Cargo" ;
    private static boolean init = false;

    private Application application;
    private Container container;
    private TagHandlerManager manager;


    public static Cargo getInstance(){
        if(!init){
            Log.w("55", "Cargo instance must be initialized");
        }
        return instance;
    }

    public static void init(Application  application, Container container ){
        if (!init){
            instance = new Cargo();
            instance.setApplication(application);
            instance.setContainer(container);

            instance.initManager();
            init = true;
        }
        else
            Log.i("55", "Cargo has already been initialized");
    }

    private void initManager(){
        manager = new TagHandlerManager();
        application.registerActivityLifecycleCallbacks(manager);
    }

    public void registerHandlers(){
        if(!init){
            Log.w("55", "You should init Cargo before registering handlers");
            return;
        }

        register("com.fiftyfive.cargo.handlers.TuneHandler");
        register("com.fiftyfive.cargo.handlers.GoogleAnalyticsHandler");
        register("com.fiftyfive.cargo.handlers.FacebookHandler");

    }

    private void register(String classPath) {
        try {
            Class tagHandler = Class.forName(classPath);
            register(tagHandler);
        } catch (ClassNotFoundException e) {
            Log.i(TAG, "Handler "+classPath+" is not registered");
            e.printStackTrace();
        }
    }

    private void register(Class<AbstractTagHandler> tagHandler) {
        try {
            AbstractTagHandler instance = tagHandler.newInstance();

            //Store handlers in tagHandlerManager
            manager.registerHandler(instance);

            instance.register(this.getContainer());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }


/* ***************************** Getters - Setters ***************************** */

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application app) {
        this.application = app;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container _container) {
        this.container = _container;
    }

/* ***************************************************************************** */

}
