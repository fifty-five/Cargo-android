package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.util.Log;

import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.CargoItem;
import com.fiftyfive.cargo.models.Event;
import com.fiftyfive.cargo.models.Item;
import com.fiftyfive.cargo.models.User;
import com.tune.Tune;
import com.tune.TuneEvent;
import com.tune.TuneEventItem;
import com.tune.TuneGender;
import com.tune.ma.application.TuneActivity;
import com.tune.ma.application.TuneActivityLifecycleCallbacks;
import com.tune.ma.application.TuneApplication;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.fiftyfive.cargo.ModelsUtils.*;

/**
 * Created by Julien Gil on 03/04/16.
 * Copyright 2016 fifty-five All rights reserved.
 *
 * The class which handles interactions with the Tune SDK
 */
public class TuneHandler extends AbstractTagHandler {

/* ************************************ Variables declaration *********************************** */

    /** advertiserId is one of the two parameters required to initialize the Tune SDK */
    final String ADVERTISER_ID = "advertiserId";
    /** conversionKey is one of the two parameters required to initialize the Tune SDK */
    final String CONVERSION_KEY = "conversionKey";

    /** The tracker of the Tune SDK which send the events */
    protected Tune tune;

    /** Constants used to define callbacks in the register and in the execute method */
    private final String TUN_INIT = "TUN_init";
    private final String TUN_IDENTIFY = "TUN_identify";
    private final String TUN_TAG_EVENT = "TUN_tagEvent";

    /** All the parameters that could be set as attributes to a TuneEvent object */
    private final String EVENT_RATING = "eventRating";
    private final String EVENT_DATE1 = "eventDate1";
    private final String EVENT_DATE2 = "eventDate2";
    private final String EVENT_REVENUE = "eventRevenue";
    private final String EVENT_ITEMS = "eventItems";
    private final String EVENT_LEVEL = "eventLevel";
    private final String EVENT_RECEIPT_DATA = "eventReceiptData";
    private final String EVENT_RECEIPT_SIGNATURE = "eventReceiptSignature";
    private final String EVENT_QUANTITY = "eventQuantity";

    /** The above parameters in an array, which is used to clean the map of parameters */
    private final String[] MIXED_PARAMETERS = {
            EVENT_RATING,
            EVENT_DATE1,
            EVENT_DATE2,
            EVENT_REVENUE,
            EVENT_ITEMS,
            EVENT_LEVEL,
            EVENT_RECEIPT_DATA,
            EVENT_RECEIPT_SIGNATURE,
            EVENT_QUANTITY
    };

    /** the formatted name "eventAttributeName" is important here as the string is used in the
        eventBuilder method to call on TuneEvent methods. */
    private final String[] EVENT_PROPERTIES = {
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



/* ************************************ Handler core methods ************************************ */

    /**
     * Called by the TagHandlerManager, initialize the core of the handler
     */
    @Override
    public void initialize() {
        super.initialize("TUN", "Tune", true);
        validate(true);
    }

    /**
     * A callback method for the registered callbacks method name mentioned in the register method.
     *
     * @param s     The method name called through the container (defined in the GTM interface)
     * @param map   A map key-object used as a way to give parameters to the class method aimed here
     */
    public void execute(String s, Map<String, Object> map) {
        logReceivedFunction(s, map);

        if (TUN_INIT.equals(s))
            init(map);
        else if (initialized) {
            switch (s) {
                case TUN_IDENTIFY:
                    identify(map);
                    break;
                case TUN_TAG_EVENT:
                    tagEvent(map);
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
     * The method you need to call first.
     * Register your Tune advertiserId and conversionKey to the Tune SDK
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method
     *              * advertiserId & conversionKey (String) : ids you got when you register your app
     */
    private void init(Map<String, Object> map) {
        String advertiserId = getString(map, ADVERTISER_ID);
        String conversionKey = getString(map, CONVERSION_KEY);

        if (advertiserId != null && conversionKey != null) {
            // set the required parameters
            Tune.init(cargo.getAppContext(), advertiserId, conversionKey);

            // retrieve the Tune instance
            tune = Tune.getInstance();
            setInitialized(true);
        }
        else
            logMissingParam(new String[]{ADVERTISER_ID, CONVERSION_KEY}, TUN_INIT);
    }



/* ****************************************** Tracking ****************************************** */

    /**
     * In order to identify the user as a unique visitor and to associate to a unique id
     * the related social networks ids, age, mail, username, gender...
     *
     * @param map    the parameters given at the moment of the dataLayer.push(),
     *               passed through the GTM container and the execute method.
     *               * userId (String) : an identifier attributed to a unique user (mandatory param)
     *               * userGoogleId (String) : the google id if your user logged in with
     *               * userFacebookId (String) : the facebook id if your user logged in with
     *               * userTwitterId (String) : the twitter id if your user logged in with
     *               * userAge (String) : the age of your user
     *               * userName (String) : the username/name of your user
     *               * userEmail (String) : the mail adress of your user
     *               * userGender (String) : the gender of your user (MALE/FEMALE/UNKNOWN)
     */
    private void identify(Map<String, Object> map) {
        String userId = getString(map, User.USER_ID);
        String userGoogleId = getString(map, User.USER_GOOGLE_ID);
        String userFacebookId = getString(map, User.USER_FACEBOOK_ID);
        String userTwitterId = getString(map, User.USER_TWITTER_ID);
        String userName = getString(map, User.USERNAME);
        String userEmail = getString(map, User.USER_EMAIL);
        String userGender = getString(map, User.USER_GENDER);

        // set the android id given through the User.USER_ID parameter in Tune
        if (userId != null) {
            tune.setUserId(getString(map, User.USER_ID));
            logParamSetWithSuccess(User.USER_ID, userId);
        }

        // set the GOOGLE_ID, FACEBOOK_ID, TWITTER_ID, USERNAME and EMAIL if they exist
        if (userGoogleId != null) {
            tune.setGoogleUserId(userGoogleId);
            logParamSetWithSuccess(User.USER_GOOGLE_ID, userGoogleId);
        }
        if (userFacebookId != null) {
            tune.setFacebookUserId(userFacebookId);
            logParamSetWithSuccess(User.USER_FACEBOOK_ID, userFacebookId);
        }
        if (userTwitterId != null) {
            tune.setTwitterUserId(userTwitterId);
            logParamSetWithSuccess(User.USER_TWITTER_ID, userTwitterId);
        }
        if (userName != null) {
            tune.setUserName(userName);
            logParamSetWithSuccess(User.USERNAME, userName);
        }
        if (userEmail != null) {
            tune.setUserEmail(userEmail);
            logParamSetWithSuccess(User.USER_EMAIL, userEmail);
        }

        // set AGE and GENDER if they exist
        if (map.containsKey(User.USER_AGE)) {
            int age = getInt(map, User.USER_AGE, -1);
            if (age == -1) {
                logUncastableParam(User.USER_AGE, "String");
                return ;
            }
            tune.setAge(age);
            logParamSetWithSuccess(User.USER_AGE, age);
        }
        if (userGender != null)
            setGender(userGender);
    }

    /**
     * Method used to create and fire an event to the Tune Console
     * The mandatory parameter is EVENT_NAME which is a necessity to build the event
     * Without this parameter, the event won't be built.
     * After the creation of the event object, some attributes can be added through the eventBuilder
     * method, using the map obtained from the gtm container.
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method.
     *              The only parameter requested here is an event name
     *              * eventName (String) : the name of the event (mandatory)
     *              * eventCurrencyCode (String)
     *              * eventAdvertiserRefId (String)
     *              * eventContentId (String)
     *              * eventContentType (String)
     *              * eventSearchString (String)
     *              * eventAttribute1 (String)
     *              * eventAttribute2 (String)
     *              * eventAttribute3 (String)
     *              * eventAttribute4 (String)
     *              * eventAttribute5 (String)
     *              * eventRating (Double)
     *              * eventDate1 (Date)
     *              * eventDate2 (Date) : Date1 needs to be set
     *              * eventRevenue (Double)
     *              * eventItems (boolean)
     *              * eventLevel (int)
     *              * eventReceiptData (String) : requires eventReceiptSignature
     *              * eventReceiptSignature (String) : requires eventReceiptData
     *              * eventQuantity (int)
     */
    private void tagEvent(Map<String, Object> map) {
        TuneEvent tuneEvent;
        String eventName = getString(map, Event.EVENT_NAME);

        if (eventName != null) {
            tuneEvent = new TuneEvent(eventName);
            logParamSetWithSuccess(Event.EVENT_NAME, eventName);
            map.remove(Event.EVENT_NAME);
        }
        else {
            logMissingParam(new String[]{Event.EVENT_NAME}, TUN_TAG_EVENT);
            return ;
        }

        if (map.size() > 0)
            tuneEvent = eventBuilder(map, tuneEvent);

        // if the returned event is not null, the event is fired.
        if (tuneEvent != null)
            tune.measureEvent(tuneEvent);
        else
            Log.e(this.key="_handler", "Event object is null, the event hasn't been send.");
    }



/* ****************************************** Utility ******************************************* */

    /**
     * A simple method called by identify() to set the gender in a secured way
     *
     * @param val   The gender given in the identify method.
     *              If the gender doesn't match with the Tune genders,
     *              sets the gender to UNKNOWN.
     */
    private void setGender(String val) {
        String gender = val.toUpperCase(Locale.ENGLISH);
        if (gender.equals("MALE") || gender.equals("FEMALE") || gender.equals("UNKNOWN")) {
            tune.setGender(TuneGender.forValue(val));
            logParamSetWithSuccess(User.USER_GENDER, gender);
        }
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
            Log.e(this.key+"_handler", "trying to set properties on a nil TuneEvent. " +
                    "Operation has been cancelled");
            return null ;
        }

        // for all the different parameters that could be add, we check if they exist,
        // and call on the appropriate TuneEvent method to set it.
        tuneEvent = getEventsWithNumberParameters(map, tuneEvent);
        tuneEvent = getEventsWithComplexParameters(map, tuneEvent);
        for (String Param : MIXED_PARAMETERS) {
            map.remove(Param);
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
        // info log for unknown entries in the map of parameters
        Set<String> keys = map.keySet();
        for (String key : keys) {
            Log.i(this.key+"_handler", " the event builder couldn't find any match with the " +
                    "parameter key [" + key + "] with value [" + getString(map, key) + "]");
        }
        return tuneEvent;
    }

    /**
     * Sets the number parameters which are present in the parameters map as TuneEvent properties
     *
     * @param map       the map of parameters
     * @param tuneEvent the TuneEvent you set the specific properties to
     * @return          the TuneEvent object with the values correctly set
     */
    private TuneEvent getEventsWithNumberParameters(Map<String, Object> map, TuneEvent tuneEvent) {
        if (map.containsKey(EVENT_RATING)) {
            double rating = getDouble(map, EVENT_RATING, -1);
            if (rating != -1) {
                tuneEvent.withRating(rating);
                logParamSetWithSuccess(EVENT_RATING, rating);
            }
            else
                logUncastableParam(EVENT_RATING, "double");
        }

        if (map.containsKey(EVENT_REVENUE)) {
            double revenue = getDouble(map, EVENT_REVENUE, -1);
            if (revenue != -1) {
                tuneEvent.withRevenue(revenue);
                logParamSetWithSuccess(EVENT_REVENUE, revenue);
            }
            else
                logUncastableParam(EVENT_REVENUE, "double");
        }

        if (map.containsKey(EVENT_LEVEL)) {
            Double levelDouble = getDouble(map, EVENT_LEVEL, -1);
            int level = levelDouble.intValue();
            if (level != -1) {
                tuneEvent.withLevel(level);
                logParamSetWithSuccess(EVENT_LEVEL, level);
            }
            else
                logUncastableParam(EVENT_LEVEL, "int");
        }

        if (map.containsKey(EVENT_QUANTITY)) {
            Double quantityDouble = getDouble(map, EVENT_QUANTITY, -1);
            int quantity = quantityDouble.intValue();
            if (quantity != -1) {
                tuneEvent.withQuantity(quantity);
                logParamSetWithSuccess(EVENT_QUANTITY, quantity);
            }
            else
                logUncastableParam(EVENT_QUANTITY, "int");
        }

        return tuneEvent;
    }

    /**
     * Sets the parameters which are present in the parameters map as TuneEvent properties
     *
     * @param map       the map of parameters
     * @param tuneEvent the TuneEvent you set the specific properties to
     * @return          the TuneEvent object with the values correctly set
     */
    private TuneEvent getEventsWithComplexParameters(Map<String, Object> map, TuneEvent tuneEvent) {
        if (map.containsKey(EVENT_DATE1)) {
            Date date = getDate(map, EVENT_DATE1);
            if (date != null) {
                tuneEvent.withDate1(date);
                logParamSetWithSuccess(EVENT_DATE1, date);

                if (map.containsKey(EVENT_DATE2)) {
                    Date date2 = getDate(map, EVENT_DATE2);
                    if (date2 != null) {
                        tuneEvent.withDate2(date2);
                        logParamSetWithSuccess(EVENT_DATE2, date2);
                    }
                    else
                        logUncastableParam(EVENT_DATE2, "date");
                }
            }
            else
                logUncastableParam(EVENT_DATE1, "date");
        }

        if (map.containsKey(EVENT_ITEMS)) {
            Boolean eventItemsBool = getBoolean(map, EVENT_ITEMS, false);
            if (eventItemsBool) {
                List items = getItems();
                if (items != null) {
                    tuneEvent.withEventItems(items);
                    logParamSetWithSuccess(EVENT_ITEMS, items);
                }
                else
                    logUncastableParam(EVENT_ITEMS, "List");
            }
            else
                logUncastableParam(EVENT_ITEMS, "String");
        }

        if (map.containsKey(EVENT_RECEIPT_DATA)) {
            String data = getString(map, EVENT_RECEIPT_DATA);
            String signature = getString(map, EVENT_RECEIPT_SIGNATURE);
            if (data != null && signature != null) {
                tuneEvent.withReceipt(data, signature);
                logParamSetWithSuccess(EVENT_RECEIPT_DATA, data);
                logParamSetWithSuccess(EVENT_RECEIPT_SIGNATURE, signature);
            }
            else
                logUncastableParam(EVENT_RECEIPT_DATA+" and/or "+EVENT_RECEIPT_SIGNATURE, "String");
        }

        return tuneEvent;
    }

    /**
     * Create a List of TuneEventItem objects from the CargoItem.itemsList attribute.
     *
     * @return the list of TuneEventItem objects
     */
    private List<TuneEventItem> getItems() {
        // fill the List with TuneEventItem objects after they get retrieved from the JSONObject
        List<TuneEventItem> tuneEventItems = new ArrayList<TuneEventItem>();

        for (CargoItem item : CargoItem.itemsList) {
            tuneEventItems.add(buildItem(item));
        }
        CargoItem.itemsListGotUsed();
        return tuneEventItems;
    }

    /**
     * Creates a TuneEventItem from a JSONObject and fills the required fields.
     *
     * @param item the item with the CargoItem type
     * @return the item with the correct type, which is TuneEventItem
     */
    private TuneEventItem buildItem(CargoItem item) {

        TuneEventItem tuneItem = new TuneEventItem(item.getName());

        // fills the attributes of the item if they exist
        if (item.getQuantity() != -1)
            tuneItem.quantity = item.getQuantity();
        if (item.getUnitPrice() != -1)
            tuneItem.unitPrice = item.getUnitPrice();
        if (item.getRevenue() != -1)
            tuneItem.revenue = item.getRevenue();
        else if (item.getUnitPrice() != -1 && item.getQuantity() != -1) {
            tuneItem.revenue = item.getUnitPrice() * item.getQuantity();
        }
        if (item.getAttribute1() != null)
            tuneItem.attribute1 = item.getAttribute1();
        if (item.getAttribute2() != null)
            tuneItem.attribute2 = item.getAttribute2();
        if (item.getAttribute3() != null)
            tuneItem.attribute3 = item.getAttribute3();
        if (item.getAttribute4() != null)
            tuneItem.attribute4 = item.getAttribute4();
        if (item.getAttribute5() != null)
            tuneItem.attribute5 = item.getAttribute5();

        return tuneItem;
    }

    /**
     * A callback triggered when an activity starts
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityStarted(Activity activity) {
        if (initialized) {
            TuneActivity.onStart(activity);
        }
    }

    /**
     * A callback triggered when an activity is resumed
     * Used to get referring package name and url scheme from and for the session measurement
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityResumed(Activity activity) {
        if (initialized) {
            TuneActivity.onResume(activity);
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
     * A callback triggered when an activity stops
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityStopped(Activity activity) {
        if (initialized) {
            TuneActivity.onStop(activity);
        }
    }

/* ********************************************************************************************** */
    
}
