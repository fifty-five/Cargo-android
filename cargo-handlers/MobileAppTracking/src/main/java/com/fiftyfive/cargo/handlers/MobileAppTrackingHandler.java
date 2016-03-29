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
 * Created by louis on 03/11/15.
 */

public class MobileAppTrackingHandler extends AbstractTagHandler {

    public Tune tune;
    private boolean init = false;

    public Cargo cargo = Cargo.getInstance();

    @Override
    public void execute(String s, Map<String, Object> map) {

        switch (s) {
            case "Tune_init":
                init(map);
                break;
            case "Tune_purchase":
                purchase(map);
                break;
            case "Tune_identify":
                identify(map);
            default:
                Log.i("55", "Function "+s+" is not registered");
        }
    }

    @Override
    public void initialize() {
        //todo : check permissions
        this.valid = true;
    }


    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback("Tune_init", this);
        container.registerFunctionCallTagCallback("Tune_purchase", this);
        container.registerFunctionCallTagCallback("Tune_identify", this);

    }


    private void init(Map<String, Object> map) {
        if(!init && map.containsKey("advertiserId")
                && map.containsKey("conversionKey")){
            //set the required parameters

            tune.init(cargo.getApplication(),
                    map.remove("advertiserId").toString(),
                    map.remove("conversionKey").toString());

            init = true;
        }
        else if (!map.containsKey("advertiserId") || !map.containsKey("conversionKey"))
            Log.w("55", "Missing a required parameter to init MAT");
        else
            Log.i("55", "MAT is already init");
      }

    // possibility to add more information to the event
    private void purchase(Map<String, Object> map) {
        tune.measureEvent(new TuneEvent(TuneEvent.PURCHASE));
    }

    private void identify(Map<String, Object> map) {
        tune.setUserId(User.USER_ID);
        tune.setGoogleUserId(getString(map, User.USER_GOOGLE_ID));
        tune.setFacebookUserId(getString(map, User.USER_FACEBOOK_ID));
        tune.setTwitterUserId(getString(map, User.USER_TWITTER_ID));
        
    }


    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (init) {
            Tune.getInstance().setReferralSources(activity);
            Tune.getInstance().measureSession();
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
