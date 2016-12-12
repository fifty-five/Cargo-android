package com.fiftyfive.cargo_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fiftyfive.cargo.Cargo;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    DataLayer dataLayer;
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

        // initialize Cargo with the context and the GTM container
        // which has been received in the SplashActivity
        Cargo.init(this.getApplication(), ContainerHolderSingleton.getContainerHolder().getContainer());

        // Register several handlers at a time with an array of Handler enum.
        Cargo.getInstance().registerHandlers(handlerArray);
        // Register a single handler with a Handler enum
        Cargo.getInstance().registerHandler(Cargo.Handler.TUN);

        // Retrieve the datalayer in order to send an event which will trigger
        // all the registered handler's initialization method with required parameters.
        dataLayer = TagManager.getInstance(this).getDataLayer();
        dataLayer.pushEvent("applicationStart", new HashMap<String, Object>());

        Button tagEventButton = (Button) findViewById(R.id.tagEventButton);
        Button tagScreenButton = (Button) findViewById(R.id.tagScreenButton);
        Button setUserButton = (Button) findViewById(R.id.identifyButton);
        Button tagPurchaseButton = (Button) findViewById(R.id.tagPurchaseButton);
        tagEventButton.setOnClickListener(tagEventListener);
        tagScreenButton.setOnClickListener(tagScreenListener);
        setUserButton.setOnClickListener(setUserListener);
        tagPurchaseButton.setOnClickListener(tagPurchaseListener);

    }

    View.OnClickListener tagEventListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dataLayer.pushEvent("tagEvent", new HashMap<String, Object>());
        }
    };

    View.OnClickListener tagScreenListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dataLayer.pushEvent("openScreen", new HashMap<String, Object>());
        }
    };

    View.OnClickListener setUserListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dataLayer.pushEvent("identify", new HashMap<String, Object>());
        }
    };

    View.OnClickListener tagPurchaseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dataLayer.pushEvent("tagPurchase", new HashMap<String, Object>());
        }
    };
}
