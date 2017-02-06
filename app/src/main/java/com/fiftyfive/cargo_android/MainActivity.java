package com.fiftyfive.cargo_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.fiftyfive.cargo.CargoItem;
import com.google.firebase.analytics.FirebaseAnalytics;


public class MainActivity extends AppCompatActivity {

    FirebaseAnalytics mFirebaseAnalytics;
    String username;
    String userMail;
    EditText userText;
    EditText mailAdressText;

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
            username = userText.getText().toString();
            userMail = mailAdressText.getText().toString();
            CheckBox xboxBox = (CheckBox)findViewById(R.id.boxXbox);
            CheckBox playBox = (CheckBox)findViewById(R.id.boxPlaystation);
            CheckBox nintendoBox = (CheckBox)findViewById(R.id.boxNintendo);

            if (username.length() > 0)
                bundle.putString("username", username);
            if (userMail.length() > 0)
                bundle.putString("userEmail", userMail);

            if (xboxBox.isChecked()) {
                revenue += 149.99;
                CargoItem.attachItemToEvent(new CargoItem("xboxBox").setUnitPrice(149.99).setQuantity(1).setAttribute1("attr1TESTforTune"));
            }
            if (playBox.isChecked()) {
                revenue += 199;
                CargoItem.attachItemToEvent(new CargoItem("playBox").setUnitPrice(199).setQuantity(1).setAttribute1("attr1TESTforTune"));
            }
            if (nintendoBox.isChecked()) {
                revenue += 255;
                CargoItem.attachItemToEvent(new CargoItem("nintendoBox").setUnitPrice(255).setQuantity(1).setAttribute1("attr1TESTforTune"));
            }

            bundle.putString("currencyCode", "EUR");
            bundle.putDouble("totalRevenue", revenue);
            bundle.putBoolean("eventItems", true);
            mFirebaseAnalytics.logEvent("tagPurchase", bundle);
        }
    };

    void setupUI() {
        Button tagEventButton = (Button) findViewById(R.id.tagEventButton);
        Button tagScreenButton = (Button) findViewById(R.id.tagScreenButton);
        Button setUserButton = (Button) findViewById(R.id.identifyButton);
        Button tagPurchaseButton = (Button) findViewById(R.id.tagPurchaseButton);

        userText = (EditText)findViewById(R.id.usernameInput);
        mailAdressText = (EditText)findViewById(R.id.mailAdressInput);

        tagEventButton.setOnClickListener(tagEventListener);
        tagScreenButton.setOnClickListener(tagScreenListener);
        setUserButton.setOnClickListener(setUserListener);
        tagPurchaseButton.setOnClickListener(tagPurchaseListener);
    }
}
