package com.fiftyfive.cargo;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.fiftyfive.cargo.ModelsUtils.getBoolean;
import static com.fiftyfive.cargo.ModelsUtils.getInt;
import static com.fiftyfive.cargo.ModelsUtils.getString;
import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ModelUtilsTest extends TestCase {

    private Map<String, Object> sampleMap =  new HashMap<>();


    public void setUp() throws Exception {
        sampleMap.put("intValue", 3 );
        sampleMap.put("stringValue", "a string");
        sampleMap.put("IntegerValue", new Integer(3));
        sampleMap.put("MapValue", new HashMap<>());
        sampleMap.put("booleanValue", true);
        sampleMap.put("BooleanValue", new Boolean(true));
        sampleMap.put("stringTrue", "true");



    }


    @Test
    public void testGetBoolean_withString(){
        assertTrue(getBoolean(sampleMap, "stringTrue", false));
    }

    @Test
    public void testGetBoolean_withBoolean(){
        assertTrue(getBoolean(sampleMap, "BooleanValue", true) );
    }

    @Test
    public void testGetBoolean_withboolean(){
        assertTrue(getBoolean(sampleMap, "booleanValue", true) );
    }

    @Test
    public void testGetString_withString(){
        assertTrue("a string" == getString(sampleMap, "stringValue") );
    }



    @Test
    public void testGetInt_withInteger(){
        assertTrue(3 == getInt(sampleMap, "IntegerValue", 10) );
    }


    @Test
    public void testGetInt_withInt(){
        assertTrue(3 == getInt(sampleMap, "intValue", 10) );
    }

}