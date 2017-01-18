package com.fiftyfive.cargo_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fiftyfive.cargo.Cargo;
import com.google.firebase.analytics.FirebaseAnalytics;


/**
 * Created by Julien Gil on 06/12/2016.
 */

public class SplashActivity extends AppCompatActivity {

    FirebaseAnalytics mFirebaseAnalytics;

    Cargo.Handler[] handlerArray = new Cargo.Handler[]{
            Cargo.Handler.AT,
            Cargo.Handler.FB
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // initialize Cargo with the context and the GTM container
        // which has been received in the SplashActivity
        Cargo.init(this.getApplication());

        // Register several handlers at a time with an array of Handler enum.
        Cargo.getInstance().registerHandlers(handlerArray);
        // Register a single handler with a Handler enum
        Cargo.getInstance().registerHandler(Cargo.Handler.TUN);

        mFirebaseAnalytics.logEvent("applicationStart", null);

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // prevent to come back to the splashScreen
    }
}