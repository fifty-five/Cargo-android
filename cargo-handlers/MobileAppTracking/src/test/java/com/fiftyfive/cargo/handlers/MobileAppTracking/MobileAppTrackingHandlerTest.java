package com.fiftyfive.cargo.handlers.MobileAppTracking;

import android.app.Application;

import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.handlers.mobileapptracking.MobileAppTrackingHandler;
import com.mobileapptracker.MATEvent;
import com.mobileapptracker.MATEventItem;
import com.mobileapptracker.MobileAppTracker;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


/**
 * Created by louis on 04/11/15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(MobileAppTracker.class)

public class MobileAppTrackingHandlerTest extends TestCase {

    //@Mock Application context;
    MobileAppTracker mobileAppTrackerMock = mock(MobileAppTracker.class);
    MobileAppTrackingHandler handler;
    @Mock Application context;
    @Mock Cargo cargo;


    public void setUp() throws Exception {
        initMocks(this);
        PowerMockito.mockStatic(MobileAppTracker.class);
        handler = new MobileAppTrackingHandler();
        handler.mobileAppTracker = mobileAppTrackerMock;
        handler.cargo = cargo;

    }


    public void testInitWithAllParameters(){
        when(cargo.getApplication()).thenReturn(context);
        HashMap<String, Object> map= new HashMap<>();
        map.put("advertiserId", 123);
        map.put("conversionKey", 432);

        handler.execute("MAT_init", map);

        verify(mobileAppTrackerMock, times(1)).init(context, "123", "432");
        assertTrue(handler.isInitialized());
    }

    public void testInitWithWrongParameters(){
        when(cargo.getApplication()).thenReturn(context);
        HashMap<String, Object> map= new HashMap<>();
        map.put("conversionKey", 432);

        handler.execute("MAT_init", map);

        assertFalse(handler.isInitialized());
    }

    public void testUserGoogleIdWithInt(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("userGoogleId", 123);

        handler.execute("MAT_identify", map);
        verify(mobileAppTrackerMock, times(1)).setGoogleUserId("123");
    }

    public void testUserGoogleIdWithString(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("userGoogleId", "123");

        handler.execute("MAT_identify", map);
        verify(mobileAppTrackerMock, times(1)).setGoogleUserId("123");
    }

    public void testIdentifyMissingKey(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("userMissingId", "123");

        handler.execute("MAT_identify", map);
        assertTrue(true);
    }

    public void testScreenName(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("screenName", "Hello");
        handler.execute("MAT_tagScreen", map);

        List<MATEventItem> items = new ArrayList<>();
        MATEventItem item = new MATEventItem("Hello");
        items.add(item);
        MATEvent event = new MATEvent(MATEvent.CONTENT_VIEW).withEventItems(items);

        verify(mobileAppTrackerMock, times(1)).measureEvent(any(MATEvent.class));

    }

    public void testTransaction(){

    }
    public void tearDown() throws Exception {

    }
}