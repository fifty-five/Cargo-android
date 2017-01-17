package com.fiftyfive.cargo_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fiftyfive.cargo.Cargo;
import com.google.firebase.analytics.FirebaseAnalytics;


public class MainActivity extends AppCompatActivity {

    FirebaseAnalytics mFirebaseAnalytics;

    Cargo.Handler[] handlerArray = new Cargo.Handler[]{
            Cargo.Handler.AT,
            Cargo.Handler.FB,
            Cargo.Handler.FIR,
            Cargo.Handler.GA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // initialize Cargo with the context and the GTM container
        // which has been received in the SplashActivity
        Cargo.init(this.getApplication());

//        // Register several handlers at a time with an array of Handler enum.
//        Cargo.getInstance().registerHandlers(handlerArray);
        // Register a single handler with a Handler enum
        Cargo.getInstance().registerHandler(Cargo.Handler.FB);

        Button tagEventButton = (Button) findViewById(R.id.tagEventButton);
        Button tagScreenButton = (Button) findViewById(R.id.tagScreenButton);
        Button setUserButton = (Button) findViewById(R.id.identifyButton);
        Button tagPurchaseButton = (Button) findViewById(R.id.tagPurchaseButton);
        tagEventButton.setOnClickListener(tagEventListener);
        tagScreenButton.setOnClickListener(tagScreenListener);
        setUserButton.setOnClickListener(setUserListener);
        tagPurchaseButton.setOnClickListener(tagPurchaseListener);
        mFirebaseAnalytics.logEvent("applicationStart", null);
    }

    View.OnClickListener tagEventListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFirebaseAnalytics.logEvent("tagEvent", null);
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
            mFirebaseAnalytics.logEvent("identify", null);
        }
    };

    View.OnClickListener tagPurchaseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFirebaseAnalytics.logEvent("tagPurchase", null);
        }
    };
}
