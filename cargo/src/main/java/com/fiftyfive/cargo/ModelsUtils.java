package com.fiftyfive.cargo;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by louis on 02/12/15.
 * Copyright 2016 fifty-five All rights reserved.
 *
 * A tool class which is used to retrieve a specific typed object within a Map from its key.
 */
public class ModelsUtils {


/* ****************************** String, Date & Boolean objects ******************************** */

    /**
     * Retrieves a String object within a map from its key name.
     *
     * @param params        the map you want to retrieve your String from
     * @param name          the key which has been used to store the parameter
     *
     * @return  the String object if it has been found or null otherwise.
     */
    public static String getString(Map<String, Object> params, String name){
        Object value = params.get(name);
        if (value instanceof String){
            return value.toString();
        }
        else if (value instanceof String[]) {
            return Arrays.toString((String[]) value);
        }
        else if (value != null){
            return value.toString();
        }
        return null;
    }

    /**
     * Retrieves a boolean object within a map from its key name.
     *
     * @param params        the map you want to retrieve your boolean from
     * @param name          the key which has been used to store the parameter
     * @param defaultValue  a default value in case of the method fails
     *
     * @return the boolean if it has been found, or default value if not.
     */
    public static boolean getBoolean(Map<String, Object> params, String name, boolean defaultValue){
        Object value = params.get(name);
        if (value instanceof Boolean){
            return (boolean) value;
        }
        else if (value instanceof  String){
            return Boolean.valueOf(value.toString());
        }
        return defaultValue;
    }

    /**
     * Retrieves a Date object within a map from its key name.
     *
     * @param params        the map you want to retrieve your Date from
     * @param name          the key which has been used to store the parameter
     *
     * @return  the Date object if it has been found or null otherwise.
     */
    public static Date getDate(Map<String, Object> params, String name){
        Object value = params.get(name);
        if (value instanceof Date){
            return (Date) value;
        }
        else if (value instanceof String) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            try {
                Date date = format.parse(value.toString());
                System.out.println(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }



/* *************************************** Number objects *************************************** */

    /**
     * Retrieves an int object within a map from its key name.
     *
     * @param params        the map you want to retrieve your int from
     * @param name          the key which has been used to store the parameter
     * @param defaultValue  a default value in case of the method fails
     *
     * @return the int if it has been found, or default value if not.
     */
    public static int getInt(Map<String, Object> params, String name, int defaultValue){
        Object value = params.get(name);
        if (value instanceof Integer){
            return (int) value;
        }
        else if (value instanceof String){
            return Integer.valueOf(value.toString());
        }
        return defaultValue;
    }

    /**
     * Retrieves a Double object within a map from its key name.
     *
     * @param params        the map you want to retrieve your Double from
     * @param name          the key which has been used to store the parameter
     * @param defaultValue  a default value in case of the method fails
     *
     * @return  the Double object if it has been found or defaultValue otherwise.
     */
    public static double getDouble(Map<String, Object> params, String name, double defaultValue){
        Object value = params.get(name);
        if (value instanceof Double) {
            return (double) value;
        }
        else if (value instanceof String){
            return Double.parseDouble(value.toString());
        }
        return defaultValue;
    }

    /**
     * Retrieves a Long object within a map from its key name.
     *
     * @param params        the map you want to retrieve your Long from
     * @param name          the key which has been used to store the parameter
     * @param defaultValue  a default value in case of the method fails
     *
     * @return  the Long object if it has been found or defaultValue otherwise.
     */
    public static long getLong(Map<String, Object> params, String name, long defaultValue){
        Object value = params.get(name);
        if (value instanceof Long) {
            return (long)value;
        }
        else if (value instanceof String) {
            return Long.parseLong(value.toString());
        }
        else if (value instanceof Integer) {
            return (Integer)value;
        }
        return defaultValue;
    }



/* *************************************** Storage objects ************************************** */

    /**
     * Retrieves a Map object within a map from its key name.
     *
     * @param params        the map you want to retrieve your Map from
     * @param name          the key which has been used to store the parameter
     *
     * @return  the Map object if it has been found or null otherwise.
     */
    public static Map<String, Object> getMap(Map<String, Object> params, String name){
        Object value = params.get(name);
        if (value instanceof Map){
            return (Map<String, Object>) value;
        }
        return null;
    }

    /**
     * Retrieves a List object within a map from its key name.
     *
     * @param params        the map you want to retrieve your List from
     * @param name          the key which has been used to store the parameter
     *
     * @return  the List object if it has been found or null otherwise.
     */
    public static List getList(Map<String, Object> params, String name){
        Object value = params.get(name);
        if (value instanceof List){
            return (List) value;
        }
        return null;
    }

/* ********************************************************************************************** */

}
