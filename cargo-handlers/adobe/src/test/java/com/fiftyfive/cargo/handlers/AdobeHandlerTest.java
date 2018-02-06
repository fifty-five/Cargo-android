package com.fiftyfive.cargo.handlers;

import android.app.Activity;
import android.app.Application;
import android.location.Location;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.adobe.mobile.MobilePrivacyStatus;
import com.fiftyfive.cargo.Cargo;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;


/**
 * Created by Julien Gil on 31/01/2018.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Cargo.class, Analytics.class, Config.class, Location.class, AdobeHandler.class})

public class AdobeHandlerTest extends TestCase {

    AdobeHandler handler;
    @Mock Application context;
    @Mock Cargo cargoMock;
    @Mock Location locationMock;

    public void setUp() throws Exception {
        super.setUp();
        initMocks(this);
        handler = new AdobeHandler();
        handler.cargo = cargoMock;
        PowerMockito.mockStatic(Analytics.class);
        PowerMockito.mockStatic(Config.class);
        PowerMockito.mockStatic(Cargo.class);
        PowerMockito.mockStatic(Location.class);
        PowerMockito.when(Cargo.getInstance()).thenReturn(cargoMock);
        PowerMockito.whenNew(Location.class).withAnyArguments().thenReturn(locationMock);
        when(cargoMock.getAppContext()).thenReturn(context);
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

/* **************************************** Init Tests ****************************************** */

    public void testInitialize(){
        handler.initialize();

        assertEquals(true, handler.valid);
        assertEquals(true, handler.isInitialized());
    }

    public void testForCoverage() {
        HashMap<String, Object> map = new HashMap<>();

        handler.execute("ADB_trackLocation", map);
        verifyNoMoreInteractions(Location.class);
        verifyNoMoreInteractions(Config.class);
        verifyNoMoreInteractions(Analytics.class);

        handler.setInitialized(true);
        handler.execute("ADB_pouet", map);
        verifyNoMoreInteractions(Location.class);
        verifyNoMoreInteractions(Config.class);
        verifyNoMoreInteractions(Analytics.class);
    }

/* *************************************** tagEvent Tests *************************************** */

    public void testSimpleTagEvent(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("eventName", "hello");

        handler.setInitialized(true);
        handler.execute("ADB_tagEvent", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackAction("hello", null);
    }

    public void testTagEventWithContextData(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("eventName", "hello");
        map.put("eventType", "purchase");
        map.put("revenue", 132.24);

        handler.setInitialized(true);
        handler.execute("ADB_tagEvent", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackAction(anyString(), any(HashMap.class));
    }

    public void testFailTagEvent(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("randomParam", "random");

        handler.setInitialized(true);
        handler.execute("ADB_tagEvent", map);

        verifyStatic(Analytics.class, Mockito.times(0));
        Analytics.trackAction(anyString(), any(HashMap.class));
    }

/* *************************************** tagScreen Tests ************************************** */

    public void testSimpleTagScreen(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("screenName", "viewBasket");

        handler.setInitialized(true);
        handler.execute("ADB_tagScreen", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackState("viewBasket", null);
    }

    public void testTagScreenWithContextData(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("screenName", "viewBasket");
        map.put("level", "2");
        map.put("revenue", 132.24);

        handler.setInitialized(true);
        handler.execute("ADB_tagScreen", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackState(anyString(), any(HashMap.class));
    }

    public void testFailTagScreen(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("randomParam", "random");

        handler.setInitialized(true);
        handler.execute("ADB_tagScreen", map);

        verifyStatic(Analytics.class, Mockito.times(0));
        Analytics.trackState(anyString(), any(HashMap.class));
    }

/* ************************************ trackLocation Tests ************************************* */

    public void testSimpleTrackLocation(){
        HashMap<String, Object> map = new HashMap<>();

        handler.setInitialized(true);
        handler.execute("ADB_trackLocation", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackLocation(locationMock, null);
    }

    public void testTrackLocationWithContext(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("reason", "visitPhysicalStore");
        map.put("moneySpent", 12.50);

        handler.setInitialized(true);
        handler.execute("ADB_trackLocation", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackLocation(any(Location.class), any(HashMap.class));
    }

/* ************************************** trackTime Tests *************************************** */

/* *** Start *** */
    public void testSimpleTrackTimeStart(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("actionName", "watchAd");

        handler.setInitialized(true);
        handler.execute("ADB_trackTimeStart", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackTimedActionStart("watchAd", null);
    }

    public void testTrackTimeStartWithContext(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("actionName", "watchAd");
        map.put("adBrand", "Peugeot");
        map.put("time", 66);

        handler.setInitialized(true);
        handler.execute("ADB_trackTimeStart", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackTimedActionStart(anyString(), any(HashMap.class));
    }

    public void testFailTrackTimeStart(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("randomParam", "random");

        handler.setInitialized(true);
        handler.execute("ADB_trackTimeStart", map);

        verifyStatic(Analytics.class, Mockito.times(0));
        Analytics.trackTimedActionStart(anyString(), any(HashMap.class));
    }

/* *** Update *** */
    public void testSimpleTrackTimeUpdate(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("actionName", "watchAd");

        handler.setInitialized(true);
        handler.execute("ADB_trackTimeStart", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackTimedActionStart("watchAd", null);

        map = new HashMap<>();
        map.put("actionName", "watchAd");

        handler.execute("ADB_trackTimeUpdate", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackTimedActionUpdate("watchAd", null);
    }

    public void testTrackTimeUpdateWithContext(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("actionName", "watchAd");

        handler.setInitialized(true);
        handler.execute("ADB_trackTimeStart", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackTimedActionStart("watchAd", null);

        map = new HashMap<>();
        map.put("actionName", "watchAd");
        map.put("adBrand", "Peugeot");
        map.put("time", 66);

        handler.execute("ADB_trackTimeUpdate", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackTimedActionUpdate(anyString(), any(HashMap.class));
    }

    public void testFailTrackTimeUpdate(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("actionName", "watchAd");

        handler.setInitialized(true);
        handler.execute("ADB_trackTimeStart", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackTimedActionStart("watchAd", null);

        map = new HashMap<>();
        map.put("adBrand", "Peugeot");
        map.put("time", 66);

        handler.execute("ADB_trackTimeUpdate", map);

        verifyStatic(Analytics.class, Mockito.times(0));
        Analytics.trackTimedActionUpdate(anyString(), any(HashMap.class));
    }

/* *** End *** */
    // TODO: check whether it is possible to get the return value from the call method
    public void testSimpleTrackTimeEnd(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("actionName", "watchAd");

        handler.setInitialized(true);
        handler.execute("ADB_trackTimeStart", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackTimedActionStart("watchAd", null);



        map = new HashMap<>();
        map.put("actionName", "watchAd");

        handler.execute("ADB_trackTimeEnd", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackTimedActionEnd(anyString(), any(Analytics.TimedActionBlock.class));
    }

    public void testTrackTimeEndWithContext(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("actionName", "watchAd");

        handler.setInitialized(true);
        handler.execute("ADB_trackTimeStart", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackTimedActionStart("watchAd", null);



        map = new HashMap<>();
        map.put("actionName", "watchAd");
        map.put("adBrand", "Peugeot");
        map.put("time", 66);

        handler.execute("ADB_trackTimeEnd", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackTimedActionEnd(anyString(), any(Analytics.TimedActionBlock.class));
    }

    public void testFailTrackTimeEnd(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("actionName", "watchAd");

        handler.setInitialized(true);
        handler.execute("ADB_trackTimeStart", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackTimedActionStart("watchAd", null);



        map = new HashMap<>();
        map.put("adBrand", "Peugeot");
        map.put("time", 66);

        handler.execute("ADB_trackTimeEnd", map);

        verifyStatic(Analytics.class, Mockito.times(0));
        Analytics.trackTimedActionEnd(anyString(), any(Analytics.TimedActionBlock.class));
    }

/* ***************************** increaseVisitorLifetimeValue Tests ***************************** */

    public void testSimpleIncreaseVisitorLifetimeValue() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("additionalLifetimeValue", 15.32);

        handler.setInitialized(true);
        handler.execute("ADB_increaseLifetimeValue", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackLifetimeValueIncrease(BigDecimal.valueOf(15.32), null);
    }

    public void testSimpleIncreaseVisitorLifetimeValueWithStringParam() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("additionalLifetimeValue", "15.32");

        handler.setInitialized(true);
        handler.execute("ADB_increaseLifetimeValue", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackLifetimeValueIncrease(BigDecimal.valueOf(15.32), null);
    }

    public void testIncreaseVisitorLifetimeValueWithContext() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("additionalLifetimeValue", 15.32);
        map.put("someParam1", 15.32);
        map.put("someParam2", "55 is da best data agency");

        handler.setInitialized(true);
        handler.execute("ADB_increaseLifetimeValue", map);

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.trackLifetimeValueIncrease(any(BigDecimal.class), any(HashMap.class));
    }

    public void testFailIncreaseVisitorLifetimeValueNoMandatoryParam() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("someParam1", 15.32);
        map.put("someParam2", "55 is da best data agency");

        handler.setInitialized(true);
        handler.execute("ADB_increaseLifetimeValue", map);

        verifyStatic(Analytics.class, Mockito.times(0));
        Analytics.trackLifetimeValueIncrease(any(BigDecimal.class), any(HashMap.class));
    }

    public void testFailIncreaseVisitorLifetimeValueNegativeMandatoryParam() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("additionalLifetimeValue", -15.32);
        map.put("someParam1", 15.32);
        map.put("someParam2", "55 is da best data agency");

        handler.setInitialized(true);
        handler.execute("ADB_increaseLifetimeValue", map);

        verifyStatic(Analytics.class, Mockito.times(0));
        Analytics.trackLifetimeValueIncrease(any(BigDecimal.class), any(HashMap.class));
    }

/* ************************************* setPrivacy Tests *************************************** */

    public void testSimpleOptOutSetPrivacy() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("privacyStatus", "OPT_OUT");

        handler.setInitialized(true);
        handler.execute("ADB_setPrivacyStatus", map);

        verifyStatic(Config.class, Mockito.times(1));
        Config.setPrivacyStatus(MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_OPT_OUT);
    }

    public void testSimpleOptInSetPrivacy() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("privacyStatus", "OPT_IN");

        handler.setInitialized(true);
        handler.execute("ADB_setPrivacyStatus", map);

        verifyStatic(Config.class, Mockito.times(1));
        Config.setPrivacyStatus(MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_OPT_IN);
    }

    public void testSimpleUnknownSetPrivacy() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("privacyStatus", "UNKNOWN");

        handler.setInitialized(true);
        handler.execute("ADB_setPrivacyStatus", map);

        verifyStatic(Config.class, Mockito.times(1));
        Config.setPrivacyStatus(MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_UNKNOWN);
    }

    public void testUnknownSetPrivacyFail() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("privacyStatus", "pouet");

        handler.setInitialized(true);
        handler.execute("ADB_setPrivacyStatus", map);

        verifyStatic(Config.class, Mockito.times(0));
        Config.setPrivacyStatus(any(MobilePrivacyStatus.class));
    }

/* ************************************** queueHits Tests *************************************** */

    public void testSendQueueHits() {
        handler.setInitialized(true);
        handler.execute("ADB_sendQueueHits", new HashMap<String, Object>());

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.getQueueSize();
        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.sendQueuedHits();
    }

    public void testClearQueueHits() {
        handler.setInitialized(true);
        handler.execute("ADB_clearQueue", new HashMap<String, Object>());

        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.getQueueSize();
        verifyStatic(Analytics.class, Mockito.times(1));
        Analytics.clearQueue();
    }

/* *************************************** Activities Tests ************************************* */

    public void testOnActivityStarted(){
        Activity testActivity = new Activity();
        handler.onActivityStarted(testActivity);

        PowerMockito.verifyNoMoreInteractions(Analytics.class);
    }

    public void testOnActivityResumed(){
        Activity testActivity = new Activity();
        handler.setInitialized(true);
        handler.onActivityResumed(testActivity);

        verifyStatic(Config.class, Mockito.times(1));
        Config.collectLifecycleData(any(Activity.class));
    }

    public void testOnActivityPaused(){
        Activity testActivity = new Activity();
        handler.setInitialized(true);
        handler.onActivityPaused(testActivity);

        verifyStatic(Config.class, Mockito.times(1));
        Config.pauseCollectingLifecycleData();
    }

    public void testOnActivityStopped(){
        Activity testActivity = new Activity();
        handler.onActivityStopped(testActivity);

        PowerMockito.verifyNoMoreInteractions(Analytics.class);
    }
}
