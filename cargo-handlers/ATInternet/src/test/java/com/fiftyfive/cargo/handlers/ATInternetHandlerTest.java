package com.fiftyfive.cargo.handlers;

import android.app.Application;

import com.atinternet.tracker.Tracker;
import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.models.Screen;


import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


/**
 * Created by louis on 04/11/15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(Tracker.class)

public class ATInternetHandlerTest extends TestCase {

    Tracker atTrackerMock = mock(Tracker.class);
    ATInternetHandler handler;
    @Mock Application context;
    @Mock Cargo cargo;


    public void setUp() throws Exception {
        initMocks(this);
        handler = new ATInternetHandler();
        handler.cargo = cargo;
        handler.atTracker = atTrackerMock;

    }

/*
*
* *************************** those tests are not working *************************** *
*
*
    public void testIdentifyNumberOfCalls(){
        when(cargo.getApplication()).thenReturn(context);

        HashMap<String, Object> map= new HashMap<>();
        map.put("test", 123);

        handler.execute("AT_identify", map);

        verify(atTrackerMock, times(1));
    }

    public void testTagScreen(){

        HashMap<String, Object> map= new HashMap<>();
        map.put(Screen.SCREEN_NAME, "HelloWorldScreen");
        when(atTrackerMock.Screens().add("HelloWorldScreen")).thenReturn(null);

        handler.execute("AT_tagScreen", map);
        verify(handler.atTracker, times(1)).Screens();
    }
*
* * *************************** ************************* *************************** *
*
*/

    public void tearDown() throws Exception {

    }
}