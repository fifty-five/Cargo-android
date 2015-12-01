package com.fiftyfive.cargo.handlers;

import android.app.Application;

import com.fiftyfive.cargo.Cargo;
import com.google.android.gms.analytics.GoogleAnalytics;

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

/**
 * Created by dali on 25/11/15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(GoogleAnalytics.class)
public class GoogleAnalyticsHandlerTest extends TestCase {

    //@Mock Application context;
    GoogleAnalytics googleAnalyticsMock = mock(GoogleAnalytics.class);
    GoogleAnalyticsHandler handler;
    @Mock
    Application context;
    @Mock
    Cargo cargo;

    public void setUp() throws Exception {
        initMocks(this);

        when(cargo.getApplication()).thenReturn(context);

        handler = new GoogleAnalyticsHandler();
        handler.cargo = cargo;
        handler.analytics = googleAnalyticsMock;
    }

    public void tearDown() throws Exception {

    }

    public void testInitWithBoolean(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("enableOptOut", true);
        handler.execute("GA_init", map);

        verify(googleAnalyticsMock, times(1)).setAppOptOut(true);

    }

    public void testInitWithString(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("enableOptOut", "true");
        handler.execute("GA_init", map);

        verify(googleAnalyticsMock, times(1)).setAppOptOut(true);

    }


    public void testInitWithMap(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("enableOptOut", new HashMap<>());
        handler.execute("GA_init", map);

        verify(googleAnalyticsMock, times(1)).setAppOptOut(false);

    }

    public void testDefaultValuesForInit(){
        HashMap<String, Object> map= new HashMap<>();
        handler.execute("GA_init", map);

        verify(googleAnalyticsMock, times(1)).setAppOptOut(false);
        verify(googleAnalyticsMock, times(1)).setDryRun(false);
        verify(googleAnalyticsMock, times(1)).setLocalDispatchPeriod(30);
    }

    public void testSettingValueForDispatchPeriod(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("dispatchPeriod", "100");
        handler.execute("GA_init", map);

        verify(googleAnalyticsMock, times(1)).setLocalDispatchPeriod(100);
    }

    public void testSettingValueForDryRun(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("disableTracking", "true");
        handler.execute("GA_init", map);

        verify(googleAnalyticsMock, times(1)).setDryRun(true);
    }
}