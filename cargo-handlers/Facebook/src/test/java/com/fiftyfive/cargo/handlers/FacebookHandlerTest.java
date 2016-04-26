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

import java.util.HashMap;

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

    AppEventsLogger facebookLoggerMock = mock(AppEventsLogger.class);
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

    public void testTagEvent(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("eventName", "hello");

        handler.execute("FB_tagEvent", map);

        verify(facebookLoggerMock, times(1)).logEvent("hello");

    }

    public void testSetEnableDebug(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("enableDebug", "true");

        handler.execute("FB_init", map);
        verifyStatic();
        FacebookSdk.setIsDebugEnabled(true);
    }


    public void tearDown() throws Exception {

    }
}