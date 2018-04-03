package com.fiftyfive.cargo_android;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.fiftyfive.cargo.CargoLocation;
import com.fiftyfive.cargo.models.Screen;
import com.google.firebase.analytics.FirebaseAnalytics;


public class MainActivity extends AppCompatActivity {

    FirebaseAnalytics mFirebaseAnalytics;
    String username;
    String userMail;
    EditText userText;
    EditText mailAdressText;

    String eventName;
    String screenName;
    EditText eventNameText;
    EditText screenNameText;

    RadioButton optInButton;
    RadioButton optOutButton;
    RadioButton unknownButton;

    String PRIVACY_STATUS = "privacyStatus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle tagScreen = new Bundle();
        tagScreen.putString(Screen.SCREEN_NAME, "MainActivity");
        mFirebaseAnalytics.logEvent("tagScreen", tagScreen);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String shopMessage = data.getStringExtra("shopMessage");
                if (shopMessage != null && shopMessage.length() > 0) {
                    Toast.makeText(this, shopMessage,
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    View.OnClickListener tagEventListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();

            username = userText.getText().toString();
            userMail = mailAdressText.getText().toString();
            eventName = eventNameText.getText().toString();

            if (username.length() > 0) {
                bundle.putString("username", username);
            }
            if (userMail.length() > 0) {
                bundle.putString("userEmail", userMail);
            }
            if (eventName.length() > 0) {
                bundle.putString("eventName", eventName);
            }

            mFirebaseAnalytics.logEvent("tagEvent", bundle);
        }
    };

    View.OnClickListener tagScreenListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle tagScreen = new Bundle();
            screenName = screenNameText.getText().toString();

            if (screenName.length() > 0) {
                tagScreen.putString(Screen.SCREEN_NAME, screenName);
            }
            mFirebaseAnalytics.logEvent("tagScreen", tagScreen);
        }
    };

    View.OnClickListener setUserListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            username = userText.getText().toString();
            userMail = mailAdressText.getText().toString();

            if (username.length() > 0) {
                bundle.putString("username", username);
            }
            if (userMail.length() > 0) {
                bundle.putString("userEmail", userMail);
            }

            mFirebaseAnalytics.logEvent("identify", bundle);
        }
    };

    View.OnClickListener marketListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, MarketActivity.class);
            startActivityForResult(intent, 1);
        }
    };

    View.OnClickListener tagLocationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Location userLocation = new Location("demoApp");
            userLocation.setLongitude(2.294254);
            userLocation.setLatitude(48.858278);
            userLocation.setAltitude(357.5);
            CargoLocation.setLocation(userLocation);
            mFirebaseAnalytics.logEvent("tagLocation", null);
        }
    };

    View.OnClickListener clearQueueListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFirebaseAnalytics.logEvent("clearQueue", null);
        }
    };

    View.OnClickListener sendHitsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFirebaseAnalytics.logEvent("sendQueueHits", null);
        }
    };

    RadioButton.OnCheckedChangeListener optInButtonValueChanged = new RadioButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Bundle privacybundle = new Bundle();
                privacybundle.putString(PRIVACY_STATUS, "OPT_IN");
                mFirebaseAnalytics.logEvent("setPrivacyStatus", privacybundle);
                optOutButton.setChecked(false);
                unknownButton.setChecked(false);
            }
            else if (!optOutButton.isChecked() || !unknownButton.isChecked()){
                optInButton.setActivated(true);
            }
        }
    };

    RadioButton.OnCheckedChangeListener optOutButtonValueChanged = new RadioButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Bundle privacybundle = new Bundle();
                privacybundle.putString(PRIVACY_STATUS, "OPT_OUT");
                mFirebaseAnalytics.logEvent("setPrivacyStatus", privacybundle);
                optInButton.setChecked(false);
                unknownButton.setChecked(false);
            }
            else if (!optInButton.isChecked() || !unknownButton.isChecked()){
                optOutButton.setActivated(true);
            }
        }
    };

    RadioButton.OnCheckedChangeListener unknownButtonValueChanged = new RadioButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Bundle privacybundle = new Bundle();
                privacybundle.putString(PRIVACY_STATUS, "UNKNOWN");
                mFirebaseAnalytics.logEvent("setPrivacyStatus", privacybundle);
                optInButton.setChecked(false);
                optOutButton.setChecked(false);
            }
            else if (!optInButton.isChecked() || !optOutButton.isChecked()){
                unknownButton.setActivated(true);
            }
        }
    };

    void setupUI() {
        Button tagEventButton = findViewById(R.id.tagEventButton);
        tagEventButton.setOnClickListener(tagEventListener);
        Button tagScreenButton = findViewById(R.id.tagScreenButton);
        tagScreenButton.setOnClickListener(tagScreenListener);
        Button setUserButton = findViewById(R.id.identifyButton);
        setUserButton.setOnClickListener(setUserListener);
        Button tagLocation = findViewById(R.id.locationButton);
        tagLocation.setOnClickListener(tagLocationListener);
        Button marketPlace = findViewById(R.id.marketButton);
        marketPlace.setOnClickListener(marketListener);
        Button clearQueue = findViewById(R.id.clearQueue);
        clearQueue.setOnClickListener(clearQueueListener);
        Button sendHits = findViewById(R.id.sendQueueHits);
        sendHits.setOnClickListener(sendHitsListener);

        optInButton = findViewById(R.id.radioButtonOptIn);
        optInButton.setOnCheckedChangeListener(optInButtonValueChanged);
        optOutButton = findViewById(R.id.radioButtonOptOut);
        optOutButton.setOnCheckedChangeListener(optOutButtonValueChanged);
        unknownButton = findViewById(R.id.radioButtonUnknown);
        unknownButton.setOnCheckedChangeListener(unknownButtonValueChanged);

        userText = findViewById(R.id.usernameInput);
        mailAdressText = findViewById(R.id.mailAdressInput);
        screenNameText = findViewById(R.id.screenNameInput);
        eventNameText = findViewById(R.id.eventNameInput);
    }

}
