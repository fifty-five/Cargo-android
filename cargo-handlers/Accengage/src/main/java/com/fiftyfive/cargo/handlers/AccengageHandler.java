package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ad4screen.sdk.A4S;
import com.ad4screen.sdk.analytics.Cart;
import com.ad4screen.sdk.analytics.Item;
import com.ad4screen.sdk.analytics.Lead;
import com.ad4screen.sdk.analytics.Purchase;
import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.models.Screen;
import com.fiftyfive.cargo.models.Transaction;
import com.google.android.gms.tagmanager.Container;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.ad4screen.sdk.A4SIdsProvider;

import static com.fiftyfive.cargo.ModelsUtils.getDate;
import static com.fiftyfive.cargo.ModelsUtils.getDouble;
import static com.fiftyfive.cargo.ModelsUtils.getList;
import static com.fiftyfive.cargo.ModelsUtils.getLong;
import static com.fiftyfive.cargo.ModelsUtils.getString;
import com.fiftyfive.cargo.models.Event;


/**
 * Author : Julien Gil
 * Created: 12/10/16
 *
 *  * The class which handles interactions with the Accengage SDK
 */
public class AccengageHandler extends AbstractTagHandler implements A4SIdsProvider {

    // define all the method names which can be called
    private final String ACC_init = "ACC_init";
    private final String ACC_intent = "ACC_intent";
    private final String ACC_tagEvent = "ACC_tagEvent";
    private final String ACC_tagLead = "ACC_tagLead";
    private final String ACC_tagAddToCart = "ACC_tagAddToCart";
    private final String ACC_tagPurchase = "ACC_tagPurchase";
    private final String ACC_updateDeviceInfo = "ACC_updateDeviceInfo";
    private final String ACC_tagView = "ACC_tagView";

    private String partnerId = null;
    private String privateKey = null;

    private boolean init = false;

    protected A4S tracker;
    /**
     * Init properly the SDK
     */
    @Override
    public void initialize() {
        super.initialize();
        tracker = A4S.get(cargo.getApplication());
        this.valid = true;
    }

    /**
     * Register the callbacks to GTM. These will be triggered after a dataLayer.push()
     *
     * @param container The instance of the GTM container we register the callbacks to
     */
    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback(ACC_init, this);
        container.registerFunctionCallTagCallback(ACC_intent, this);
        container.registerFunctionCallTagCallback(ACC_tagEvent, this);
        container.registerFunctionCallTagCallback(ACC_tagLead, this);
        container.registerFunctionCallTagCallback(ACC_tagAddToCart, this);
        container.registerFunctionCallTagCallback(ACC_tagPurchase, this);
        container.registerFunctionCallTagCallback(ACC_updateDeviceInfo, this);
        container.registerFunctionCallTagCallback(ACC_tagView, this);
    }

    /**
     * This one will be called after an event has been pushed to the dataLayer
     *
     * @param s     The method you aim to call (this should be define in GTM interface)
     * @param map   A map key-object used as a way to give parameters to the class method aimed here
     */
    @Override
    public void execute(String s, Map<String, Object> map) {

        if (s.equals(ACC_init))
            init(map);
        else if (!init) {
            Log.i("Cargo AccengageHandler", " the handler hasn't be initialized, " +
                    "please do so before doing anything else.");
        }
        else {
            switch (s) {
                case ACC_init:
                    init(map);
                    break;
                case ACC_intent:
                    setIntentA4S(map);
                    break;
                case ACC_tagEvent:
                    tagEvent(map);
                    break;
                case ACC_tagLead:
                    tagLead(map);
                    break;
                case ACC_tagAddToCart:
                    tagAddToCart(map);
                    break;
                case ACC_tagPurchase:
                    tagPurchase(map);
                    break;
                case ACC_updateDeviceInfo:
                    updateDeviceInfo(map);
                    break;
                case ACC_tagView:
                    tagView(map);
                    break;
                default:
                    Log.i("55", "Function " + s + " is not registered");
            }
        }
    }

    /**
     * The method you need to call first.
     * Register the private key and the partner ID to the Accengage SDK.
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method
     *              * privateKey & partnerId : ids Accengage gives you when you register your app
     */
    private void init(Map<String, Object> map) {
        privateKey = getString(map, "privateKey");
        partnerId = getString(map, "partnerId");

        if (privateKey == null || partnerId == null) {
            Log.w("Cargo AccengageHandler", " partnerId and/or privateKey is missing for " +
                    "the Accengage SDK initialization");
        }
        else {
            init = true;
        }
    }

    /**
     * This method is used as an override of the Activity method onNewIntent(Intent intent)
     * You need to call it after the super.onNewIntent(intent) in all of your activities.
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method
     *              * intent : the intent given in the onNewIntent()  method of your activity
     */
    private void setIntentA4S(Map<String, Object> map) {
        Intent intent = (Intent)map.get("intent");
        if (intent == null) {
            Log.w("Cargo AccengageHandler", " intent is missing for " +
                    "the Accengage setIntentA4S method");
        }
        else {
            tracker.setIntent(intent);
        }
    }

    /**
     * Method used to create and fire an event to the Accengage interface
     * The mandatory parameters are EVENT_NAME, EVENT_ID which are a necessity to build the event
     * Without these parameters, the event won't be built.
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method.
     *              * EVENT_ID (long) : the id for the event. Its value should be greater than 1000
     *              * EVENT_NAME (String) : the name for this event.
     *              * parameters as String... : the other entries of the map are put in a String[]
     *                  as String formatted like "key: value"
     */
    private void tagEvent(Map<String, Object> map) {
        long eventId = getLong(map, Event.EVENT_ID, 0);
        String eventName = getString(map, Event.EVENT_NAME);
        int arraySize = (map.size() - 2);

        // a check in order to log if the event ID is not present or badly formatted
        if (eventId < 1000) {
            Log.w("Cargo AccengageHandler", " eventId is missing or uses a wrong format for " +
                    "the Accengage tagEvent method");
        }
        // a check to log if the eventName is not present
        else if (eventName == null) {
            Log.w("Cargo AccengageHandler", " eventName is missing for " +
                    "the Accengage tagEvent method");
        }
        // the core of the method, which fires the event
        else {
            String eventParam;
            map.remove(Event.EVENT_ID);
            map.remove(Event.EVENT_NAME);
            // if there is extra parameters, puts them in a String array in the format "key: value"
            if (arraySize > 0) {
                String[] parameters = new String[arraySize];
                arraySize = 0;

                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    eventParam = entry.getKey()+": "+getString(map, entry.getKey());
                    parameters[arraySize++] = eventParam;
                }
                // fire the event with the array of parameters
                tracker.trackEvent(eventId, eventName, parameters);
            }
            else {
                // fire the event without an array of parameters
                tracker.trackEvent(eventId, eventName);
            }
        }
    }

    /**
     * Method used to create and fire a screen view to Accengage
     * The mandatory parameters is SCREEN_NAME which is a necessity to build the tagScreen.
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method.
     *              * SCREEN_NAME (String) : the name of the screen that has been seen
     */
    private void tagView(Map<String, Object> map) {
        String screenName = getString(map, Screen.SCREEN_NAME);

        if (screenName != null) {
            tracker.setView(screenName);
        }
        else {
            Log.w("Cargo AccengageHandler", " "+Screen.SCREEN_NAME+" is missing for " +
                    "the Accengage tagView method");
        }
    }

    /**
     * The method used to create and fire a custom lead to Accengage.
     * Both parameters are mandatory.
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method.
     *              * leadLabel (String) : the label of the lead
     *              * leadValue (String) : the value associated to the lead label
     */
    private void tagLead(Map<String, Object> map) {
        String leadLabel = getString(map, "leadLabel");
        String leadValue = getString(map, "leadValue");

        if (leadLabel != null && leadValue != null) {
            tracker.trackLead(new Lead(leadLabel, leadValue));
        }
        else {
            Log.w("Cargo AccengageHandler", " leadLabel and/or leadValue is missing for " +
                    "the Accengage tagLead method");
        }
    }

    /**
     * The method used to report an add to cart to Accengage. It logs the id of the cart,
     * and the item which has been added. Both parameters are mandatory.
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method.
     *              * TRANSACTION_ID (String) : the id associated to this cart.
     *              * item (AccItem) : the item which is added to the cart.
     */
    private void tagAddToCart(Map<String, Object> map) {
        AccItem accItem = getItem(map, "item");
        String cartId = getString(map, Transaction.TRANSACTION_ID);

        if (accItem != null && cartId != null) {
            tracker.trackAddToCart(new Cart(cartId, accItem.toItem()));
        }
        else {
            Log.w("Cargo AccengageHandler", " item and/or "+Transaction.TRANSACTION_ID+
                    " is missing for the Accengage tagAddToCart method");
        }
    }

    /**
     * The method used to report a purchase in your app in Accengage.
     * TRANSACTION_ID, TRANSACTION_CURRENCY_CODE, TRANSACTION_TOTAL are required.
     * TRANSACTION_PRODUCTS is optional.
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method.
     *              * TRANSACTION_ID (String) : the id associated to this purchase.
     *              * TRANSACTION_CURRENCY_CODE (String) : the currency used in the transaction.
     *                  Should be a valid 3 letters ISO4217 currency (EUR,USD,..)
     *              * TRANSACTION_TOTAL (Double) : the amount of the transaction
     *              * TRANSACTION_PRODUCTS (List of AccItem) : the list of the products purchased
     */
    private void tagPurchase(Map<String, Object> map) {
        String id = getString(map, Transaction.TRANSACTION_ID);
        String currencyCode = getString(map, Transaction.TRANSACTION_CURRENCY_CODE);
        Double totalPrice = getDouble(map, Transaction.TRANSACTION_TOTAL, -1);
        List<AccItem> items = getList(map, Transaction.TRANSACTION_PRODUCTS);

        // check for the required parameters to be available
        if (id != null && currencyCode != null && totalPrice >= 0) {
            // if the optional parameter is present, get into this statement
            if (items != null) {
                int size = items.size();
                Item[] purchaseItems = new Item[size];

                size = 0;
                // for each item in the list, change it to the right type and add it in an array
                for (AccItem item : items) {
                    purchaseItems[size++] = item.toItem();
                }
                // fires the purchase event with all the parameters
                tracker.trackPurchase(new Purchase(id, currencyCode, totalPrice, purchaseItems));
            }
            else {
                // fires the purchase event without the optional parameter
                Purchase purchase = new Purchase(id, currencyCode, totalPrice);
                tracker.trackPurchase(purchase);
            }
        }
        // logs a warning for the missing mandatory parameters
        else {
            Log.w("Cargo AccengageHandler", Transaction.TRANSACTION_ID + " and/or " +
                    Transaction.TRANSACTION_CURRENCY_CODE + " and/or " +
                    Transaction.TRANSACTION_TOTAL + " is missing for the Accengage tagPurchase method");
        }
    }

    /**
     * A device profile is a set of key/value that are uploaded to Accengage server.
     * You can create a device profile for each device in order to qualify the profile
     * (for example, registering whether the user is opt in for or
     * out of some categories of notifications).
     * In order to update information about a device profile, use this method.
     * Two parameters are required : deviceInfoKey and (deviceInfoValue or deviceInfoDate).
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method.
     *              * deviceInfoKey (String) : the key of the set for this device info
     *              * deviceInfoValue (String) : the value linked to the key
     *              * deviceInfoDate (Date) : a date value which can be used
     *                  instead of the String typed value if needed.
     */
    private void updateDeviceInfo(Map<String, Object> map) {
        String  key = getString(map, "deviceInfoKey");
        String  value = getString(map, "deviceInfoValue");
        Date    date = getDate(map, "deviceInfoDate");

        // check for the presence of the mandatory parameter
        if (key != null) {
            Bundle bundle = new Bundle();

            // if the value parameter is set, build the bundle
            if (value != null)
                bundle.putString(key, value);
            // else if the date parameter is set, build the bundle with it
            else if (date != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
                bundle.putString(key, dateFormat.format(date));
            }
            // logs a warning if the value or the date is missing
            else {
                Log.w("Cargo AccengageHandler", " deviceInfoValue or deviceInfoDate" +
                        " is missing for the Accengage updateDeviceInfo method");
                return ;
            }

            // send the update device event to the server
            tracker.updateDeviceInfo(bundle);
        }
        // if the first required parameter isn't set, logs a warning
        else {
            Log.w("Cargo AccengageHandler", " deviceInfoKey" +
                    " is missing for the Accengage updateDeviceInfo method");
        }
    }

    /**
     * A method needed in the Accengage SDK since we don't set the partner ID in the manifest.
     * A service uses this method in order to retrieve the partner ID.
     * @param context   the Application context
     * @return          the partner ID
     */
    @Override
    public String getPartnerId(Context context) {
        return partnerId;
    }

    /**
     * A method needed in the Accengage SDK since we don't set the private key in the manifest.
     * A service uses this method in order to retrieve the private key.
     * @param context   the Application context
     * @return          the private key
     */
    @Override
    public String getPrivateKey(Context context) {
        return privateKey;
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
        tracker.startActivity(activity);
    }

    /**
     * A callback triggered when an activity is paused
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityPaused(Activity activity) {
        tracker.stopActivity(activity);
    }

    /**
     * A callback triggered when an activity stops
     * @param activity  the activity which triggered the callback
     */
    @Override
    public void onActivityStopped(Activity activity) {
    }

    /**
     * The getter for the init boolean, returning if the tagHandler has been initialized
     *
     * @return the boolean
     */
    public boolean isInitialized() { return init; }

    /**
     * This setter is made for testing purpose and shouldn't be used outside of the test class.
     *
     * @param value the boolean value you want the "init" attribute to be set with.
     */
    protected void setInitialize(boolean value) {
        this.init = value;
    }

    /**
     * A short method used to get an AccItem typed object from a map.
     *
     * @param params    the map where is stored the AccItem as an Object
     * @param name      the key used to store the AccItem in the map
     * @return          an AccItem object, or null if there were no object associated to the key
     *                      or if the object was not castable.
     */
    private AccItem getItem(Map<String, Object> params, String name) {
        Object value = params.get(name);
        if (value instanceof AccItem){
            return (AccItem) value;
        }
        else if (value != null){
            return (AccItem) value;
        }
        return null;
    }
}

/**
 * A class to make the link between the app Item type and the Accengage Item type.
 * Properties are public in order to make any changes if needed, as a discount or whatever.
 */
class AccItem {

    public String id;
    public String name;
    public String category;
    public String currencyCode;
    public Double price;
    public int quantity;

    /**
     * The constructor of the object
     *
     * @param id            the id which refer to the product
     * @param name          the name of the product
     * @param category      the category the product belongs to
     * @param currencyCode  the currency used to define the price
     *                      Should be a valid 3 letters ISO4217 currency (EUR,USD,..)
     * @param price         the price of the item
     * @param quantity      the quantity of this item which is bought or added to cart.
     */
    public AccItem(String id, String name, String category, String currencyCode, Double price, int quantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.currencyCode = currencyCode;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * A method that transform the AccItem type into a Item type from Accengage SDK
     *
     * @return  an Item (from Accengage SDK) object
     */
    protected Item toItem() {
        return (new Item(id, name, category, currencyCode, price, quantity));
    }
}