package com.fiftyfive.cargo_android;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.fiftyfive.cargo.Cargo;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.tune.TuneEventItem;

import java.util.ArrayList;


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

            if (xboxBox.isChecked())
                revenue += 149.99;
            if (playBox.isChecked())
                revenue += 199;
            if (nintendoBox.isChecked())
                revenue += 255;

            bundle.putString("currencyCode", "EUR");
            bundle.putDouble("totalRevenue", revenue);
            mFirebaseAnalytics.logEvent("tagPurchase", bundle);
        }
    };
}
