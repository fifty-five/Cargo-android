package com.fiftyfive.cargo.handlers;

import android.app.Application;
import android.os.BaseBundle;
import android.os.Bundle;
import android.util.Log;

import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.models.Event;
import com.fiftyfive.cargo.models.User;
import com.google.firebase.analytics.FirebaseAnalytics;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;


/**
 * Created by julien on 19/07/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(FirebaseAnalytics.class)

public class FirebaseHandlerTest extends TestCase {

/* *********************************** Variables declaration ************************************ */

    FirebaseAnalytics fireMock = PowerMockito.mock(FirebaseAnalytics.class);
    FirebaseHandler handler;
    @Mock Application context;
    @Mock Cargo cargo;

/* ***************************************** Test setup ***************************************** */

    public void setUp() throws Exception {
        initMocks(this);
        handler = new FirebaseHandler();
        handler.mFirebaseAnalytics = fireMock;
        handler.cargo = cargo;
    }

    public void tearDown() throws Exception {

    }

/* **************************************** Init Tests ****************************************** */

    public void testInit() {

        HashMap<String, Object> map= new HashMap<>();
        map.put("enableCollection", false);

        handler.execute("Firebase_init", map);

        verify(fireMock, times(1)).setAnalyticsCollectionEnabled(false);
    }

/* ************************************** identify Tests **************************************** */

    public void testSimpleIdentify() {

        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_ID, "randomUserId");

        handler.execute("Firebase_identify", map);

        verify(fireMock, times(1)).setUserId("randomUserId");
    }

    public void testAddUserProperties() {

        HashMap<String, Object> map= new HashMap<>();
        map.put("age", 55);
        map.put("gender", "male");
        map.put("children", 4);

        handler.execute("Firebase_identify", map);

        verify(fireMock, times(1)).setUserProperty("age", "55");
        verify(fireMock, times(1)).setUserProperty("gender", "male");
        verify(fireMock, times(1)).setUserProperty("children", "4");
    }

/* *************************************** tagEvent Tests *************************************** */

    public void testSimpleTagEvent() {
        HashMap<String, Object> map= new HashMap<>();
        map.put(Event.EVENT_NAME, "randomClick");

        handler.execute("Firebase_tagEvent", map);

        verify(fireMock, times(1)).logEvent("randomClick", null);
    }

    public void testTagEventWithParams() throws Exception {

        HashMap<String, Object> map= new HashMap<>();
        map.put(Event.EVENT_NAME, "randomClick");
        map.put(Event.EVENT_TYPE, "click");
        map.put(FirebaseAnalytics.Param.QUANTITY, 5);
        map.put(FirebaseAnalytics.Param.CURRENCY, "USD");

        handler.execute("Firebase_tagEvent", map);

        verify(fireMock, times(1)).logEvent(anyString(), any(Bundle.class));
    }

    public void testFailTagEvent() {
        HashMap<String, Object> map= new HashMap<>();
        map.put(Event.EVENT_ID, 5542);
        map.put(Event.EVENT_TYPE, "click");

        handler.execute("Firebase_tagEvent", map);

        verify(fireMock, times(0)).logEvent(anyString(), any(Bundle.class));
    }

}