package com.fiftyfive.cargo_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.fiftyfive.cargo.CargoItem;
import com.google.firebase.analytics.FirebaseAnalytics;


public class MainActivity extends AppCompatActivity {

    FirebaseAnalytics mFirebaseAnalytics;
    String username;
    String userMail;
    EditText userText;
    EditText mailAdressText;
    EditText xboxNumber;
    EditText playNumber;
    EditText nintendoNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    View.OnClickListener tagEventListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();

            username = userText.getText().toString();
            userMail = mailAdressText.getText().toString();

            if (username.length() > 0)
                bundle.putString("username", username);
            if (userMail.length() > 0)
                bundle.putString("userEmail", userMail);

            mFirebaseAnalytics.logEvent("tagEvent", bundle);
        }
    };

    View.OnClickListener tagScreenListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFirebaseAnalytics.logEvent("tagScreen", null);
        }
    };

    View.OnClickListener setUserListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            username = userText.getText().toString();
            userMail = mailAdressText.getText().toString();

            if (username.length() > 0)
                bundle.putString("username", username);
            if (userMail.length() > 0)
                bundle.putString("userEmail", userMail);

            mFirebaseAnalytics.logEvent("identify", bundle);
        }
    };

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
            }
            if (playQty > 0) {
                revenue += (199 * playQty);
                CargoItem.attachItemToEvent(
                        new CargoItem("PlayStation 4").setUnitPrice(199).setQuantity(playQty).setAttribute1("Sony")
                );
            }
            if (nintendoQty > 0) {
                revenue += (255 * nintendoQty);
                CargoItem.attachItemToEvent(
                        new CargoItem("Nintendo Switch").setUnitPrice(255).setQuantity(nintendoQty).setAttribute1("Nintendo")
                );
            }

            bundle.putString("currencyCode", "EUR");
            bundle.putDouble("totalRevenue", revenue);
            if (CargoItem.getItemsList() != null)
                bundle.putBoolean("eventItems", true);
            mFirebaseAnalytics.logEvent("tagPurchase", bundle);
        }
    };

    int getQty(EditText eTxt) {
        String str = eTxt.getText().toString();
        if (str.length() == 0)
            return 0;
        return Integer.parseInt(str);
    }

    void setupUI() {
        Button tagEventButton = (Button) findViewById(R.id.tagEventButton);
        tagEventButton.setOnClickListener(tagEventListener);
        Button tagScreenButton = (Button) findViewById(R.id.tagScreenButton);
        tagScreenButton.setOnClickListener(tagScreenListener);
        Button setUserButton = (Button) findViewById(R.id.identifyButton);
        setUserButton.setOnClickListener(setUserListener);
        Button tagPurchaseButton = (Button) findViewById(R.id.tagPurchaseButton);
        tagPurchaseButton.setOnClickListener(tagPurchaseListener);

        userText = (EditText)findViewById(R.id.usernameInput);
        mailAdressText = (EditText)findViewById(R.id.mailAdressInput);

        xboxNumber = (EditText)findViewById(R.id.editTextXbox);
        playNumber = (EditText)findViewById(R.id.editTextPlay);
        nintendoNumber = (EditText)findViewById(R.id.editTextNintendo);
    }

}
