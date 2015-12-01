package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.util.Log;

import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.models.Event;
import com.fiftyfive.cargo.models.Screen;
import com.fiftyfive.cargo.models.Transaction;
import com.fiftyfive.cargo.models.User;
import com.google.android.gms.tagmanager.Container;
import com.mobileapptracker.MATEvent;
import com.mobileapptracker.MATEventItem;
import com.mobileapptracker.MobileAppTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fiftyfive.cargo.ModelsUtils.getList;
import static com.fiftyfive.cargo.ModelsUtils.getString;
import static com.fiftyfive.cargo.ModelsUtils.getDouble;

/**
 * Created by louis on 03/11/15.
 */

public class MobileAppTrackingHandler extends AbstractTagHandler {

    public MobileAppTracker mobileAppTracker = null;
    private boolean init = false;

    public Cargo cargo = Cargo.getInstance();

    @Override
    public void execute(String s, Map<String, Object> map) {

        switch (s) {
            case "MAT_init":
                init(map);
                break;
            case "MAT_tagEvent":
                tagEvent(map);
                break;
            case "MAT_tagScreen":
                tagScreen(map);
                break;
            case "MAT_tagTransaction":
                tagTransaction(map);
                break;
            case "MAT_identify":
                identify(map);
                break;
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
        container.registerFunctionCallTagCallback("MAT_init", this);
        container.registerFunctionCallTagCallback("MAT_tagScreen", this);
        container.registerFunctionCallTagCallback("MAT_tagEvent", this);
        container.registerFunctionCallTagCallback("MAT_tagTransaction", this);
        container.registerFunctionCallTagCallback("MAT_identify", this);

    }


    private void init(Map<String, Object> map) {
        //Log.i("55", "Init has been received");

        //on Android the MAT tracker must be initialized before anything else
        if(map.containsKey("advertiserId")
                && map.containsKey("conversionKey")){
            //set the required parameters

            mobileAppTracker.init(cargo.getApplication(),
                    map.remove("advertiserId").toString(),
                    map.remove("conversionKey").toString());

            init = true;
        }

        //Log.w("55", "Missing a required parameter to init MAT");
      }


    private void tagScreen(Map<String, Object> parameters){

        String screenName = getString(parameters, Screen.SCREEN_NAME);

        List<MATEventItem> items = new ArrayList<>();
        MATEventItem eventItem = new MATEventItem(screenName);
        items.add(eventItem);

        mobileAppTracker.measureEvent(new MATEvent(MATEvent.CONTENT_VIEW).withEventItems(items));

    }

    private void identify(Map<String, Object> map) {

        mobileAppTracker.setGoogleUserId(getString(map, User.USER_GOOGLE_ID));
        mobileAppTracker.setFacebookUserId(getString(map, User.USER_FACEBOOK_ID));


    }


    private void tagEvent(Map<String, Object> parameters){

        mobileAppTracker.measureEvent(getString(parameters, Event.EVENT_NAME));


    }

    private void tagTransaction(Map<String, Object> parameters){

        List<Map<String, Object>> transactionProducts = getList(parameters, Transaction.TRANSACTION_PRODUCTS);

        List<MATEventItem> matItems = new ArrayList<>();
        for(Map<String, Object> purchaseItem : transactionProducts){
            String itemName = getString(purchaseItem, Transaction.TRANSACTION_PRODUCT_NAME);
            MATEventItem matItem = new MATEventItem(itemName);
            matItems.add(matItem);
        }



        MATEvent matEvent = new MATEvent(MATEvent.PURCHASE)
                .withEventItems(matItems)
                .withRevenue(getDouble(parameters, Transaction.TRANSACTION_TOTAL, (double) 0));


        mobileAppTracker.measureEvent(matEvent);

    }


    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if(init) {
            // Get source of open for app re-engagement
            mobileAppTracker.setReferralSources(activity);
            // MAT will not function unless the measureSession call is included
            mobileAppTracker.measureSession();
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
