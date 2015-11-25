package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.models.Event;
import com.fiftyfive.cargo.models.TransactionProduct;
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

/**
 * Created by louis on 03/11/15.
 */

public class MobileAppTrackingHandler extends AbstractTagHandler {

    public MobileAppTracker mobileAppTracker;
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

        final ObjectMapper mapper = new ObjectMapper();
        final Screen screen = mapper.convertValue(parameters, Screen.class);


        List<MATEventItem> items = new ArrayList<>();
        MATEventItem eventItem = new MATEventItem(screen.getScreenName());
        items.add(eventItem);

        mobileAppTracker.measureEvent(new MATEvent(MATEvent.CONTENT_VIEW).withEventItems(items));

    }

    private void identify(Map<String, Object> map) {
        final ObjectMapper mapper = new ObjectMapper();
        User user = mapper.convertValue(map, User.class);

        mobileAppTracker.setGoogleUserId(user.getUserGoogleId());


    }


    private void tagEvent(Map<String, Object> parameters){

        if(!init){
            //Log.w("55", "You should init MAT before tagging screen");
            return;
        }
        final ObjectMapper mapper = new ObjectMapper();
        Event event = mapper.convertValue(parameters, Event.class);
        mobileAppTracker.measureEvent(event.getEventName());


    }

    private void tagTransaction(Map<String, Object> parameters){

        if(!init){
            //Log.w("55", "You should init MAT before tagging screen");
            return;
        }

        final ObjectMapper mapper = new ObjectMapper();
        final Transaction transaction = mapper.convertValue(parameters, Transaction.class);


        List<MATEventItem> matItems = new ArrayList<>();
        for(TransactionProduct purchaseItem : transaction.getTransactionProducts()){
            MATEventItem matItem = new MATEventItem(purchaseItem.getName());
            matItems.add(matItem);
        }



        MATEvent matEvent = new MATEvent(MATEvent.PURCHASE)
                .withEventItems(matItems)
                .withRevenue(Double.parseDouble(transaction.getTransactionTotal()));


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
