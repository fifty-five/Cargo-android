package com.fiftyfive.cargo.handlers;

import android.app.Application;
import android.util.Log;

import com.atinternet.tracker.CustomObjects;
import com.atinternet.tracker.Gesture;
import com.atinternet.tracker.Gestures;
import com.atinternet.tracker.Screen;
import com.atinternet.tracker.Screens;
import com.atinternet.tracker.SetConfigCallback;
import com.atinternet.tracker.Tracker;
import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.models.Event;
import com.fiftyfive.cargo.models.User;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;
import java.util.HashMap;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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

    Screens screensMock = mock(Screens.class);
    Screen screenMock = mock(Screen.class);

    Gestures gesturesMock = mock(Gestures.class);
    Gesture gestureMock = mock(Gesture.class);

    CustomObjects customObjMock = mock(CustomObjects.class);


    String testName = "testScreenOrEvent";

    public void setUp() throws Exception {
        initMocks(this);
        handler = new ATInternetHandler();
        handler.cargo = cargo;
        handler.atTracker = atTrackerMock;

        when(atTrackerMock.Screens()).thenReturn(screensMock);
        when(atTrackerMock.Gestures()).thenReturn(gesturesMock);
        when(atTrackerMock.CustomObjects()).thenReturn(customObjMock);

        when(screensMock.add(testName)).thenReturn(screenMock);
        when(screenMock.setLevel2(anyInt())).thenReturn(screenMock);

        when(gesturesMock.add(anyString())).thenReturn(gestureMock);
        when(gesturesMock.add(anyString(), anyString())).thenReturn(gestureMock);
        when(gesturesMock.add(anyString(), anyString(), anyString())).thenReturn(gestureMock);
        when(gesturesMock.add(anyString(), anyString(), anyString(), anyString())).thenReturn(gestureMock);
        when(gestureMock.setLevel2(anyInt())).thenReturn(gestureMock);

    }

    public void tearDown() throws Exception {

    }

    public void testIdentify(){

        HashMap<String, Object> map = new HashMap<>();
        map.put(User.USER_ID, "Nestor");

        handler.execute("AT_identify", map);

        verify(atTrackerMock, times(1)).setConfig("identifier", "Nestor", null);

    }

    public void testFailedIdentifyWithNullValue(){

        HashMap<String, Object> map = new HashMap<>();
        map.put(User.USER_ID, null);

        handler.execute("AT_identify", map);

        verify(atTrackerMock, times(0)).setConfig("identifier", null, null);
    }

    public void testFailedIdentifyWithNoValue(){

        HashMap<String, Object> map = new HashMap<>();
        map.put("RandomKey", "Nestor");

        handler.execute("AT_identify", map);

        verify(atTrackerMock, times(0)).setConfig("identifier", null, null);
    }



    public void testSimpleTagScreen(){

        HashMap<String, Object> map = new HashMap<>();
        map.put(com.fiftyfive.cargo.models.Screen.SCREEN_NAME, testName);
        map.put(com.fiftyfive.cargo.models.Tracker.LEVEL2, 55);

        handler.execute("AT_tagScreen", map);

        verify(atTrackerMock.Screens(), times(1)).add(testName);
        verify(screenMock, times(1)).setLevel2(55);

//        /!\ CARE ! Needs to be times(1) but bug of NPE not fixed now
        verify(screenMock, times(1)).sendView();
    }

    public void testComplexTagScreen(){

        HashMap<String, Object> map = new HashMap<>();
        map.put(com.fiftyfive.cargo.models.Screen.SCREEN_NAME, testName);
        map.put(com.fiftyfive.cargo.models.Tracker.LEVEL2, 55);
        map.put(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM1, "test1");
        map.put(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM2, "test2");

        handler.execute("AT_tagScreen", map);

        verify(atTrackerMock.CustomObjects(), times(1)).add(new HashMap<String, Object>() {{
            put(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM1, "test1");
            put(com.fiftyfive.cargo.models.Tracker.CUSTOM_DIM2, "test2");
        }});
        verify(atTrackerMock.Screens(), times(1)).add(testName);
        verify(screenMock, times(1)).setLevel2(55);
//        /!\ CARE ! Needs to be times(1) but bug of NPE not fixed now
        verify(screenMock, times(1)).sendView();
    }



    public void testTagEventWithoutChapters(){

        HashMap<String, Object> map = new HashMap<>();
        map.put(Event.EVENT_NAME, testName);
        map.put(Event.EVENT_TYPE, "sendTouch");
        map.put(com.fiftyfive.cargo.models.Tracker.LEVEL2, 55);

        handler.execute("AT_tagEvent", map);

        verify(atTrackerMock.Gestures(), times(1)).add(testName);
        verify(gestureMock, times(1)).setLevel2(55);
        verify(gestureMock, times(1)).sendTouch();
        verify(gestureMock, times(0)).sendDownload();
        verify(gestureMock, times(0)).sendExit();
        verify(gestureMock, times(0)).sendSearch();
        verify(gestureMock, times(0)).sendNavigation();
    }

    public void testTagEventWithChapters(){

        HashMap<String, Object> map = new HashMap<>();
        map.put(Event.EVENT_NAME, testName);
        map.put(Event.EVENT_TYPE, "sendDownload");
        map.put("Chapter1", "chapter1");
        map.put("Chapter2", "chapter2");
        map.put("Chapter3", "chapter3");
        map.put(com.fiftyfive.cargo.models.Tracker.LEVEL2, 55);

        handler.execute("AT_tagEvent", map);

        verify(atTrackerMock.Gestures(), times(1)).add(testName, "chapter1", "chapter2", "chapter3");
        verify(gestureMock, times(1)).setLevel2(55);
        verify(gestureMock, times(1)).sendDownload();
        verify(gestureMock, times(0)).sendTouch();
        verify(gestureMock, times(0)).sendExit();
        verify(gestureMock, times(0)).sendSearch();
        verify(gestureMock, times(0)).sendNavigation();
    }

    public void testTagEventFailedNoType(){

        HashMap<String, Object> map = new HashMap<>();
        map.put(Event.EVENT_NAME, testName);
        map.put("Chapter1", "chapter1");
        map.put("Chapter2", "chapter2");
        map.put("Chapter3", "chapter3");
        map.put(com.fiftyfive.cargo.models.Tracker.LEVEL2, 55);

        handler.execute("AT_tagEvent", map);

        verify(atTrackerMock.Gestures(), times(0)).add(testName, "chapter1", "chapter2", "chapter3");
        verify(gestureMock, times(0)).setLevel2(55);
        verify(gestureMock, times(0)).sendDownload();
    }

    public void testTagEventFailedWrongType(){

        HashMap<String, Object> map = new HashMap<>();
        map.put(Event.EVENT_NAME, testName);
        map.put(Event.EVENT_TYPE, "sendNothing");
        map.put("Chapter1", "chapter1");
        map.put("Chapter2", "chapter2");
        map.put("Chapter3", "chapter3");
        map.put(com.fiftyfive.cargo.models.Tracker.LEVEL2, 55);

        handler.execute("AT_tagEvent", map);

        verify(atTrackerMock.Gestures(), times(1)).add(testName, "chapter1", "chapter2", "chapter3");
        verify(gestureMock, times(1)).setLevel2(55);
        verify(gestureMock, times(0)).sendTouch();
        verify(gestureMock, times(0)).sendDownload();
        verify(gestureMock, times(0)).sendExit();
        verify(gestureMock, times(0)).sendSearch();
        verify(gestureMock, times(0)).sendNavigation();
    }

    public void testTagEventSendNavigation(){

        HashMap<String, Object> map = new HashMap<>();
        map.put(Event.EVENT_NAME, testName);
        map.put(Event.EVENT_TYPE, "sendNavigation");
        map.put(com.fiftyfive.cargo.models.Tracker.LEVEL2, 55);

        handler.execute("AT_tagEvent", map);

        verify(atTrackerMock.Gestures(), times(1)).add(testName);
        verify(gestureMock, times(1)).setLevel2(55);
        verify(gestureMock, times(1)).sendNavigation();
        verify(gestureMock, times(0)).sendTouch();
        verify(gestureMock, times(0)).sendDownload();
        verify(gestureMock, times(0)).sendExit();
        verify(gestureMock, times(0)).sendSearch();
    }

    public void testTagEventSendExit(){

        HashMap<String, Object> map = new HashMap<>();
        map.put(Event.EVENT_NAME, testName);
        map.put(Event.EVENT_TYPE, "sendExit");
        map.put(com.fiftyfive.cargo.models.Tracker.LEVEL2, 55);

        handler.execute("AT_tagEvent", map);

        verify(atTrackerMock.Gestures(), times(1)).add(testName);
        verify(gestureMock, times(1)).setLevel2(55);
        verify(gestureMock, times(1)).sendExit();
        verify(gestureMock, times(0)).sendTouch();
        verify(gestureMock, times(0)).sendDownload();
        verify(gestureMock, times(0)).sendSearch();
        verify(gestureMock, times(0)).sendNavigation();
    }

    public void testTagEventSendSearch(){

        HashMap<String, Object> map = new HashMap<>();
        map.put(Event.EVENT_NAME, testName);
        map.put(Event.EVENT_TYPE, "sendSearch");
        map.put(com.fiftyfive.cargo.models.Tracker.LEVEL2, 55);

        handler.execute("AT_tagEvent", map);

        verify(atTrackerMock.Gestures(), times(1)).add(testName);
        verify(gestureMock, times(1)).setLevel2(55);
        verify(gestureMock, times(1)).sendSearch();
        verify(gestureMock, times(0)).sendTouch();
        verify(gestureMock, times(0)).sendDownload();
        verify(gestureMock, times(0)).sendExit();
        verify(gestureMock, times(0)).sendNavigation();
    }
}