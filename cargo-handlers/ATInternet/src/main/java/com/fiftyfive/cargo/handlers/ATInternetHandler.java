package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.util.Log;

import com.atinternet.tracker.ATInternet;
import com.atinternet.tracker.Gesture;
import com.atinternet.tracker.Tracker;
import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.models.Event;
import com.fiftyfive.cargo.models.Screen;
import com.fiftyfive.cargo.models.User;
import com.google.android.gms.tagmanager.Container;

import java.util.HashMap;
import java.util.Map;

import static com.fiftyfive.cargo.ModelsUtils.getInt;
import static com.fiftyfive.cargo.ModelsUtils.getString;

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
        this.valid = true;
    }

    /**
     * Register the callbacks to the container. After a dataLayer.push(),
     * these will trigger the execute method of this handler.
     *
     * @param container The instance of the GTM container we register the callbacks to
     */
    @Override
    public void register(Container container) {
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

        switch (s) {
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
                Log.i("55", "Function "+s+" is not registered");
        }
    }



/* ****************************************** Tracking ****************************************** */

    /**
     * Method used to create and fire a screen view to AT Internet
     * The mandatory parameter is SCREEN_NAME
     *
     * @param params    the parameters given at the moment of the dataLayer.push(),
     *                  passed through the GTM container and the execute method.
     *                  * screenName (String) : the name of the screen that has been seen
     *                  * customDim1 (String) : a custom dimension to set some more context
     *                  * customDim2 (String) : a second custom dim to set some more context
     */
    private void tagScreen(Map<String, Object> params){

        String screenName = getString(params, Screen.SCREEN_NAME);

        if(params.containsKey(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM1)
                && params.containsKey(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM2)) {

            final String customDim1 = getString(params, com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM1);
            final String customDim2 = getString(params, com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM2);

            atTracker.CustomObjects().add(new HashMap<String, Object>() {{
                put(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM1, customDim1);
                put(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM2, customDim2);
            }});
        }
        atTracker.Screens()
                .add(screenName)
                .setLevel2(getInt(params, com.fiftyfive.cargo.models.Tracker.LEVEL2, 0))
                .sendView();
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

        if (eventType == null) {
            Log.w("CARGO ATInternetHandler", "in tagEvent() no EVENT_TYPE given, event hasn't been sent");
            return ;
        }

        Gesture gesture = setChapters(eventName, params);
        gesture.setLevel2(getInt(params, com.fiftyfive.cargo.models.Tracker.LEVEL2, 0));

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
                Log.w("CARGO ATInternetHandler", "in tagEvent() wrong EVENT_TYPE given, event hasn't been sent");
                break;
        }
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

        if (android_id == null) {
            Log.w("CARGO ATInternetHandler", " in identify() missing USER_ID (android_id) parameter. USER_ID hasn't been set");
            return ;
        }

        atTracker.setConfig("identifier", android_id, null);
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
        String chapter1 = getString(parameters, "chapter1");
        String chapter2 = getString(parameters, "chapter2");
        String chapter3 = getString(parameters, "chapter3");

        // depending on the parameters, it returns the right object.
        if (chapter1 == null)
            return (atTracker.Gestures().add(eventName));
        else if (chapter2 == null)
            return (atTracker.Gestures().add(eventName, chapter1));
        else if (chapter3 == null)
            return (atTracker.Gestures().add(eventName, chapter1, chapter2));
        else
            return (atTracker.Gestures().add(eventName, chapter1, chapter2, chapter3));
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
