package com.fiftyfive.cargo.handlers;

import android.app.Application;

import com.fiftyfive.cargo.Cargo;

import com.tune.Tune;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

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
@PrepareForTest(Tune.class)

public class TuneHandlerTest extends TestCase {

    Tune tuneMock = mock(Tune.class);
    TuneHandler handler;
    @Mock Application context;
    @Mock Cargo cargo;


    public void setUp() throws Exception {
        initMocks(this);
        handler = new TuneHandler();
        handler.tune = tuneMock;
        handler.cargo = cargo;

    }


    public void testInitWithAllParameters(){
        when(cargo.getApplication()).thenReturn(context);
        PowerMockito.mockStatic(Tune.class);

        HashMap<String, Object> map= new HashMap<>();
        map.put("advertiserId", 123);
        map.put("conversionKey", 432);

        handler.execute("Tune_init", map);
        verifyStatic();
        Tune.init(context, "123", "432");
        assertTrue(handler.isInitialized());
    }

    public void testInitWithWrongParameters(){
        when(cargo.getApplication()).thenReturn(context);
        HashMap<String, Object> map= new HashMap<>();
        map.put("conversionKey", 432);

        handler.execute("Tune_init", map);

        assertFalse(handler.isInitialized());
    }

    public void testUserGoogleIdWithInt(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("userGoogleId", 123);

        handler.execute("Tune_identify", map);
        verify(tuneMock, times(1)).setGoogleUserId("123");
    }

    public void testUserGoogleIdWithString(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("userGoogleId", "123");

        handler.execute("Tune_identify", map);
        verify(tuneMock, times(1)).setGoogleUserId("123");
    }

    public void testIdentifyMissingKey(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("userMissingId", "123");

        handler.execute("Tune_identify", map);
        assertTrue(true);
    }

    public void tearDown() throws Exception {

    }
}