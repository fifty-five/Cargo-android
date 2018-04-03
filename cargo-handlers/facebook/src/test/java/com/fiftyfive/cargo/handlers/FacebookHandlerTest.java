package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.models.Transaction;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;


/**
 * Created by louis on 04/11/15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({FacebookSdk.class, Cargo.class, AppEventsLogger.class})

public class FacebookHandlerTest extends TestCase {

/* *********************************** Variables declaration ************************************ */

    AppEventsLogger facebookLoggerMock = mock(AppEventsLogger.class);
    FacebookHandler handler;
    @Mock Application context;
    @Mock Cargo cargoMock;

/* ***************************************** Test setup ***************************************** */

    public void setUp() throws Exception {
        super.setUp();
        initMocks(this);
        handler = new FacebookHandler();
        handler.cargo = cargoMock;
        PowerMockito.mockStatic(FacebookSdk.class);
        PowerMockito.mockStatic(Cargo.class);
        PowerMockito.mockStatic(AppEventsLogger.class);
        handler.facebookLogger = facebookLoggerMock;
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

/* **************************************** Init Tests ****************************************** */

    public void testInitialize(){
        handler.initialize();

        assertEquals(true, handler.valid);
    }

    public void testInitWithAllParameters(){
        PowerMockito.when(Cargo.getInstance()).thenReturn(cargoMock);
        when(cargoMock.getAppContext()).thenReturn(context);
        when(cargoMock.getAppContext().getApplicationContext()).thenReturn(context);

        HashMap<String, Object> map= new HashMap<>();
        map.put("applicationId", 1.01234567891011E14);

        handler.execute("FB_init", map);

        verifyStatic(FacebookSdk.class, Mockito.times(1));
        FacebookSdk.sdkInitialize(context);

        verifyStatic(AppEventsLogger.class, Mockito.times(1));
        AppEventsLogger.newLogger(context);

        verifyStatic(FacebookSdk.class, Mockito.times(1));
        FacebookSdk.setApplicationId("101234567891011");
    }

    public void testInitNoParam(){
        PowerMockito.when(Cargo.getInstance()).thenReturn(cargoMock);
        when(cargoMock.getAppContext()).thenReturn(context);
        when(cargoMock.getAppContext().getApplicationContext()).thenReturn(context);

        HashMap<String, Object> map= new HashMap<>();

        handler.execute("FB_init", map);

        verifyStatic(FacebookSdk.class, Mockito.times(0));
        FacebookSdk.sdkInitialize(context);

        verifyStatic(AppEventsLogger.class, Mockito.times(0));
        AppEventsLogger.newLogger(context);

        verifyStatic(FacebookSdk.class, Mockito.times(0));
        FacebookSdk.setApplicationId(anyString());
    }

    public void testSetEnableDebug(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("enableDebug", "true");

        handler.execute("FB_init", map);

        verifyStatic(FacebookSdk.class, Mockito.times(1));
        FacebookSdk.setIsDebugEnabled(true);
    }

    public void testMissingInit(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventName", "hello");

        handler.execute("FB_tagEvent", map);

        verify(facebookLoggerMock, times(0)).logEvent("hello");
    }

    public void testWrongMethod(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventName", "hello");

        handler.setInitialized(true);
        handler.execute("FB_tagNothingAtAll", map);

        verifyNoMoreInteractions(facebookLoggerMock);
    }

/* *************************************** tagEvent Tests *************************************** */

    public void testSimpleTagEvent(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventName", "hello");

        handler.setInitialized(true);
        handler.execute("FB_tagEvent", map);

        verify(facebookLoggerMock, times(1)).logEvent("hello");
    }

    public void testTagEventWithVTS(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventName", "hello");
        map.put("valueToSum", 55.42);

        handler.setInitialized(true);
        handler.execute("FB_tagEvent", map);

        verify(facebookLoggerMock, times(1)).logEvent("hello", 55.42);
    }

    public void testTagEventWithVTSAndParams(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventName", "addToCart");
        map.put("valueToSum", 42.0);
        map.put("itemName", "Power Ball");
        map.put("itemId", 5542);

        handler.setInitialized(true);
        handler.execute("FB_tagEvent", map);

        verify(facebookLoggerMock, times(1)).logEvent(anyString(), anyDouble(), any(Bundle.class));
    }

    public void testTagEventWithParams(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventName", "addToCart");
        map.put("itemName", "Power Ball");
        map.put("onSale", true);
        map.put("itemId", 5542);

        handler.setInitialized(true);
        handler.execute("FB_tagEvent", map);

        verify(facebookLoggerMock, times(1)).logEvent(anyString(), any(Bundle.class));
    }

    public void testTagEventWithMoreParams(){
        HashMap<String, Object> map= new HashMap<>();
        Bundle testBundle = new Bundle();
        testBundle.putFloat("someFloat", 0.1f);

        map.put("eventName", "addToCart");
        map.put("itemName", "Power Ball");
        map.put("itemId", 5542);
        map.put("onSale", false);
        map.put("randomParam", testBundle);

        handler.setInitialized(true);
        handler.execute("FB_tagEvent", map);

        verify(facebookLoggerMock, times(1)).logEvent(anyString(), any(Bundle.class));
    }

    public void testTagEventNoParam(){
        HashMap<String, Object> map= new HashMap<>();

        handler.setInitialized(true);
        handler.execute("FB_tagEvent", map);

        verifyNoMoreInteractions(facebookLoggerMock);
    }

/* ************************************** tagPurchase Tests ************************************* */

    public void testTagPurchase(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(Transaction.TRANSACTION_TOTAL, 42.5);
        map.put(Transaction.TRANSACTION_CURRENCY_CODE, "USD");

        handler.setInitialized(true);
        handler.execute("FB_tagPurchase", map);

        verify(facebookLoggerMock, times(1)).logPurchase(BigDecimal.valueOf(42.5), Currency.getInstance("USD"));
    }

    public void testFailedPurchase(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(Transaction.TRANSACTION_TOTAL, 42.5);

        handler.setInitialized(true);
        handler.execute("FB_tagPurchase", map);

        verifyNoMoreInteractions(facebookLoggerMock);
    }

    public void testFailedPurchase2(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(Transaction.TRANSACTION_CURRENCY_CODE, "USD");

        handler.setInitialized(true);
        handler.execute("FB_tagPurchase", map);

        verifyNoMoreInteractions(facebookLoggerMock);
    }

    public void testFailedPurchase3(){
        HashMap<String, Object> map= new HashMap<>();

        handler.setInitialized(true);
        handler.execute("FB_tagPurchase", map);

        verifyNoMoreInteractions(facebookLoggerMock);
    }

/* *************************************** Activities Tests ************************************* */

    public void testOnActivityStarted(){
        Activity testActivity = new Activity();
        handler.onActivityStarted(testActivity);

        verifyNoMoreInteractions(facebookLoggerMock);
    }

    public void testOnActivityResumedFail(){
        Activity testActivity = new Activity();
        handler.onActivityResumed(testActivity);

        verifyNoMoreInteractions(facebookLoggerMock);
    }

    public void testOnActivityResumed(){
        Activity testActivity = new Activity();
        handler.setInitialized(true);
        handler.onActivityResumed(testActivity);

        verifyStatic(AppEventsLogger.class, Mockito.times(1));
        AppEventsLogger.activateApp(any(Activity.class));
    }

    public void testOnActivityPausedFail(){
        Activity testActivity = new Activity();
        handler.onActivityPaused(testActivity);

        verifyNoMoreInteractions(facebookLoggerMock);
    }

    public void testOnActivityPaused(){
        Activity testActivity = new Activity();
        handler.setInitialized(true);
        handler.onActivityPaused(testActivity);

        verifyStatic(AppEventsLogger.class, Mockito.times(1));
        AppEventsLogger.deactivateApp(any(Activity.class));
    }

    public void testOnActivityStopped(){
        Activity testActivity = new Activity();
        handler.onActivityStopped(testActivity);

        verifyNoMoreInteractions(facebookLoggerMock);
    }

}