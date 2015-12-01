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
        PowerMockito.mockStatic(GoogleAnalytics.class);
        handler = new GoogleAnalyticsHandler();
        handler.analytics = googleAnalyticsMock;
    }

    public void tearDown() throws Exception {

    }

    public void testInit(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("enableOptOut", true);

        handler.execute("GA_init", map);
        verify(googleAnalyticsMock, times(1)).setAppOptOut(true);
    }

    public void testInitWithAllParameters(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("enableOptOut", true);
        map.put("disableTracking", true);
        map.put("trackerDispatchPeriod", 123);

        handler.execute("GA_init", map);
        verify(googleAnalyticsMock, times(1)).setAppOptOut(true);
        verify(googleAnalyticsMock, times(1)).setDryRun(true);
        verify(googleAnalyticsMock, times(1)).setLocalDispatchPeriod(123);
    }
}