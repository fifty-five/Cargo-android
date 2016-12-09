package com.fiftyfive.cargo.handlers;

import android.app.Application;

import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.models.Event;
import com.fiftyfive.cargo.models.Screen;
import com.fiftyfive.cargo.models.Tracker;
import com.fiftyfive.cargo.models.User;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by dali on 25/11/15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({GoogleAnalytics.class, GoogleAnalyticsHandler.class})
public class GoogleAnalyticsHandlerTest extends TestCase {

/* *********************************** Variables declaration ************************************ */

    GoogleAnalytics googleAnalyticsMock = mock(GoogleAnalytics.class);
    EventBuilderGA eventMock = mock(EventBuilderGA.class);

    com.google.android.gms.analytics.Tracker trackerMock = mock(
            com.google.android.gms.analytics.Tracker.class);
    GoogleAnalyticsHandler handler;
    @Mock
    Application context;
    @Mock
    Cargo cargo;

/* ***************************************** Test setup ***************************************** */

    public void setUp() throws Exception {
        initMocks(this);

        when(cargo.getApplication()).thenReturn(context);
        PowerMockito.whenNew(EventBuilderGA.class).withNoArguments().thenReturn(eventMock);
        when(eventMock.getEvent()).thenReturn(new HitBuilders.EventBuilder().setAction("").setCategory(""));

        handler = new GoogleAnalyticsHandler();
        handler.cargo = cargo;
        handler.analytics = googleAnalyticsMock;
        handler.tracker = trackerMock;
    }

    public void tearDown() throws Exception {

    }

/* **************************************** Init Tests ****************************************** */

    public void testInit(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("applicationId", "UA-12345678-0");
        handler.execute("GA_init", map);

        verify(googleAnalyticsMock, times(1)).newTracker("UA-12345678-0");
        assertEquals(true, handler.isInitialized());
    }

    public void testFailInitParameter(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("applicationId", null);
        handler.execute("GA_init", map);

        verify(googleAnalyticsMock, times(0)).newTracker(anyString());
        assertEquals(false, handler.isInitialized());
    }

    public void testFailInitFormat(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("applicationId", "12345678-0");
        handler.execute("GA_init", map);

        verify(googleAnalyticsMock, times(0)).newTracker(anyString());
        assertEquals(false, handler.isInitialized());
    }

/* **************************************** Set Tests ******************************************* */

    public void testSetWithoutInit(){
        HashMap<String, Object> map= new HashMap<>();

        handler.execute("GA_set", map);

        verify(googleAnalyticsMock, times(0)).setAppOptOut(anyBoolean());
        verify(googleAnalyticsMock, times(0)).setDryRun(anyBoolean());
        verify(googleAnalyticsMock, times(0)).setLocalDispatchPeriod(anyInt());
        verify(googleAnalyticsMock, times(0)).enableAdvertisingIdCollection(anyBoolean());
    }

    public void testSetWithoutParam(){
        HashMap<String, Object> map= new HashMap<>();

        handler.setInitialized(true);
        handler.execute("GA_set", map);

        verify(googleAnalyticsMock, times(1)).setAppOptOut(false);
        verify(googleAnalyticsMock, times(1)).setDryRun(false);
        verify(googleAnalyticsMock, times(1)).setLocalDispatchPeriod(30);
        verify(googleAnalyticsMock, times(1)).enableAdvertisingIdCollection(true);
    }

    public void testSetWithParams(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(Tracker.ENABLE_OPT_OUT, true);
        map.put(Tracker.DISABLE_TRACKING, true);
        map.put(Tracker.DISPATCH_INTERVAL, 55);
        map.put("allowIdfaCollection", false);

        handler.setInitialized(true);
        handler.execute("GA_set", map);

        verify(googleAnalyticsMock, times(1)).setAppOptOut(true);
        verify(googleAnalyticsMock, times(1)).setDryRun(true);
        verify(googleAnalyticsMock, times(1)).setLocalDispatchPeriod(55);
        verify(googleAnalyticsMock, times(1)).enableAdvertisingIdCollection(false);
    }

/* **************************************** Set Tests ******************************************* */

    public void testIdentify(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_ID, "userId");

        handler.setInitialized(true);
        handler.execute("GA_identify", map);

        verify(trackerMock, times(1)).set("&uid", "userId");
    }

    public void testIdentifyMissingParam(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(Tracker.ENABLE_OPT_OUT, true);
        map.put(Tracker.DISABLE_TRACKING, true);

        handler.setInitialized(true);
        handler.execute("GA_identify", map);

        verify(trackerMock, times(0)).set(anyString(), anyString());
    }

/* ************************************* TagScreen Tests **************************************** */

    public void testTagScreen(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(Screen.SCREEN_NAME, "main menu");

        handler.setInitialized(true);
        handler.execute("GA_tagScreen", map);

        verify(trackerMock, times(1)).setScreenName("main menu");
    }

    public void testFailedTagScreen(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(Event.EVENT_NAME, "main menu");

        handler.setInitialized(true);
        handler.execute("GA_tagScreen", map);

        verify(trackerMock, times(0)).setScreenName(anyString());
    }

/* ************************************* TagScreen Tests **************************************** */

    public void testTagEvent() throws Exception {
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventAction", "action");
        map.put("eventCategory", "category");

        handler.setInitialized(true);
        handler.execute("GA_tagEvent", map);

        PowerMockito.verifyNew(EventBuilderGA.class).withNoArguments();
        verify(eventMock, times(1)).setAction("action");
        verify(eventMock, times(1)).setCategory("category");
        verify(eventMock, times(0)).setLabel(anyString());
        verify(eventMock, times(0)).setValue(anyLong());
        verify(eventMock, times(0)).setNonInteraction(anyBoolean());
        verify(eventMock).getEvent();
    }

    public void testTagEventLabel() throws Exception {
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventAction", "action");
        map.put("eventCategory", "category");
        map.put("eventLabel", "label");

        handler.setInitialized(true);
        handler.execute("GA_tagEvent", map);

        PowerMockito.verifyNew(EventBuilderGA.class).withNoArguments();
        verify(eventMock, times(1)).setAction("action");
        verify(eventMock, times(1)).setCategory("category");
        verify(eventMock, times(1)).setLabel("label");
        verify(eventMock, times(0)).setValue(anyLong());
        verify(eventMock, times(0)).setNonInteraction(anyBoolean());
        verify(eventMock).getEvent();
    }

    public void testTagEventValue() throws Exception {
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventAction", "action");
        map.put("eventCategory", "category");
        map.put("eventValue", (long)125);

        handler.setInitialized(true);
        handler.execute("GA_tagEvent", map);

        PowerMockito.verifyNew(EventBuilderGA.class).withNoArguments();
        verify(eventMock, times(1)).setAction("action");
        verify(eventMock, times(1)).setCategory("category");
        verify(eventMock, times(0)).setLabel(anyString());
        verify(eventMock, times(1)).setValue((long)125);
        verify(eventMock, times(0)).setNonInteraction(anyBoolean());
        verify(eventMock).getEvent();
    }

    public void testTagEventLabelValue() throws Exception {
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventAction", "action");
        map.put("eventCategory", "category");
        map.put("eventLabel", "label");
        map.put("eventValue", (long)125);

        handler.setInitialized(true);
        handler.execute("GA_tagEvent", map);

        PowerMockito.verifyNew(EventBuilderGA.class).withNoArguments();
        verify(eventMock, times(1)).setAction("action");
        verify(eventMock, times(1)).setCategory("category");
        verify(eventMock, times(1)).setLabel("label");
        verify(eventMock, times(1)).setValue((long)125);
        verify(eventMock, times(0)).setNonInteraction(anyBoolean());
        verify(eventMock).getEvent();
    }

    public void testTagEventNonInteraction() throws Exception {
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventAction", "action");
        map.put("eventCategory", "category");
        map.put("setNonInteraction", true);

        handler.setInitialized(true);
        handler.execute("GA_tagEvent", map);

        PowerMockito.verifyNew(EventBuilderGA.class).withNoArguments();
        verify(eventMock, times(1)).setAction("action");
        verify(eventMock, times(1)).setCategory("category");
        verify(eventMock, times(0)).setLabel(anyString());
        verify(eventMock, times(0)).setValue(anyLong());
        verify(eventMock, times(1)).setNonInteraction(true);
        verify(eventMock).getEvent();
    }

    public void testAllCaseTagEvent() throws Exception {
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventAction", "action");
        map.put("eventCategory", "category");
        map.put("eventLabel", "label");
        map.put("eventValue", (long)125);
        map.put("setNonInteraction", true);

        handler.setInitialized(true);
        handler.execute("GA_tagEvent", map);

        PowerMockito.verifyNew(EventBuilderGA.class).withNoArguments();
        verify(eventMock, times(1)).setAction("action");
        verify(eventMock, times(1)).setCategory("category");
        verify(eventMock, times(1)).setLabel("label");
        verify(eventMock, times(1)).setValue((long)125);
        verify(eventMock, times(1)).setNonInteraction(true);
        verify(eventMock).getEvent();
    }

    public void testFailTagEvent() throws Exception {
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventAction", "action");
        map.put("eventLabel", "category");

        handler.setInitialized(true);
        handler.execute("GA_tagEvent", map);

        PowerMockito.verifyZeroInteractions(eventMock);
    }
}