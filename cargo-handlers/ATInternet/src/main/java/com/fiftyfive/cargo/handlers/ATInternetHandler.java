package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.util.Log;

import com.atinternet.tracker.ATInternet;
import com.atinternet.tracker.Gesture;
import com.atinternet.tracker.SetConfigCallback;
import com.atinternet.tracker.Tracker;
import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.models.Event;
import com.fiftyfive.cargo.models.Screen;
import com.fiftyfive.cargo.models.User;
import com.google.android.gms.tagmanager.Container;

import java.util.HashMap;
import java.util.Map;

import static com.fiftyfive.cargo.ModelsUtils.getBoolean;
import static com.fiftyfive.cargo.ModelsUtils.getInt;
import static com.fiftyfive.cargo.ModelsUtils.getString;
import static com.fiftyfive.cargo.models.Tracker.CHAPTER1;
import static com.fiftyfive.cargo.models.Tracker.CHAPTER2;
import static com.fiftyfive.cargo.models.Tracker.CHAPTER3;
import static com.fiftyfive.cargo.models.Tracker.LEVEL2;

/**
 * Created by dali on 04/12/15.
 *
 * The class which handles interactions with the AT Internet SDK
 */
public class ATInternetHandler extends AbstractTagHandler {

/* ************************************ Variables declaration *********************************** */

    /** The tracker of the AT Internet SDK which send the events */
    public Tracker atTracker;

    /** Constants used to define callbacks in the register and in the execute method */
    private final String AT_INIT = "AT_init";
    private final String AT_SET_CONFIG = "AT_setConfig";
    private final String AT_TAG_SCREEN = "AT_tagScreen";
    private final String AT_TAG_EVENT = "AT_tagEvent";
    private final String AT_IDENTIFY = "AT_identify";



/* ************************************ Handler core methods ************************************ */

    /**
     * Called by the TagHandlerManager, initialize the core of the handler
     */
    @Override
    public void initialize() {
        super.initialize();
        atTracker = ((ATInternet) cargo.getApplication()).getDefaultTracker();
        this.name = "AT Internet";
    }

    /**
     * Register the callbacks to the container. After a dataLayer.push(),
     * these will trigger the execute method of this handler.
     *
     * @param container The instance of the GTM container we register the callbacks to
     */
    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback(AT_INIT, this);
        container.registerFunctionCallTagCallback(AT_SET_CONFIG, this);
        container.registerFunctionCallTagCallback(AT_TAG_SCREEN, this);
        container.registerFunctionCallTagCallback(AT_TAG_EVENT, this);
        container.registerFunctionCallTagCallback(AT_IDENTIFY, this);
    }

    /**
     * A callback method for the registered callbacks method name mentionned in the register method.
     *
     * @param s     The method name called through the container (defined in the GTM interface)
     * @param map   A map key-object used as a way to give parameters to the class method aimed here
     */
    @Override
    public void execute(String s, Map<String, Object> map) {
        logReceivedFunction(s, map);

        if (AT_INIT.equals(s))
            init(map);
        else if (this.initialized) {
            switch (s) {
                case AT_SET_CONFIG:
                    setConfig(map);
                    break;
                case AT_TAG_SCREEN:
                    tagScreen(map);
                    break;
                case AT_TAG_EVENT:
                    tagEvent(map);
                    break;
                case AT_IDENTIFY:
                    identify(map);
                    break;
                default:
                    logUnknownFunction(s);
            }
        }
        else
            logUninitializedFramework();
    }



/* ************************************* SDK initialization ************************************* */

    /**
     * The method you have to call first, because it initializes
     * the AT Internet tracker with the parameters you give.
     *
     * @param params
     *      - log : the log you want to use
     *      - logSSL : the secured log
     *      - site : id you got when you register your app,
     *                 used to report hits to your AT interface
     */
    private void init(Map<String, Object> params){
        final String SITE = "site";
        final String LOG = "log";
        final String LOG_SSL = "logSSL";

        final String siteId = getString(params, SITE);
        final String log = getString(params, LOG);
        final String logSSL = getString(params, LOG_SSL);

        if (siteId != null && log != null && logSSL != null) {
            HashMap config = new HashMap<>();
            config.put(SITE, siteId);
            config.put(LOG, log);
            config.put(LOG_SSL, logSSL);

            atTracker.setConfig(config, false, new SetConfigCallback() {
                @Override
                public void setConfigEnd() {
                    logParamWithSuccess(SITE, siteId);
                    logParamWithSuccess(LOG, log);
                    logParamWithSuccess(LOG_SSL, logSSL);
                }
            });
            this.initialized = true;
        }
        else
            logMissingParam(new String[]{SITE,LOG, LOG_SSL}, AT_INIT);
    }

    /**
     * The method you may call if you want to reconfigure your tracker configuration
     *
     * @param params
     *      - override (boolean) : if you want your values set to override ALL the existant data
     *                             (set to false by default)
     *      - Dictionary (Map of objects) : your setup for the tracker http://tinyurl.com/j3avazw
     */
    private void setConfig(Map<String, Object> params){
        final String OVERRIDE = "override";
        Boolean override = getBoolean(params, OVERRIDE, false);
        params.remove(OVERRIDE);
        logParamWithSuccess(OVERRIDE, override);

        HashMap<String, Object> map = new HashMap<>(params);
        atTracker.setConfig(map, override, new SetConfigCallback() {
            @Override
            public void setConfigEnd() {
                Log.v(name, "New configuration has been set");
            }
        });
    }

/* ****************************************** Tracking ****************************************** */

    /**
     * Method used to create and fire a screen view to AT Internet
     * The mandatory parameter is SCREEN_NAME
     *
     * @param params    the parameters given at the moment of the dataLayer.push(),
     *                  passed through the GTM container and the execute method.
     *                  * screenName (String) : the name of the screen that has been seen
     *                  * chapter1/2/3 (String) : used to add more context to the event
     *                  * level2 (int) : to add a second level to the event
     *                  * isBasketView (bool): set to true if the screen is a basket screen
     */
    private void tagScreen(Map<String, Object> params){

        final String screenName = getString(params, Screen.SCREEN_NAME);

        if (screenName != null){
            com.atinternet.tracker.Screen atScreen = atTracker.Screens().add(screenName);
            logParamWithSuccess(Screen.SCREEN_NAME, screenName);

            atScreen = setAdditionalScreenProperties(atScreen, params);
            atScreen.sendView();
        }
        else
            logMissingParam(new String[]{Screen.SCREEN_NAME}, AT_TAG_SCREEN);
    }

    /**
     * Method used to create and fire an event to the AT Internet interface
     * The mandatory parameters are EVENT_NAME, EVENT_TYPE which are a necessity to build the event.
     * Without these parameters, the event won't be built.
     *
     * @param params    the parameters given at the moment of the dataLayer.push(),
     *                  passed through the GTM container and the execute method.
     *                  * eventName (String) : the name for this event.
     *                  * eventType (String) : defines the type of event you want to send.
     *                    the different values can be :     - sendTouch
     *                                                      - sendNavigation
     *                                                      - sendDownload
     *                                                      - sendExit
     *                                                      - sendSearch
     *                  * chapter1/2/3 (String) : used to add more context to the event
     *                  * level2 (int) : to add a second level to the event
     */
    private void tagEvent(Map<String, Object> params){
        String eventName = getString(params, Event.EVENT_NAME);
        String eventType = getString(params, Event.EVENT_TYPE);

        if (eventName != null && eventType != null) {
            Gesture gesture = setChapters(eventName, params);

            if (params.containsKey(LEVEL2)){
                int    level2 = getInt(params, LEVEL2, -1);
                gesture.setLevel2(level2);
                logParamWithSuccess(LEVEL2, Integer.toString(level2));
            }

            switch (eventType) {
                case "sendTouch":
                    gesture.sendTouch();
                    break;
                case "sendNavigation":
                    gesture.sendNavigation();
                    break;
                case "sendDownload":
                    gesture.sendDownload();
                    break;
                case "sendExit":
                    gesture.sendExit();
                    break;
                case "sendSearch":
                    gesture.sendSearch();
                    break;
                default:
                    String[] values = new String[]{"sendTouch", "sendNavigation", "sendDownload",
                    "sendExit", "sendSearch"};
                    logNotFoundValue(eventType, Event.EVENT_TYPE, values);
            }
        }
        else
            logMissingParam(new String[]{Event.EVENT_NAME, Event.EVENT_TYPE}, AT_TAG_EVENT);
    }

    /**
     * A way to identify the user. Use a unique identifier like the Ad Id.
     *
     * @param params    the parameters given at the moment of the dataLayer.push(),
     *                  passed through the GTM container and the execute method.
     *                  * userId (String) : the identifier.
     */
    private void identify(Map<String, Object> params){

        final String android_id = getString(params, User.USER_ID);

        if (android_id != null) {
            atTracker.setConfig("identifier", android_id, new SetConfigCallback() {
                @Override
                public void setConfigEnd() {
                    logParamWithSuccess(User.USER_ID, android_id);
                }
            });
        }
    }



/* ****************************************** Utility ******************************************* */

    /**
     * Internal calls only. This method is used to return the right object when building an event.
     * Depending on what the parameters map contains, it will set the EVENT_NAME and chapters and
     * return the built object to the tagEvent method.
     *
     * @param eventName     the name of the event
     * @param parameters    the map of parameters which can contain up to 3 chapters.
     *                      The chapter1 has to be set if you want to set chapter2, etc...
     *                      * chapter1 (String) : first level of context
     *                      * chapter2 (String) : second level of context
     *                      * chapter3 (String) : third level of context
     *
     * @return              the gesture object, which can be compared to an event object.
     */
    private Gesture setChapters(String eventName, Map<String, Object> parameters){
        String chapter1 = getString(parameters, CHAPTER1);
        String chapter2 = getString(parameters, CHAPTER2);
        String chapter3 = getString(parameters, CHAPTER3);

        // depending on the parameters, it returns the right object.
        if (chapter1 == null)
            return (atTracker.Gestures().add(eventName));
        else if (chapter2 == null){
            logParamWithSuccess(CHAPTER1, chapter1);
            return (atTracker.Gestures().add(eventName, chapter1));
        }
        else if (chapter3 == null){
            logParamWithSuccess(CHAPTER1, chapter1);
            logParamWithSuccess(CHAPTER2, chapter2);
            return (atTracker.Gestures().add(eventName, chapter1, chapter2));
        }
        else {
            logParamWithSuccess(CHAPTER1, chapter1);
            logParamWithSuccess(CHAPTER2, chapter2);
            logParamWithSuccess(CHAPTER3, chapter3);
            return (atTracker.Gestures().add(eventName, chapter1, chapter2, chapter3));
        }
    }

    /**
     * Internal calls only. This method is used to return the right object when building a screen.
     * Depending on what the parameters map contains, it will set the parameters to the
     * screen object and return it to the tagScreen method.
     *
     * @param atScreen the screen object already build with eventName in the tagScreen method
     * @param parameters the map of parameters which can contain up to 3 chapters.
     *                      The chapter1 has to be set if you want to set chapter2, etc...
     *                      * chapter1 (String) : first level of context
     *                      * chapter2 (String) : second level of context
     *                      * chapter3 (String) : third level of context
     *                      * level2 (int) : to add more context
     *                      * isBasketView (bool) : true if the screen is a basket view
     *
     * @return              the built screen object
     */
    private com.atinternet.tracker.Screen
    setAdditionalScreenProperties(com.atinternet.tracker.Screen atScreen,
                                  Map<String, Object> parameters){
        final String BASKET_VIEW = "isBasketView";
        String chapter1 = getString(parameters, CHAPTER1);
        String chapter2 = getString(parameters, CHAPTER2);
        String chapter3 = getString(parameters, CHAPTER3);

        if (chapter1 != null) {
            atScreen.setChapter1(chapter1);
            logParamWithSuccess(CHAPTER1, chapter1);
            if (chapter2 != null){
                atScreen.setChapter2(chapter2);
                logParamWithSuccess(CHAPTER2, chapter2);
                if (chapter3 != null) {
                    atScreen.setChapter3(chapter3);
                    logParamWithSuccess(CHAPTER3, chapter3);
                }
            }
        }

        if (parameters.containsKey(LEVEL2)) {
            int    level2 = getInt(parameters, LEVEL2, -1);
            atScreen.setLevel2(level2);
            logParamWithSuccess(LEVEL2, Integer.toString(level2));
        }

        if (parameters.containsKey(BASKET_VIEW)){
            boolean basket = getBoolean(parameters, BASKET_VIEW, false);
            atScreen.setIsBasketScreen(basket);
            logParamWithSuccess(BASKET_VIEW, basket);
        }

        return atScreen;
    }

    /**
     * A callback triggered when an activity starts
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityStarted(Activity activity) {

    }

    /**
     * A callback triggered when an activity is resumed
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityResumed(Activity activity) {

    }

    /**
     * A callback triggered when an activity is paused
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityPaused(Activity activity) {

    }

    /**
     * A callback triggered when an activity stops
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityStopped(Activity activity) {

    }

/* ********************************************************************************************** */

}
