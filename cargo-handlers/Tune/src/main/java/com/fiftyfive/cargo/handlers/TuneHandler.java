package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.util.Log;

import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.models.User;
import com.google.android.gms.tagmanager.Container;
import com.tune.Tune;
import com.tune.TuneEvent;
import com.tune.TuneGender;

import java.util.Locale;
import java.util.Map;

import static com.fiftyfive.cargo.ModelsUtils.getInt;
import static com.fiftyfive.cargo.ModelsUtils.getString;


/**
 * Author : louis
 * Created: 03/11/15
 *
 *  * The class which handles interactions with the Facebook SDK
 */
public class TuneHandler extends AbstractTagHandler {

    public Tune tune;
    private boolean init = false;

    public Cargo cargo = Cargo.getInstance();

    String ADVERTISER_ID = "advertiserId";
    String CONVERSION_KEY = "conversionKey";

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
        container.registerFunctionCallTagCallback("Tune_init", this);
        container.registerFunctionCallTagCallback("Tune_identify", this);

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
            case "Tune_init":
                init(map);
                break;
            case "Tune_identify":
                identify(map);
            default:
                Log.i("Cargo TuneHandler", "Function "+s+" is not registered");
        }
    }

    /**
     * The method you need to call first. Register your facebook app id to the facebook SDK
     *
     * @param map   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method
     *              * advertiserId & conversionKey : ids Tune gives you when you register your app
     *
     */
    private void init(Map<String, Object> map) {
        if (init)
            Log.i("Cargo TuneHandler", "Tune is already init");
        else if(map.containsKey(ADVERTISER_ID) && map.containsKey(CONVERSION_KEY)) {
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
     * In order to identify the user as a unique visitor,
     * we use the android ID which is unique for each android device
     * Check http://stackoverflow.com/a/2785493 if you don't know how to retrieve it
     *
     * @param map    the parameters given at the moment of the dataLayer.push(),
     *                      passed through the GTM container and the execute method.
     *                      The only parameter requested here is the android_id (User.USER_ID)
     */
    private void identify(Map<String, Object> map) {
        // set the android id given through the User.USER_ID parameter in Tune
        String android_id = getString(map, User.USER_ID);
        if (android_id == null) {
            Log.w("Cargo TuneHandler", " in identify() missing mandatory parameter USER_ID. " +
                    "USER_ID and any other parameters given haven't been set");
            return ;
        }
        // set the GOOGLE_ID, FACEBOOK_ID, TWITTER_ID, AGE and GENDER if they exist
        tune.setUserId(android_id);
        if (map.containsKey(User.USER_GOOGLE_ID))
            tune.setGoogleUserId(getString(map, User.USER_GOOGLE_ID));
        if (map.containsKey(User.USER_FACEBOOK_ID))
            tune.setFacebookUserId(getString(map, User.USER_FACEBOOK_ID));
        if (map.containsKey(User.USER_TWITTER_ID))
            tune.setTwitterUserId(getString(map, User.USER_TWITTER_ID));
        if (map.containsKey(User.USER_AGE))
            tune.setAge(getInt(map, User.USER_AGE, -1));
        if (map.containsKey(User.USER_GENDER))
            setGender(getString(map, User.USER_GENDER));

    }

    private void setGender(String val) {
        String gender = val.toUpperCase(Locale.ENGLISH);
        if (gender.equals("MALE") || gender.equals("FEMALE") || gender.equals("UNKNOWN"))
            tune.setGender(TuneGender.forValue(val));
        else
            Log.w("Cargo TuneHandler", "tune.setGender() waits for MALE/FEMALE/UNKNOWN");

    }


    @Override
    public void onActivityStarted(Activity activity) {

    }


    @Override
    public void onActivityResumed(Activity activity) {
        if (init) {
            tune.setReferralSources(activity);
            tune.measureSession();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    public boolean isInitialized(){
        return init;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }




}
