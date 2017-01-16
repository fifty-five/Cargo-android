package com.fiftyfive.cargo;

import com.fiftyfive.cargo.Cargo;

import java.util.Map;

/**
 * Created by Julien Gil on 12/01/2017.
 */

public class Tags implements com.google.android.gms.tagmanager.CustomTagProvider {

    /**
     * The method handling callbacks from GTM container when a tag is triggered.
     * The container must specify Tags as the ClassPath, the execute() method will
     * call the Cargo.execute() method to dispatch the map to the corresponding handler.
     *
     * @param map the map obtained from the container, containing the following :
     *            * handlerKey (String) : the key of the handler
     *                                  (the same as the one used in the registerHandler() method)
     *            * handlerMethod (String) : the method aimed by this call. e.g : 'FB_init'
     *            * parameters : other key-value pairs are used as parameters for the method.
     */
    @Override
    public void execute(Map<String, Object> map) {
        Cargo.getInstance().execute(map);
    }
}
