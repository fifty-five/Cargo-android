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


    /**
     * Simply return the instance of Cargo if it has been initialized.
     * If not, return null
     *
     * @return  cargo instance or null if cargo hasn't been initialized
     */
    public static Cargo getInstance(){
        if(!init){
            Log.w("55", "Cargo instance must be initialized, null will be return");
        }
        return instance;
    }


    /**
     * Initialize and setup cargo
     * Static method, its call should be followed by Cargo.getInstance()
     *
     * @param application   Your application instance
     * @param container     The GTM container
     */
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

    /**
     * init & set the TagHandlerManager and makes it call its methods on the different LifecycleCallbacks
     */
    private void initManager(){
        manager = new TagHandlerManager();
        application.registerActivityLifecycleCallbacks(manager);
    }

    /**
     * register the handlers after the package name of the classes (hardcoded for now)
     * calls on register(String classPath)
     */
    public void registerHandlers(){
        if(!init){
            Log.w("55", "You should init Cargo before registering handlers");
            return;
        }

        register("com.fiftyfive.cargo.handlers.TuneHandler");
        register("com.fiftyfive.cargo.handlers.GoogleAnalyticsHandler");
        register("com.fiftyfive.cargo.handlers.FacebookHandler");
        register("com.fiftyfive.cargo.handlers.FirebaseHandler");

    }


    /**
     * register the classes
     * calls on register(Class<AbstractTagHandler> tagHandler)
     *
     * @param classPath     the class path of the handler you want to register
     */
    private void register(String classPath) {
        try {
            Class tagHandler = Class.forName(classPath);
            register(tagHandler);
        } catch (ClassNotFoundException e) {
            Log.i(TAG, "Handler "+classPath+" is not registered");
            e.printStackTrace();
        }
    }

    /**
     * Instanciate the handler after its class name
     * Store each handler registered on the registerHandlers() method into the TagHandlerManager
     * Calls on the register method of each handler to register each callbacks based on GTM tags
     *
     * Display an exception if a fail occurs
     *
     * @param tagHandler    the class of the handler
     */
    private void register(Class<AbstractTagHandler> tagHandler) {
        try {
            AbstractTagHandler instance = tagHandler.newInstance();

            // Calls the tagHandlerManager.registerHandler() method to store handlers in a list
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
