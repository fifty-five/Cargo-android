package com.fiftyfive.cargo.handlers;

import android.app.Application;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.models.Transaction;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.mockito.Mock;
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
        initMocks(this);
        handler = new FacebookHandler();
        PowerMockito.mockStatic(FacebookSdk.class);
        PowerMockito.mockStatic(Cargo.class);
        PowerMockito.mockStatic(AppEventsLogger.class);
        handler.facebookLogger = facebookLoggerMock;
    }

    public void tearDown() throws Exception {

    }

/* **************************************** Init Tests ****************************************** */

    public void testInitialize(){
        PowerMockito.when(Cargo.getInstance()).thenReturn(cargoMock);
        when(cargoMock.getApplication()).thenReturn(context);

        handler.initialize();

        verifyStatic();
        Cargo.getInstance();

        verifyStatic();
        FacebookSdk.sdkInitialize(context);

        verifyStatic();
        AppEventsLogger.newLogger(context);
    }

    public void testInitWithAllParameters(){

        HashMap<String, Object> map= new HashMap<>();
        map.put("applicationId", 123);

        handler.execute("FB_init", map);

        verifyStatic();
        FacebookSdk.setApplicationId("123");
    }

    public void testFailedInit(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventName", "hello");

        handler.execute("FB_tagEvent", map);

        verify(facebookLoggerMock, times(0)).logEvent("hello");
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
        map.put("itemId", 5542);

        handler.setInitialized(true);
        handler.execute("FB_tagEvent", map);

        verify(facebookLoggerMock, times(1)).logEvent(anyString(), any(Bundle.class));
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

        verify(facebookLoggerMock, times(0)).logPurchase(any(BigDecimal.class), any(Currency.class));
    }

    public void testSetEnableDebug(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("enableDebug", "true");

        handler.execute("FB_init", map);
        verifyStatic();
        FacebookSdk.setIsDebugEnabled(true);
    }

}