package com.fiftyfive.cargo_android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.fiftyfive.cargo.Cargo;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    DataLayer dataLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Cargo.init(this.getApplication(), ContainerHolderSingleton.getContainerHolder().getContainer());
        Cargo.getInstance().registerHandlers();
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
