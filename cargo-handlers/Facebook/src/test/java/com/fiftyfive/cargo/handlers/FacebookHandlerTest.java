package com.fiftyfive.cargo.handlers;

import android.app.Application;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.fiftyfive.cargo.Cargo;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import static com.fiftyfive.cargo.ModelsUtils.getBoolean;
import static com.fiftyfive.cargo.ModelsUtils.getInt;
import static com.fiftyfive.cargo.ModelsUtils.getString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
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
@PrepareForTest({FacebookSdk.class, Cargo.class, AppEventsLogger.class, Bundle.class})

public class FacebookHandlerTest extends TestCase {

    AppEventsLogger facebookLoggerMock = mock(AppEventsLogger.class);
    Bundle bundleMock = PowerMockito.mock(Bundle.class);
    FacebookHandler handler;
    @Mock Application context;
    @Mock Cargo cargoMock;

    public void setUp() throws Exception {
        initMocks(this);
        handler = new FacebookHandler();
        PowerMockito.mockStatic(FacebookSdk.class);
        PowerMockito.mockStatic(Cargo.class);
        PowerMockito.mockStatic(AppEventsLogger.class);
        handler.facebookLogger = facebookLoggerMock;
    }


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

    public void testSimpleTagEvent(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventName", "hello");

        handler.execute("FB_tagEvent", map);

        verify(facebookLoggerMock, times(1)).logEvent("hello");
    }

    public void testTagEventWithVTS(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventName", "hello");
        map.put("valueToSum", 55.42);

        handler.execute("FB_tagEvent", map);

        verify(facebookLoggerMock, times(1)).logEvent("hello", 55.42);
    }

    public void testTagEventWithVTSAndParams(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventName", "addToCart");
        map.put("valueToSum", 42.0);
        map.put("itemName", "Power Ball");
        map.put("itemId", 5542);

        handler.execute("FB_tagEvent", map);

        verify(facebookLoggerMock, times(1)).logEvent(anyString(), anyDouble(), any(Bundle.class));
    }

    public void testTagEventWithParams(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventName", "addToCart");
        map.put("itemName", "Power Ball");
        map.put("itemId", 5542);

        handler.execute("FB_tagEvent", map);

        verify(facebookLoggerMock, times(1)).logEvent(anyString(), any(Bundle.class));
    }

    public void testTagPurchase(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("cartPrice", 42.5);
        map.put("currencyCode", "USD");

        handler.execute("FB_purchase", map);

        verify(facebookLoggerMock, times(1)).logPurchase(BigDecimal.valueOf(42.5), Currency.getInstance("USD"));
    }

    public void testSetEnableDebug(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("enableDebug", "true");

        handler.execute("FB_init", map);
        verifyStatic();
        FacebookSdk.setIsDebugEnabled(true);
    }

    public void testEventParamBuilder() throws Exception {
        PowerMockito.whenNew(Bundle.class).withAnyArguments().thenReturn(bundleMock);

        HashMap<String, Object> map = new HashMap<>();
        map.put("test0", false);
        map.put("test1", true);
        map.put("test2", 2);
        map.put("test3", "test3");

        handler.eventParamBuilder(map);

        verify(bundleMock, times(3)).putInt(anyString(), anyInt());
        verify(bundleMock).putString("test3", "test3");
    }


    public void tearDown() throws Exception {

    }
}