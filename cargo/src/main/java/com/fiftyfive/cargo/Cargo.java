package com.fiftyfive.cargo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.Map;

import static com.fiftyfive.cargo.ModelsUtils.*;

/**
 * Created by louis on 03/11/15.
 * Copyright 2016 fifty-five All rights reserved.
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

    /** An instance of the appContext within Cargo is instantiated */
    private Context appContext;
    /** Used to store initialized handlers and call on their activity life cycle callback methods */
    private TagHandlerManager manager;

    private static final String HANDLER_METHOD = "handlerMethod";

    private static final String AT_INTERNET = "AT Internet";
    private static final String FACEBOOK = "Facebook";
    private static final String TUNE = "Tune";

/* *************************************** Init methods ***************************************** */

    /**
     * Initialize and setup cargo
     * Static method, its call should be followed by Cargo.getInstance()
     *
     * @param application   Your appContext instance
     */
    public static void init(Application  application){
        if (!init){
            instance = new Cargo();
            instance.setAppContext(application.getApplicationContext());
            instance.initManager(application);
            init = true;
        }
        else
            Log.i("55", "Cargo has already been initialized");
    }

    /**
     * Initialize the TagHandlerManager and register to the appContext life cycle callbacks.
     * at any onStart, onStop, onResume, onPause, a callback method will be also called within the handlers
     *
     * @param application   the application instance
     */
    private void initManager(Application application){
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
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * The method handling callbacks from GTM container when a tag is triggered.
     * Actually, this one is called from the class Tags, which redirects the callback here.
     * The execute() method will dispatch the map of parameters to the corresponding handler method.
     *
     * @param map the map obtained from the container, containing the following :
     *            * handlerKey (String) : the key of the handler
     *                                  (the same as the one used in the registerHandler() method)
     *            * handlerMethod (String) : the method aimed by this call. e.g : 'FB_init'
     *            * parameters : other key-value pairs are used as parameters for the method.
     */
    void execute(Map<String, Object> map) {
        if(!init){
            Log.w(TAG, "You should initialize Cargo before trying to call its methods");
            return;
        }
        String handlerMethod = getString(map, HANDLER_METHOD);
        map.remove(HANDLER_METHOD);

        if (handlerMethod != null) {
            String handlerKey = handlerMethod.split("\\_")[0];

            if (handlerKey != null && handlerKey.length() > 1 && handlerKey.length() < 4) {
                Log.d(TAG, "Received '"+ handlerMethod + "' method name " +
                        "for the handler matching the '"+ handlerKey +"' key.");
                List<AbstractTagHandler> handlers = manager.getHandlers();
                for (AbstractTagHandler handler : handlers) {
                    if (handlerKey.toUpperCase().equals(handler.key.toUpperCase())) {
                        handler.execute(handlerMethod, map);
                        return ;
                    }
                }
                Log.w(TAG, "Unable to find a handler matching the key '" + handlerKey + "'.");
            }
            else {
                Log.w(TAG, "Something went wrong while analyzing '"+HANDLER_METHOD+"' format, " +
                        "check it again please.");
            }
        }
        else {
            Log.w(TAG, "Parameter '"+ HANDLER_METHOD +"' is " +
                    "required in method cargo.execute(Map<String, Object> map)");
        }
    }

    /**
     * An enum which allows to initialize the desired handlers without any risk of mistake.
     */
    public enum Handler {
        /** The enum constant for the AT Internet handler */
        AT(AT_INTERNET),
        /** The enum constant for the Facebook handler */
        FB(FACEBOOK),
        /** The enum constant for the Tune handler */
        TUN(TUNE);

        private final String stringValue;

        /**
         * Sets the String value of the handler.
         *
         * @param toString the string value associated to the handler.
         */
        private Handler(String toString) {
            stringValue = toString;
        }

        /**
         * Returns the string value of the Handler constant.
         *
         * @return the string value for the constant it is called from.
         */
        @Override
        public String toString() {
            return stringValue;
        }
    }


/* ************************************** Getters - Setters ************************************* */

    /**
     * Gets the appContext context Cargo has been instantiated with.
     *
     * @return appContext  the appContext context
     */
    public Context getAppContext() {
        return appContext;
    }

    /**
     * Reset the Application context as long as the parameter isn't a null value
     *
     * @param appContext   the new Application context you want cargo to be set with
     */
    private void setAppContext(Context appContext) {
        if (appContext != null)
            this.appContext = appContext;
    }

/* ********************************************************************************************** */

}
