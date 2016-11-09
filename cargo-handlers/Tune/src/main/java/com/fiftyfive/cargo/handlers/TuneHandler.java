package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.util.Log;

import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.models.Event;
import com.fiftyfive.cargo.models.Screen;
import com.fiftyfive.cargo.models.User;
import com.google.android.gms.tagmanager.Container;
import com.tune.Tune;
import com.tune.TuneEvent;
import com.tune.TuneGender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

import static com.fiftyfive.cargo.ModelsUtils.getDate;
import static com.fiftyfive.cargo.ModelsUtils.getDouble;
import static com.fiftyfive.cargo.ModelsUtils.getInt;
import static com.fiftyfive.cargo.ModelsUtils.getList;
import static com.fiftyfive.cargo.ModelsUtils.getString;


/**
 * Author : Julien Gil
 * Created: 03/11/15
 *
 *  * The class which handles interactions with the Tune SDK
 */
public class TuneHandler extends AbstractTagHandler {

    protected Tune tune;
    private boolean init = false;

    public Cargo cargo = Cargo.getInstance();

    final String Tune_init = "Tune_init";
    final String Tune_identify = "Tune_identify";
    final String Tune_tagEvent = "Tune_tagEvent";
    final String Tune_tagScreen = "Tune_tagScreen";

    final String ADVERTISER_ID = "advertiserId";
    final String CONVERSION_KEY = "conversionKey";


    // all the parameters that could be set as attributes to a TuneEvent object
    final String EVENT_RATING = "eventRating";
    final String EVENT_DATE1 = "eventDate1";
    final String EVENT_DATE2 = "eventDate2";
    final String EVENT_REVENUE = "eventRevenue";
    final String EVENT_ITEMS = "eventItems";
    final String EVENT_LEVEL = "eventLevel";
    final String EVENT_RECEIPT_DATA = "eventReceiptData";
    final String EVENT_RECEIPT_SIGNATURE = "eventReceiptSignature";
    final String EVENT_QUANTITY = "eventQuantity";

    // the formatted name "eventRandomAttribute" is important here as the string is used in the
    // eventBuilder method to call on TuneEvent methods.
    final String[] EVENT_PROPERTIES = {
            "eventCurrencyCode",
            "eventAdvertiserRefId",
            "eventContentId",
            "eventContentType",
            "eventSearchString",
            "eventAttribute1",
            "eventAttribute2",
            "eventAttribute3",
            "eventAttribute4",
            "eventAttribute5"
    };


    /**
     * Init properly the SDK, not needed here
     */
    @Override
    public void initialize() {
        this.valid = true;
    }

    /**
     * Register the callbacks to GTM. These will be triggered after a dataLayer.push()
     *
     * @param container The instance of the GTM container we register the callbacks to
     */
    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback(Tune_init, this);
        container.registerFunctionCallTagCallback(Tune_identify, this);
        container.registerFunctionCallTagCallback(Tune_tagEvent, this);
        container.registerFunctionCallTagCallback(Tune_tagScreen, this);

    }

    /**
     * This one will be called after an event has been pushed to the dataLayer
     *
     * @param s     The method you aim to call (this should be define in GTM interface)
     * @param map   A map key-object used as a way to give parameters to the class method aimed here
     */
    @Override
    public void execute(String s, Map<String, Object> map) {

        switch (s) {
            case Tune_init:
                init(map);
                break;
            case Tune_identify:
                identify(map);
                break;
            case Tune_tagEvent:
                tagEvent(map);
                break;
            case Tune_tagScreen:
                tagScreen(map);
                break;
            default:
                Log.i("Cargo TuneHandler", "Function "+s+" is not registered");
        }
    }

    /**
     * The method you need to call first. Register your tune app id to the tune SDK
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method
     *              * advertiserId & conversionKey : ids Tune gives you when you register your app
     */
    private void init(Map<String, Object> map) {
        if (init)
            Log.i("Cargo TuneHandler", "Tune has already been initialized");
        else if (map.containsKey(ADVERTISER_ID) && map.containsKey(CONVERSION_KEY)) {
            if (getString(map, ADVERTISER_ID) != null && getString(map, CONVERSION_KEY) != null) {
                // set the required parameters
                Tune.init(cargo.getApplication(),
                        map.remove(ADVERTISER_ID).toString(),
                        map.remove(CONVERSION_KEY).toString());

                // retrieve the Tune instance
                tune = Tune.getInstance();
                init = true;
            }
            else
                Log.w("Cargo TuneHandler", "At least one required parameter is set to null" +
                        " in init Tune. Tune hasn't been init.");
        }
        else
            Log.w("Cargo TuneHandler", "Missing a required parameter to init Tune");
    }

    /**
     * In order to identify the user as a unique visitor and to associate to a unique id
     * the related social networks ids, age, mail, username, gender...
     *
     * @param map    the parameters given at the moment of the dataLayer.push(),
     *               passed through the GTM container and the execute method.
     *               The only parameter requested here is the android_id (User.USER_ID)
     */
    private void identify(Map<String, Object> map) {

        // set the android id given through the User.USER_ID parameter in Tune
        if (map.containsKey(User.USER_ID))
            tune.setUserId(getString(map, User.USER_ID));

        // set the GOOGLE_ID, FACEBOOK_ID, TWITTER_ID, USERNAME, EMAIL, AGE and GENDER if they exist
        if (map.containsKey(User.USER_GOOGLE_ID))
            tune.setGoogleUserId(getString(map, User.USER_GOOGLE_ID));
        if (map.containsKey(User.USER_FACEBOOK_ID))
            tune.setFacebookUserId(getString(map, User.USER_FACEBOOK_ID));
        if (map.containsKey(User.USER_TWITTER_ID))
            tune.setTwitterUserId(getString(map, User.USER_TWITTER_ID));
        if (map.containsKey(User.USER_AGE))
            tune.setAge(getInt(map, User.USER_AGE, -1));
        if (map.containsKey(User.USERNAME))
            tune.setUserName(getString(map, User.USERNAME));
        if (map.containsKey(User.USER_EMAIL))
            tune.setUserEmail(getString(map, User.USER_EMAIL));
        if (map.containsKey(User.USER_GENDER))
            setGender(getString(map, User.USER_GENDER));
    }

    /**
     * Method used to create and fire an event to the Tune Console
     * The mandatory parameters are EVENT_NAME or EVENT_ID which are a necessity to build the event
     * Without this parameter, the event won't be built.
     * After the creation of the event object, some attributes can be added through the eventBuilder
     * method, using the map obtained from the gtm container.
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method.
     *              The only parameter requested here is a name or an id for the event
     *              (Event.EVENT_NAME or Event.EVENT_ID)
     */
    private void tagEvent(Map<String, Object> map) {

        TuneEvent tuneEvent;

        if (map.containsKey(Event.EVENT_NAME)) {
            tuneEvent = new TuneEvent(getString(map, Event.EVENT_NAME));
            map.remove(Event.EVENT_NAME);
        }
        else if (map.containsKey(Event.EVENT_ID)) {
            tuneEvent = new TuneEvent(getInt(map, Event.EVENT_ID, -1));
            map.remove(Event.EVENT_ID);
        }
        else {
            Log.w("Cargo TuneHandler", " in order to create an event, " +
                    "an eventName or eventId is mandatory. The event hasn't been created.");
            return ;
        }

        if (map.size() > 0)
            tuneEvent = eventBuilder(map, tuneEvent);

        // if the returned event is not null, the event is fired.
        if (tuneEvent != null)
            tune.measureEvent(tuneEvent);
    }

    /**
     * Method used to create and fire a screen view to the Tune Console
     * The mandatory parameters is SCREEN_NAME which is a necessity to build the tagScreen.
     * Actually, as no native tagScreen is given in the Tune SDK, we fire a custom event.
     *
     * After the creation of the event object, some attributes can be added through the eventBuilder
     * method, using the map obtained from the gtm container.
     * We recommend to use Attribute1/2 if you need more information about the screen.
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method.
     *              The only parameter requested here is a name for the screen
     *              (Screen.SCREEN_NAME)
     */
    private void tagScreen(Map<String, Object> map) {
        TuneEvent tuneEvent;

        if (map.containsKey(Screen.SCREEN_NAME)) {
            tuneEvent = new TuneEvent(getString(map, Screen.SCREEN_NAME));
            map.remove(Screen.SCREEN_NAME);
        }
        else {
            Log.w("Cargo TuneHandler", " in order to tag a screen, " +
                    "an screenName is mandatory. The event hasn't been created.");
            return ;
        }

        if (map.size() > 0)
            tuneEvent = eventBuilder(map, tuneEvent);

        // if the returned event is not null, the event is fired.
        if (tuneEvent != null)
            tune.measureEvent(tuneEvent);
    }

    /**
     * A simple method called by identify() to set the gender in a secured way
     *
     * @param val   The gender given in the identify method.
     *              If the gender doesn't match with the Tune genders,
     *              sets the gender to UNKNOWN.
     */
    private void setGender(String val) {
        String gender = val.toUpperCase(Locale.ENGLISH);
        if (gender.equals("MALE") || gender.equals("FEMALE") || gender.equals("UNKNOWN"))
            tune.setGender(TuneGender.forValue(val));
        else {
            tune.setGender(TuneGender.UNKNOWN);
            Log.w("Cargo TuneHandler", "in identify, waiting for MALE/FEMALE/UNKNOWN," +
                    " gender has been set to UNKNOWN");
        }
    }

    /**
     * The method used to add attributes to the event object given as a parameter. The map contains
     * the key of the attributes to attach to this event. For the name of the key you have to give,
     * please have a look at all the EVENT_... constants on the top of this file. The String array
     * contains all the parameters requested as String from Tune SDK, reflection is used to call the
     * corresponding instance methods.
     *
     * @param map           the key/value list of the attributes you want to attach to your event
     * @param tuneEvent     the event you want to custom
     * @return              the custom event
     */
    private TuneEvent eventBuilder(Map<String, Object> map, TuneEvent tuneEvent) {

        if (tuneEvent == null) {
            Log.w("Cargo TuneHandler", "trying to set properties on a nil TuneEvent. " +
                    "Operation has been cancelled");
            return null ;
        }

        // for all the different parameters that could be add, we check if they exist,
        // and call on the appropriate TuneEvent method to set it.
        if (map.containsKey(EVENT_RATING)) {
            tuneEvent.withRating(getDouble(map, EVENT_RATING, -1));
            map.remove(EVENT_RATING);
        }
        if (map.containsKey(EVENT_DATE1)) {
            tuneEvent.withDate1(getDate(map, EVENT_DATE1));
            map.remove(EVENT_DATE1);

            if (map.containsKey(EVENT_DATE2)) {
                tuneEvent.withDate2(getDate(map, EVENT_DATE2));
                map.remove(EVENT_DATE2);
            }
        }
        if (map.containsKey(EVENT_REVENUE)) {
            tuneEvent.withRevenue(getDouble(map, EVENT_REVENUE, -1));
            map.remove(EVENT_REVENUE);
        }
        if (map.containsKey(EVENT_ITEMS)) {
            tuneEvent.withEventItems(getList(map, EVENT_ITEMS));
            map.remove(EVENT_ITEMS);
        }
        if (map.containsKey(EVENT_LEVEL)) {
            tuneEvent.withLevel(getInt(map, EVENT_LEVEL, -1));
            map.remove(EVENT_LEVEL);
        }
        if (map.containsKey(EVENT_RECEIPT_DATA)) {
            tuneEvent.withReceipt(getString(map, EVENT_RECEIPT_DATA),
                    getString(map, EVENT_RECEIPT_SIGNATURE));
            map.remove(EVENT_RECEIPT_DATA);
            map.remove(EVENT_RECEIPT_SIGNATURE);
        }
        if (map.containsKey(EVENT_QUANTITY)) {
            tuneEvent.withQuantity(getInt(map, EVENT_QUANTITY, -1));
            map.remove(EVENT_QUANTITY);
        }

        // for all the String format parameters that could be given, we check if they are set, and
        // we call on the corresponding TuneEvent method through reflection.
        for (String property : EVENT_PROPERTIES) {
            if (map.containsKey(property)) {
                String mName = "with"+property.substring(5);
                try {
                    Method method = tuneEvent.getClass().getMethod(mName, String.class);
                    method.invoke(tuneEvent, map.remove(property));
                } catch (Exception e) {
                    Log.e("Cargo TuneHandler", "exception", e);
                    e.printStackTrace();
                }
            }
        }
        for (Map.Entry<String, Object> entry : map.entrySet())
        {
            Log.w("Cargo TuneHandler", " the event builder couldn't find any match with the parameter key ["+entry.getKey()+"] with value ["+entry.getValue().toString()+"]");
        }
        return tuneEvent;
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
     * Used to get referring package name and url scheme from and for the session measurement
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityResumed(Activity activity) {
        if (init) {
            tune.setReferralSources(activity);
            tune.measureSession();
        }
    }

    /**
     * A callback triggered when an activity is paused
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityPaused(Activity activity) {

    }

    /**
     * The getter for the init boolean, returning if the tagHandler has been initialized
     *
     * @return the boolean
     */
    public boolean isInitialized(){
        return init;
    }

    /**
     * A callback triggered when an activity stops
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityStopped(Activity activity) {

    }




}
