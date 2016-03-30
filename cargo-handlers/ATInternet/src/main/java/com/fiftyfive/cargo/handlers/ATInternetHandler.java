package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.provider.Settings;
import android.util.Log;

import com.atinternet.tracker.SetConfigCallback;
import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.AbstractTagHandler;
import com.fiftyfive.cargo.models.Screen;
import com.fiftyfive.cargo.models.Transaction;
import com.google.android.gms.tagmanager.Container;
import com.atinternet.tracker.ATInternet;
import com.atinternet.tracker.Tracker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.provider.Settings.Secure;

import static com.fiftyfive.cargo.ModelsUtils.getDouble;
import static com.fiftyfive.cargo.ModelsUtils.getInt;
import static com.fiftyfive.cargo.ModelsUtils.getList;
import static com.fiftyfive.cargo.ModelsUtils.getString;

/**
 * Created by dali on 04/12/15.
 */

public class ATInternetHandler extends AbstractTagHandler {

    /** The AT Tracker */
    public Tracker atTracker;
    public Cargo cargo;

    @Override
    public void execute(String s, Map<String, Object> map) {

        switch (s) {
            case "AT_tagScreen":
                tagScreen(map);
                break;
            case "AT_identify":
                identify();
                break;
            case "AT_tagTransaction":
                tagTransaction(map);
                break;
            default:
                Log.i("55", "Function "+s+" is not registered");
        }
    }

    @Override
    public void initialize() {
        cargo = Cargo.getInstance();
        atTracker = ((ATInternet) cargo.getApplication()).getDefaultTracker();
        //todo : check permissions
        this.valid = true;
    }


    @Override
    public void register(Container container) {
        container.registerFunctionCallTagCallback("AT_tagScreen", this);
        container.registerFunctionCallTagCallback("AT_tagTransaction", this);
        container.registerFunctionCallTagCallback("AT_identify", this);
    }

    private void tagScreen(Map<String, Object> parameters){

        String screenName = getString(parameters, Screen.SCREEN_NAME);

        if(parameters.containsKey(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM1)
                && parameters.containsKey(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM2)) {

            final String customDim1 = getString(parameters, com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM1);
            final String customDim2 = getString(parameters, com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM2);

            atTracker.CustomObjects().add(new HashMap<String, Object>() {{
                put(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM1, customDim1);
                put(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM2, customDim2);
            }});
        }
        atTracker.Screens().add(screenName).setLevel2(getInt(parameters, com.fiftyfive.cargo.models.Tracker.LEVEL2, 0)).sendView();
    }


    private void tagTransaction(Map<String, Object> parameters){
        List<Map<String, Object>> transactionProducts = getList(parameters, Transaction.TRANSACTION_PRODUCTS);

        atTracker.Cart().set(getString(parameters, "idCart"));

        for(Map<String, Object> purchaseItem : transactionProducts){

            atTracker.Cart().Products().add(getString(purchaseItem, Transaction.TRANSACTION_PRODUCT_NAME))
                    .setProductId(getString(purchaseItem, Transaction.TRANSACTION_PRODUCT_SKU))
                    .setCategory1(getString(purchaseItem, Transaction.TRANSACTION_PRODUCT_CATEGORY))
                    .setQuantity(getInt(purchaseItem, Transaction.TRANSACTION_PRODUCT_QUANTITY, 0))
                    .setUnitPriceTaxIncluded(getDouble(purchaseItem, Transaction.TRANSACTION_PRODUCT_PRICE, 0));
        }


        atTracker.Orders().add(getString(parameters, Transaction.TRANSACTION_ID),
                getDouble(parameters, Transaction.TRANSACTION_TOTAL, 0));

        atTracker.Cart().unset();
    }


    // as we look for unique visitor, we use the android ID which is unique for each android device
    private void identify(){
        final String android_id = Settings.Secure.getString(cargo.getApplication().getContentResolver(), Secure.ANDROID_ID);

        ((ATInternet) cargo.getApplication()).getDefaultTracker().setConfig("identifier", android_id, new SetConfigCallback() {
            @Override
            public void setConfigEnd() {
                Log.i("atInternetHandler", "SDK is now using Android ID as visitor identifier : " + android_id);
            }
        });

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
