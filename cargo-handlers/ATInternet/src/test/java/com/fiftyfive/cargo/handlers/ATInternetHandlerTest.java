package com.fiftyfive.cargo.handlers;

import android.app.Application;

import com.atinternet.tracker.Tracker;
import com.fiftyfive.cargo.Cargo;


import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


/**
 * Created by dali on 10/12/15.
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
        handler.atTracker = atTrackerMock;
    }

    public void testTagScreen(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("screenName", "Home");
        handler.execute("GA_tagScreen", map);

        verify(atTrackerMock, times(1));

    }

    public void tearDown() throws Exception {

    }
}