package com.fiftyfive.cargo;

import android.app.Application;
import android.util.Log;
import com.google.android.gms.tagmanager.Container;

/**
 * Created by louis on 03/11/15.
 * Copyright © 2016 fifty-five All rights reserved.
 *
 * The core of Cargo. Initialize the handlers.
 */
public class Cargo {


/* ************************************ Variables declaration *********************************** */

    /** A reference to the instance of Cargo, in order to use it as a kind of singleton */
    private static Cargo instance = null;
    /** The name of the class, used for the logs */
    private static final String TAG = "Cargo" ;
    /** A boolean which defines whether the instance has been correctly initialized */
    private static boolean init = false;

    /** An instance of the application within Cargo is instantiated */
    private Application application;
    /** The container which contains tags, triggers and variables defined in the GTM interface */
    private Container container;
    /** Used to store initialized handlers and call on their activity life cycle callback methods */
    private TagHandlerManager manager;

    private static final String AT_INTERNET = "AT Internet";
    private static final String FACEBOOK = "Facebook";
    private static final String FIREBASE = "Firebase";
    private static final String GOOGLE_ANALYTICS = "Google Analytics";
    private static final String TUNE = "Tune";

/* *************************************** Init methods ***************************************** */

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
     * Initialize the TagHandlerManager and register to the application life cycle callbacks.
     * at any onStart, onStop, onResume, onPause, a callback method will be also called within the handlers
     */
    private void initManager(){
        manager = new TagHandlerManager();
        application.registerActivityLifecycleCallbacks(manager);
    }

    /**
     * Simply return the instance of Cargo if it has been initialized.
     * If not, return null
     *
     * @return  cargo instance or null if cargo hasn't been initialized
     */
    public static Cargo getInstance(){
        if(!init){
            Log.w(TAG, "Cargo instance must be initialized, null will be return");
        }
        return instance;
    }



/* ****************************** Handler registration methods ********************************** */

    /**
     * Instantiate several handlers in the same time.
     * It has to be called once Cargo has been initialized.
     *
     * @param handlers An array of Handler enums corresponding to the handlers to instantiate.
     */
    public void registerHandlers(Handler[] handlers){
        if(!init){
            Log.w(TAG, "You should initialize Cargo before trying to register handlers");
            return;
        }

        for (Handler handler : handlers) {
            registerHandler(handler);
        }
    }

    /**
     * Instantiate the handler from the Handler enum passed as parameter.
     * It has to be called once Cargo has been initialized.
     *
     * @param handler The Handler enum corresponding to the handler to instantiate.
     */
    public void registerHandler(Handler handler) {
        if(!init){
            Log.w(TAG, "You should initialize Cargo before trying to register handlers");
            return;
        }

        switch (handler.toString()) {
            case AT_INTERNET:
                register("com.fiftyfive.cargo.handlers.ATInternetHandler");
                break;
            case FACEBOOK:
                register("com.fiftyfive.cargo.handlers.FacebookHandler");
                break;
            case FIREBASE:
                register("com.fiftyfive.cargo.handlers.FirebaseHandler");
                break;
            case GOOGLE_ANALYTICS:
                register("com.fiftyfive.cargo.handlers.GoogleAnalyticsHandler");
                break;
            case TUNE:
                register("com.fiftyfive.cargo.handlers.TuneHandler");
                break;
            default:
                Log.w(TAG, handler.toString()+" hasn't been recognized as a correct " +
                        "handler and won't be initialized");
        }
    }

    /**
     * Register the class from its classpath
     * Calls on register(which takes tagHandler Class as parameter) if the Class has been found.
     * Throws a ClassNotFoundException if not.
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
     * Instantiates the handler after its class type
     * Calls on TagHandlerManager.registerHandlers() method in order to store all the handlers
     * Calls on the register method of each handler to register the callbacks based on GTM tags
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

    public enum Handler {
        AT(AT_INTERNET),
        FB(FACEBOOK),
        FIR(FIREBASE),
        GA(GOOGLE_ANALYTICS),
        TUN(TUNE);

        private final String stringValue;

        private Handler(String toString) {
            stringValue = toString;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }


/* ************************************** Getters - Setters ************************************* */

    /**
     * Gets the application context Cargo has been instantiated with.
     *
     * @return Application      the application context
     */
    public Application getApplication() {
        return application;
    }

    /**
     * Reset the Application context as long as the parameter isn't a null value
     *
     * @param app   the new Application context you want cargo to be set with
     */
    private void setApplication(Application app) {
        if (app != null)
            this.application = app;
    }

    /**
     * Gets the GTM container Cargo has been instantiated with.
     *
     * @return Container      the GTM container you registered to
     */
    public Container getContainer() {
        return container;
    }

    /**
     * Sets the GTM container to the instance
     *
     * @param _container    the GTM container
     */
    private void setContainer(Container _container) {
        this.container = _container;
    }

/* ********************************************************************************************** */

}
