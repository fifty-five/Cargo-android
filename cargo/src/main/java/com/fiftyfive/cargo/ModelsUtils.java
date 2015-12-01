package com.fiftyfive.cargo;


import java.util.Map;

/**
 * Created by louis on 01/12/15.
 */
public class ModelsUtils {

    public static boolean getBoolean(Map<String, Object> params, String name, boolean defaultValue){

        Object value = params.get(name);
        if(value instanceof Boolean){
            return (boolean) value;
        }
        else if(value instanceof  String){
            return Boolean.valueOf(value.toString());
        }
        return defaultValue;
    }

    public static int getInt(Map<String, Object> params, String name, int defaultValue){
        Object value = params.get(name);
        if(value instanceof Integer){
            return (int) value;
        }
        else if (value instanceof String){
            return Integer.valueOf(value.toString());
        }
        return defaultValue;
    }

}
