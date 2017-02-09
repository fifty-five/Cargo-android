package com.fiftyfive.cargo;

import java.util.Map;

import static com.fiftyfive.cargo.CargoItem.notifyTagFired;

/**
 * Created by Julien Gil on 12/01/2017.
 *
 * The Tags class implements the CustomTagProvider interface of the GTM SDK in order to be able
 * to receive and redirect custom functions calls with parameters.
 * Once a function call has been received, calls on the Cargo execute method to redirect it to
 * the handler matching the call.
 *
 * This class also plays a role in the CargoItem.itemsList, notifies it when a tag is fired.
 */
public class Tags implements com.google.android.gms.tagmanager.CustomTagProvider {

    /**
     * The method handling callbacks from GTM container when a tag is triggered.
     * The container must specify Tags as the ClassPath, the execute() method will
     * call the Cargo.execute() method to dispatch the map to the corresponding handler.
     *
     * @param map the map obtained from the container, containing the following :
     *            * handlerMethod (String) : the method aimed by this call. e.g : 'FB_init'
     *            * parameters : other key-value pairs are used as parameters for the method.
     */
    @Override
    public void execute(Map<String, Object> map) {
        Cargo.getInstance().execute(map);
        notifyTagFired();
    }

}
