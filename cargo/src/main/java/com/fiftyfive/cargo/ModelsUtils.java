package com.fiftyfive.cargo;


import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by louis on 01/12/15.
 */
public class ModelsUtils {

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

    public static String getString(Map<String, Object> params, String name){
        Object value = params.get(name);
        if (value instanceof String){
            return value.toString();
        }
        else if (value != null){
            return value.toString();
        }
        return null;
    }

    public static Map<String, Object> getMap(Map<String, Object> params, String name){
        Object value = params.get(name);
        if (value instanceof Map){
            return (Map<String, Object>) value;
        }
        return null;
    }

    public static List getList(Map<String, Object> params, String name){
        Object value = params.get(name);
        if (value instanceof List){
            return (List) value;
        }
        return null;
    }

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

    public static long getLong(Map<String, Object> params, String name, long defaultValue){
        Object value = params.get(name);
        if (value instanceof Long) {
            return (long)value;
        }
        else if (value instanceof String) {
            return Long.parseLong(value.toString());
        }
        else if (value == (int)value) {
            return (int)value;
        }
        return defaultValue;
    }

    public static Date getDate(Map<String, Object> params, String name){
        Object value = params.get(name);
        if (value instanceof Date){
            return (Date) value;
        }
        return null;
    }
}
