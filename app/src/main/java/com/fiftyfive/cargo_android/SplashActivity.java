package com.fiftyfive.cargo_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;


import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.TagManager;

import java.util.concurrent.TimeUnit;


/**
 * Created by Julien Gil on 06/12/2016.
 */

public class SplashActivity extends AppCompatActivity {

    int SPLASH_TIME_OUT = 750;
    String CONTAINER_ID = "GTM-WKZPWJ5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TagManager tagManager = TagManager.getInstance(this);

        // Modify the log level of the logger to print out not only
        // warning and error messages, but also verbose, debug, info messages.
        tagManager.setVerboseLoggingEnabled(true);

        // Calls an API method in google play services, the result object is stored in a PendingResult
        // The ContainerHolder will be available from the returned PendingResult as soon as one of the following happens:
        // -> a saved container is loaded, or if there is no saved container,
        // -> a network container is loaded or a network error occurs, or
        // -> a timeout occurs
        PendingResult<ContainerHolder> pending =
                tagManager.loadContainerPreferNonDefault(CONTAINER_ID, R.raw.gtm_container);

        // The onResult method will be called as soon as one of the following happens: (callBack)
        //     1. a saved container is loaded
        //     2. if there is no saved container, a network container is loaded
        //     3. the request times out. The example below uses a constant to manage the timeout period.
        pending.setResultCallback(new ResultCallback<ContainerHolder>() {
            @Override
            // onResult() is called when the result is ready
            public void onResult(ContainerHolder containerHolder) {
                // Sets the container in the ContainerHolderSingleton
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                // returns if unsuccessful
                if (!containerHolder.getStatus().isSuccess()) {
                    displayErrorToUser();
                    return;
                }

                // manually refresh the container for the demo (can be done each 15min or no-op)
                containerHolder.refresh();
                ContainerHolderSingleton.setContainerHolder(containerHolder);

                delayed();
            }
        }, 5, TimeUnit.SECONDS);
    }

    /**
     * When the time set on SPLASH_TIME_OUT is over, launch this method
     * Is used in order to let some time for the 55 logo to be displayed
     */
    private void delayed(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // prevent to come back to the splashScreen
            }
        }, SPLASH_TIME_OUT);
    }

    /**
     * display an error on the screen if some happens
     */
    private void displayErrorToUser() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("An error has occurred");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
                "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialog.show();
    }
}