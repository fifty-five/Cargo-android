package com.fiftyfive.cargo_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fiftyfive.cargo.CargoItem;
import com.fiftyfive.cargo.models.Screen;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Julien Gil on 02/02/2018.
 */
public class MarketActivity extends Activity {

    FirebaseAnalytics mFirebaseAnalytics;
    EditText xboxNumber;
    EditText playNumber;
    EditText nintendoNumber;
    boolean shopped;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        setupUI();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        tagScreen();
        tagStartAction();
    }

    @Override
    protected void onResume() {
        super.onResume();
        shopped = false;
    }

    int getQty(EditText eTxt) {
        String str = eTxt.getText().toString();
        if (str.length() == 0) {
            return 0;
        }
        return Integer.parseInt(str);
    }

    View.OnClickListener tagPurchaseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            Double revenue = 0.0;
            int xboxQty = getQty(xboxNumber);
            int playQty = getQty(playNumber);
            int nintendoQty = getQty(nintendoNumber);

            if (xboxQty > 0) {
                revenue += (149.99 * xboxQty);
                CargoItem.attachItemToEvent(
                        new CargoItem("xBox One").setUnitPrice(149.99).setQuantity(xboxQty).setAttribute1("Microsoft")
                );
                Bundle actionUpdate = new Bundle();
                actionUpdate.putString("item", "xbox");
                actionUpdate.putInt("quantity", xboxQty);
                actionUpdate.putDouble("revenue", revenue);
                mFirebaseAnalytics.logEvent("tagActionUpdate", actionUpdate);
            }
            if (playQty > 0) {
                revenue += (199 * playQty);
                CargoItem.attachItemToEvent(
                        new CargoItem("PlayStation 4").setUnitPrice(199).setQuantity(playQty).setAttribute1("Sony")
                );
                Bundle actionUpdate = new Bundle();
                actionUpdate.putString("item", "PlayStation");
                actionUpdate.putInt("quantity", playQty);
                actionUpdate.putDouble("revenue", revenue);
                mFirebaseAnalytics.logEvent("tagActionUpdate", actionUpdate);
            }
            if (nintendoQty > 0) {
                revenue += (255 * nintendoQty);
                CargoItem.attachItemToEvent(
                        new CargoItem("Nintendo Switch").setUnitPrice(255).setQuantity(nintendoQty).setAttribute1("Nintendo")
                );
                Bundle actionUpdate = new Bundle();
                actionUpdate.putString("item", "Nintendo");
                actionUpdate.putInt("quantity", nintendoQty);
                actionUpdate.putDouble("revenue", revenue);
                mFirebaseAnalytics.logEvent("tagActionUpdate", actionUpdate);
            }

            bundle.putString("currencyCode", "EUR");
            bundle.putDouble("totalRevenue", revenue);
            bundle.putBoolean("eventItems", CargoItem.getItemsList() != null);
            mFirebaseAnalytics.logEvent("tagPurchase", bundle);

            if (xboxQty > 0 || playQty > 0 || nintendoQty > 0) {
                shopped = true;
                Bundle actionEnd = new Bundle();
                actionEnd.putString("actionName", "shop");
                actionEnd.putBoolean("successfulAction", true);
                mFirebaseAnalytics.logEvent("tagActionEnd", actionEnd);
            }
            onBackPressed();
        }
    };

    void setupUI() {
        Button tagPurchaseButton = findViewById(R.id.tagPurchaseButton);
        tagPurchaseButton.setOnClickListener(tagPurchaseListener);
        xboxNumber = findViewById(R.id.editTextXbox);
        playNumber = findViewById(R.id.editTextPlay);
        nintendoNumber = findViewById(R.id.editTextNintendo);
    }

    void tagScreen() {
        Bundle tagScreen = new Bundle();
        tagScreen.putString(Screen.SCREEN_NAME, "market");
        mFirebaseAnalytics.logEvent("tagScreen", tagScreen);
    }

    void tagStartAction() {
        Bundle startAction = new Bundle();
        startAction.putString("actionName", "shop");
        mFirebaseAnalytics.logEvent("tagActionStart", startAction);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!shopped) {
            Bundle actionEnd = new Bundle();
            actionEnd.putString("actionName", "shop");
            actionEnd.putBoolean("successfulAction", false);
            mFirebaseAnalytics.logEvent("tagActionEnd", actionEnd);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (shopped) {
            intent.putExtra("shopMessage", "Shopping action sent");
        }
        else {
            intent.putExtra("shopMessage", "Shopping action not sent");
        }
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }
}
