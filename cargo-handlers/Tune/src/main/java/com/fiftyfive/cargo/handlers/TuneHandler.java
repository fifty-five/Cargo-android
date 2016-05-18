package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.util.Log;

import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.models.User;
import com.google.android.gms.tagmanager.Container;
import com.tune.Tune;
import com.tune.TuneEvent;

import java.util.Map;

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
                Log.i("55", "Function "+s+" is not registered");
        }
    }

    /**
     * The method you need to call first. Register your facebook app id to the facebook SDK
     *
     * @param parameters   the parameters given at the moment of the dataLayer.push(),
     *              passed through the GTM container and the execute method
     *              * advertiserId & conversionKey : ids Tune gives you when you register your app
     *
     */
    private void init(Map<String, Object> parameters) {
        if(!init && parameters.containsKey(ADVERTISER_ID) && parameters.containsKey(CONVERSION_KEY)) {

            // set the required parameters
            Tune.init(cargo.getApplication(),
                    parameters.remove(ADVERTISER_ID).toString(),
                    parameters.remove(CONVERSION_KEY).toString());

            // retrieve the Tune instance
            tune = Tune.getInstance();
            init = true;
        }
        else if (!parameters.containsKey(ADVERTISER_ID) || !parameters.containsKey(CONVERSION_KEY))
            Log.w("55", "Missing a required parameter to init Tune");
        else
            Log.i("55", "MAT is already init");
      }

    /**
      * In order to identify the user as a unique visitor,
      * we use the android ID which is unique for each android device
      * Check http://stackoverflow.com/a/2785493 if you don't know how to retrieve it
      *
      * @param parameters    the parameters given at the moment of the dataLayer.push(),
      *                      passed through the GTM container and the execute method.
      *                      The only parameter requested here is the android_id (User.USER_ID)
      */
    private void identify(Map<String, Object> parameters) {
        // set the android id given through the User.USER_ID parameter in Tune
        String android_id = getString(parameters, User.USER_ID);
        if (android_id == null) {
            Log.w("CARGO ATInternetHandler", " in identify() missing USER_ID (android_id) parameter. USER_ID hasn't been set");
            return ;
        }

        // set the USER_GOOGLE_ID & USER_FACEBOOK_ID if they exist
        tune.setUserId(android_id);
        if (getString(parameters, User.USER_GOOGLE_ID) != null)
            tune.setGoogleUserId(parameters.remove(User.USER_GOOGLE_ID).toString());
        if (getString(parameters, User.USER_FACEBOOK_ID) != null)
            tune.setFacebookUserId(parameters.remove(User.USER_FACEBOOK_ID).toString());
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
