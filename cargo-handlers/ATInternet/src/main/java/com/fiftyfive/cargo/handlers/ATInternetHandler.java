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

    /** The AT Tracker */
    public Tracker atTracker;
    public Cargo cargo;


    /**
     * Init properly AT SDK
     */
    @Override
    public void initialize() {
        // Retrieve Cargo instance
        cargo = Cargo.getInstance();
        // Instantiate a tracker and memorise its instance. When you call it in the future, you will recover the same instance.
        atTracker = ((ATInternet) cargo.getApplication()).getDefaultTracker();

        this.valid = true;
    }

    /**
     * Register the callbacks to GTM. These will be triggered after a dataLayer.push()
     *
     * @param container The instance of the GTM container we register the callbacks to
     */
    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback("AT_tagScreen", this);
        container.registerFunctionCallTagCallback("AT_tagEvent", this);
        container.registerFunctionCallTagCallback("AT_identify", this);
    }


    /**
     * This one will be called after an event has been pushed to the dataLayer
     *
     * @param s     The method you aime to call (this should be define in GTM interface)
     * @param map   A map key-object used as a way to give parameters to the class method aimed here
     */
    @Override
    public void execute(String s, Map<String, Object> map) {

        switch (s) {
            case "AT_tagScreen":
                tagScreen(map);
                break;
            case "AT_identify":
                identify(map);
                break;
            case "AT_tagEvent":
                tagEvent(map);
            default:
                Log.i("55", "Function "+s+" is not registered");
        }
    }


    /**
     * In order to identify the user as a unique visitor,
     * we use the android ID which is unique for each android device
     * Check http://stackoverflow.com/a/2785493 if you don't know how to retrieve it
     *
     * @param parameters    the parameters given at the moment of the dataLayer.push(),
     *                      passed through the GTM container and the execute method.
     *                      The only parameter requested here is the android_id
     */
    private void identify(Map<String, Object> parameters){

        final String android_id = getString(parameters, User.USER_ID);

        if (android_id == null) {
            Log.w("CARGO ATInternetHandler", " in identify() missing USER_ID (android_id) parameter. USER_ID hasn't been set");
            return ;
        }

        atTracker.setConfig("identifier", android_id, null);
    }


    /**
     * Send a tag for a screen.
     *
     * @param parameters    the parameters given at the moment of the dataLayer.push(),
     *                      passed through the GTM container and the execute method.
     *
     *                      (String)Screen.SCREEN_NAME to identify the screen
     *
     *                      (String)Tracker.CUSTOM_DIM1 & Tracker.CUSTOM_DIM2 to send more information
     *                          more explanation here : http://developers.atinternet-solutions.com/android-en/content-android-en/custom-object-android-en/
     *
     *                      (String)Chapter1-3 to add some hierarchy information
     *
     *                      (a number) : AT Internet’s SDK offers you the possibility to “separate”
     *                          your application into different sections (called “level 2s”).
     *                          These level 2s allow you to better target certain parts of your app.
     */
    private void tagScreen(Map<String, Object> parameters){

        //retrieve the screen name with a log if none is given
        String screenName = getString(parameters, Screen.SCREEN_NAME);
        if (screenName == null)
            Log.d("CARGO ATInternetHandler", "in tagScreen() no SCREEN_NAME given");

        //set up the CUSTOM_DIM if they exist
        if(parameters.containsKey(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM1)
                && parameters.containsKey(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM2)) {

            final String customDim1 = getString(parameters, com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM1);
            final String customDim2 = getString(parameters, com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM2);

            atTracker.CustomObjects().add(new HashMap<String, Object>() {{
                put(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM1, customDim1);
                put(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM2, customDim2);
            }});
        }
        //set the screen name and the chapters if needed, returns a screen object
        com.atinternet.tracker.Screen screen = setScreenChapters(screenName, parameters);
        //set the level2 and send the tag
        screen.setLevel2(getInt(parameters, com.fiftyfive.cargo.models.Tracker.LEVEL2, 0))
                .sendView();
    }

    /**
     * Send a tag for a custom event.
     *
     * @param parameters    the parameters given at the moment of the dataLayer.push(),
     *                      passed through the GTM container and the execute method.
     *
     *                      (String)Event.EVENT_NAME to identify the event
     *
     *                      (String)Event.EVENT_TYPE to indicate the type of the event you want to send
     *                          -> touch/navigation/download/search/exit
     *
     *                      (String)Chapter1-3 to add some hierarchy information
     *
     *                      (a number) : AT Internet’s SDK offers you the possibility to “separate”
     *                          your application into different sections (called “level 2s”).
     *                          These level 2s allow you to better target certain parts of your app.
     *
     */
    private void tagEvent(Map<String, Object> parameters){

        String eventName = getString(parameters, Event.EVENT_NAME);
        String eventType = getString(parameters, Event.EVENT_TYPE);

        if (eventType == null) {
            Log.w("CARGO ATInternetHandler", "in tagEvent() no EVENT_TYPE given, event hasn't been sent");
            return ;
        }

        //set the event name and the chapters if needed, returns a screen object
        Gesture gesture = setEventChapters(eventName, parameters);
        //set the level2
        gesture.setLevel2(getInt(parameters, com.fiftyfive.cargo.models.Tracker.LEVEL2, 0));

        //send the tag as an event of the type specified in the parameter Event.EVENT_TYPE
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
     * Internal method used to set the chapters to a screen event
     *
     * @param screenName    The name of the screen
     * @param parameters    The chapters you want to set
     * @return              The screen object created in the SDK
     */
    private com.atinternet.tracker.Screen setScreenChapters(String screenName, Map<String, Object> parameters){
        // retrieve the chapters
        String chapter1 = getString(parameters, "Chapter1");
        String chapter2 = getString(parameters, "Chapter2");
        String chapter3 = getString(parameters, "Chapter3");

        // set up the chapters in a Screen object, according if they have been set or not.
        // No chapter2 will be set without a Chapter1
        if (chapter1 == null)
            return (atTracker.Screens().add(screenName));
        else if (chapter2 == null)
            return (atTracker.Screens().add(screenName, chapter1));
        else if (chapter3 == null)
            return (atTracker.Screens().add(screenName, chapter1, chapter2));
        else
            return (atTracker.Screens().add(screenName, chapter1, chapter2, chapter3));
    }

    /**
     * Internal method used to set the chapters to an event
     *
     * @param eventName    The name of the event
     * @param parameters    The chapters you want to set
     * @return              The event object created in the SDK
     */
    private Gesture setEventChapters(String eventName, Map<String, Object> parameters){
        String chapter1 = getString(parameters, "Chapter1");
        String chapter2 = getString(parameters, "Chapter2");
        String chapter3 = getString(parameters, "Chapter3");

        // set up the chapters in a Gesture object, according if they have been set or not.
        // No chapter2 will be set without a Chapter1
        if (chapter1 == null)
            return (atTracker.Gestures().add(eventName));
        else if (chapter2 == null)
            return (atTracker.Gestures().add(eventName, chapter1));
        else if (chapter3 == null)
            return (atTracker.Gestures().add(eventName, chapter1, chapter2));
        else
            return (atTracker.Gestures().add(eventName, chapter1, chapter2, chapter3));
    }


    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

}
