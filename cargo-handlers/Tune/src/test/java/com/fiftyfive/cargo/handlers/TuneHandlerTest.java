package com.fiftyfive.cargo.handlers;

import android.app.Application;

import com.fiftyfive.cargo.Cargo;

import com.fiftyfive.cargo.models.User;
import com.tune.Tune;
import com.tune.TuneGender;

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

    public void testUserIdWithString(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_ID, "123456-543210-55-42");

        handler.execute("Tune_identify", map);
        verify(tuneMock, times(1)).setUserId("123456-543210-55-42");
    }


    public void testUserGoogleIdWithInt(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_GOOGLE_ID, 123);
        map.put(User.USER_ID, "123456-543210-55-42");

        handler.execute("Tune_identify", map);
        verify(tuneMock, times(1)).setGoogleUserId("123");
    }

    public void testUserGoogleIdWithString(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_GOOGLE_ID, "234");
        map.put(User.USER_ID, "123456-543210-55-42");

        handler.execute("Tune_identify", map);
        verify(tuneMock, times(1)).setGoogleUserId("234");
    }

    public void testUserFacebookIdWithString(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_FACEBOOK_ID, "345");
        map.put(User.USER_ID, "123456-543210-55-42");

        handler.execute("Tune_identify", map);
        verify(tuneMock, times(1)).setFacebookUserId("345");
    }

    public void testUserTwitterIdWithString(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_TWITTER_ID, "012");
        map.put(User.USER_ID, "123456-543210-55-42");

        handler.execute("Tune_identify", map);
        verify(tuneMock, times(1)).setTwitterUserId("012");
    }

    public void testUserAgeWithString(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_AGE, "55");
        map.put(User.USER_ID, "123456-543210-55-42");

        handler.execute("Tune_identify", map);
        verify(tuneMock, times(1)).setAge(55);
    }

    public void testUserAgeWithInt(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_AGE, 42);
        map.put(User.USER_ID, "123456-543210-55-42");

        handler.execute("Tune_identify", map);
        verify(tuneMock, times(1)).setAge(42);
    }

    public void testUserGenderMale(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_GENDER, "male");
        map.put(User.USER_ID, "123456-543210-55-42");

        handler.execute("Tune_identify", map);
        verify(tuneMock, times(1)).setGender(TuneGender.MALE);
    }

    public void testUserGenderFemale(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_GENDER, "female");
        map.put(User.USER_ID, "123456-543210-55-42");

        handler.execute("Tune_identify", map);
        verify(tuneMock, times(1)).setGender(TuneGender.FEMALE);
    }

    public void testUserGenderUnknown(){
        HashMap<String, Object> map= new HashMap<>();
        map.put(User.USER_GENDER, "unknown");
        map.put(User.USER_ID, "123456-543210-55-42");

        handler.execute("Tune_identify", map);
        verify(tuneMock, times(1)).setGender(TuneGender.UNKNOWN);
    }

    public void testIdentifyMissingKey(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("userMissingId", "123");
        map.put(User.USER_ID, "123456-543210-55-42");

        handler.execute("Tune_identify", map);
        assertTrue(true);
    }

    public void tearDown() throws Exception {
        
    }
}