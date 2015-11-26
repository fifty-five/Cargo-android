package com.fiftyfive.cargo.handlers;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.fiftyfive.cargo.Cargo;

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
import static org.mockito.Matchers.eq;

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
@PrepareForTest(FacebookSdk.class)

public class FacebookHandlerTest extends TestCase {

    FacebookSdk facebookTrackerMock = mock(FacebookSdk.class);
    AppEventsLogger facebookLoggerMock = mock(AppEventsLogger.class);
    FacebookHandler handler;
    @Mock Application context;
    @Mock Cargo cargo;


    public void setUp() throws Exception {
        initMocks(this);
        handler = new FacebookHandler();
        PowerMockito.mockStatic(FacebookSdk.class);
        handler.facebookTracker = facebookTrackerMock;
        handler.facebookLogger = facebookLoggerMock;
        handler.cargo = cargo;

    }


    public void testInitWithAllParameters(){
        when(cargo.getApplication()).thenReturn(context);

        HashMap<String, Object> map= new HashMap<>();
        map.put("applicationId", 123);

        handler.execute("FB_init", map);

        verifyStatic();
        FacebookSdk.sdkInitialize(context);

        verifyStatic();
        FacebookSdk.setApplicationId("123");

    }

    public void testTagEvent(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventName", "hello");

        handler.execute("FB_tagEvent", map);

        verify(facebookLoggerMock, times(1)).logEvent("hello");

    }

    public void tearDown() throws Exception {

    }
}