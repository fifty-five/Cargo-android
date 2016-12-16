package com.fiftyfive.cargo.handlers;

import android.app.Application;

import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.models.Event;
import com.fiftyfive.cargo.models.User;
import com.tune.Tune;
import com.tune.TuneEvent;
import com.tune.TuneEventItem;
import com.tune.TuneGender;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;


/**
 * Created by louis on 04/11/15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Tune.class, TuneEvent.class, TuneHandler.class})

public class TuneHandlerTest extends TestCase {

/* *********************************** Variables declaration ************************************ */

    Tune tuneMock = PowerMockito.mock(Tune.class);
    TuneEvent tuneEventMock = PowerMockito.mock(TuneEvent.class);
    TuneHandler handler;
    @Mock Application context;
    @Mock Cargo cargo;

/* ***************************************** Test setup ***************************************** */

    public void setUp() throws Exception {
        initMocks(this);
        handler = new TuneHandler();
        handler.tune = tuneMock;
        handler.cargo = cargo;
        PowerMockito.whenNew(TuneEvent.class).withArguments(anyString()).thenReturn(tuneEventMock);
        PowerMockito.whenNew(TuneEvent.class).withArguments(anyInt()).thenReturn(tuneEventMock);
    }

    public void tearDown() throws Exception {

    }

/* **************************************** Init Tests ****************************************** */

    public void testInitWithAllParameters(){
        when(cargo.getAppContext()).thenReturn(context);
        PowerMockito.mockStatic(Tune.class);

        HashMap<String, Object> map= new HashMap<>();
        map.put("advertiserId", 123);
        map.put("conversionKey", 432);

        handler.execute("TUN_init", map);
        verifyStatic();
        Tune.init(context, "123", "432");
        assertTrue(handler.isInitialized());
    }

    public void testInitWithWrongParameters(){
        when(cargo.getAppContext()).thenReturn(context);
        HashMap<String, Object> map= new HashMap<>();
        map.put("conversionKey", 432);

        handler.execute("TUN_init", map);

        assertFalse(handler.isInitialized());
    }

    public void testFrameworkNotInit(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_ID, "123456-543210-55-42");

        handler.execute("TUN_identify", map);
        verify(tuneMock, times(0)).setUserId(anyString());
    }

/* *************************************** tagEvent Tests *************************************** */

    public void testSimpleTagEventWithAndStringParams() throws Exception {
        HashMap<String, Object> map= new HashMap<>();
        map.put(Event.EVENT_NAME, "eventName");
        map.put("eventCurrencyCode", "eventCurrencyCode");
        map.put("eventAdvertiserRefId", "eventAdvertiserRefId");
        map.put("eventContentId", "eventContentId");
        map.put("eventContentType", "eventContentType");
        map.put("eventSearchString", "eventSearchString");
        map.put("eventAttribute1", "eventAttribute1");
        map.put("eventAttribute2", "eventAttribute2");
        map.put("eventAttribute3", "eventAttribute3");
        map.put("eventAttribute4", "eventAttribute4");
        map.put("eventAttribute5", "eventAttribute5");

        handler.setInitialized(true);
        handler.execute("TUN_tagEvent", map);
        PowerMockito.verifyNew(TuneEvent.class).withArguments("eventName");

        verify(tuneEventMock, times(1)).withCurrencyCode("eventCurrencyCode");
        verify(tuneEventMock, times(1)).withAdvertiserRefId("eventAdvertiserRefId");
        verify(tuneEventMock, times(1)).withContentId("eventContentId");
        verify(tuneEventMock, times(1)).withContentType("eventContentType");
        verify(tuneEventMock, times(1)).withSearchString("eventSearchString");
        verify(tuneEventMock, times(1)).withAttribute1("eventAttribute1");
        verify(tuneEventMock, times(1)).withAttribute2("eventAttribute2");
        verify(tuneEventMock, times(1)).withAttribute3("eventAttribute3");
        verify(tuneEventMock, times(1)).withAttribute4("eventAttribute4");
        verify(tuneEventMock, times(1)).withAttribute5("eventAttribute5");

        verify(tuneMock, times(1)).measureEvent((TuneEvent) Matchers.any());
    }

    public void testTagEventWithIdAndDifferentParams() throws Exception {
        HashMap<String, Object> map= new HashMap<>();
        map.put(Event.EVENT_ID, 5542);
        map.put("eventRating", 25.2);
        Date date1 = new Date();
        map.put("eventDate1", date1);
        map.put("eventRevenue", 55.42);
        List<TuneEventItem> eventItems = new ArrayList<>();
        eventItems.add(new TuneEventItem("itemName1").withQuantity(55));
        eventItems.add(new TuneEventItem("itemName2").withQuantity(42));
        map.put("eventItems", eventItems);
        map.put("eventLevel", 55);
        map.put("eventReceiptData", "test1");
        map.put("eventReceiptSignature", "test 2");
        map.put("eventQuantity", 42);
        Date date2 = new Date();
        map.put("eventDate2", date2);
        map.put("lkdshfkjhdsf", "sdkjhd");

        handler.setInitialized(true);
        handler.execute("TUN_tagEvent", map);
        PowerMockito.verifyNew(TuneEvent.class).withArguments(5542);

        verify(tuneEventMock, times(1)).withRating(25.2);
        verify(tuneEventMock, times(1)).withDate1(date1);
        verify(tuneEventMock, times(1)).withDate2(date2);
        verify(tuneEventMock, times(1)).withRevenue(55.42);
        verify(tuneEventMock, times(1)).withEventItems(eventItems);
        verify(tuneEventMock, times(1)).withLevel(55);
        verify(tuneEventMock, times(1)).withReceipt("test1", "test 2");
        verify(tuneEventMock, times(1)).withQuantity(42);
        verify(tuneMock, times(1)).measureEvent((TuneEvent) Matchers.any());
    }

    public void testFailIDTagEvent() throws Exception {
        HashMap<String, Object> map= new HashMap<>();
        map.put(Event.EVENT_ID, 55.5f);

        handler.setInitialized(true);
        handler.execute("TUN_tagEvent", map);

        verifyZeroInteractions(tuneEventMock);
    }

    public void testFailTagEvent() throws Exception {
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventQuantity", 55);

        handler.setInitialized(true);
        handler.execute("TUN_tagEvent", map);

        verifyZeroInteractions(tuneEventMock);
    }

/* **************************************** identify Tests ************************************** */

    public void testUserIdWithString(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_ID, "123456-543210-55-42");

        handler.setInitialized(true);
        handler.execute("TUN_identify", map);
        verify(tuneMock, times(1)).setUserId("123456-543210-55-42");
    }

    public void testUserName(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USERNAME, "name name");

        handler.setInitialized(true);
        handler.execute("TUN_identify", map);
        verify(tuneMock, times(1)).setUserName("name name");
    }

    public void testUserEmail(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_EMAIL, "aMailAdress");

        handler.setInitialized(true);
        handler.execute("TUN_identify", map);
        verify(tuneMock, times(1)).setUserEmail("aMailAdress");
    }

    public void testUserGoogleIdWithInt(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_GOOGLE_ID, 123);

        handler.setInitialized(true);
        handler.execute("TUN_identify", map);
        verify(tuneMock, times(1)).setGoogleUserId("123");
    }

    public void testUserGoogleIdWithString(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_GOOGLE_ID, "234");

        handler.setInitialized(true);
        handler.execute("TUN_identify", map);
        verify(tuneMock, times(1)).setGoogleUserId("234");
    }

    public void testUserFacebookIdWithString(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_FACEBOOK_ID, "345");

        handler.setInitialized(true);
        handler.execute("TUN_identify", map);
        verify(tuneMock, times(1)).setFacebookUserId("345");
    }

    public void testUserTwitterIdWithString(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_TWITTER_ID, "012");

        handler.setInitialized(true);
        handler.execute("TUN_identify", map);
        verify(tuneMock, times(1)).setTwitterUserId("012");
    }

    public void testUserAgeWithString(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_AGE, "55");

        handler.setInitialized(true);
        handler.execute("TUN_identify", map);
        verify(tuneMock, times(1)).setAge(55);
    }

    public void testUserAgeWithInt(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_AGE, 42);

        handler.setInitialized(true);
        handler.execute("TUN_identify", map);
        verify(tuneMock, times(1)).setAge(42);
    }

    public void testFailUserAge(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_AGE, 42.5f);

        handler.setInitialized(true);
        handler.execute("TUN_identify", map);
        verifyZeroInteractions(tuneMock);
    }

    public void testUserGenderMale(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_GENDER, "male");

        handler.setInitialized(true);
        handler.execute("TUN_identify", map);
        verify(tuneMock, times(1)).setGender(TuneGender.MALE);
    }

    public void testUserGenderFemale(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_GENDER, "female");

        handler.setInitialized(true);
        handler.execute("TUN_identify", map);
        verify(tuneMock, times(1)).setGender(TuneGender.FEMALE);
    }

    public void testUserGenderUnknown(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_GENDER, "unknown");

        handler.setInitialized(true);
        handler.execute("TUN_identify", map);
        verify(tuneMock, times(1)).setGender(TuneGender.UNKNOWN);
    }

    public void testUserGenderForcedToUnknown(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_GENDER, "dfjkhdrkjfgh");

        handler.setInitialized(true);
        handler.execute("TUN_identify", map);
        verify(tuneMock, times(1)).setGender(TuneGender.UNKNOWN);
    }

    public void testIdentifyMissingKey(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("userMissingId", "123");

        handler.execute("TUN_identify", map);
        assertTrue(true);
    }

}