package com.fiftyfive.cargo_android;

import com.google.android.gms.tagmanager.ContainerHolder;

/**
 * Created by Julien Gil on 06/12/2016.
 */

public class ContainerHolderSingleton {
    private static ContainerHolder containerHolder;

    /**
     * Utility class; don't instantiate.
     */
    private ContainerHolderSingleton() {
    }


    /* ---------- getter & setter ---------- */

    public static ContainerHolder getContainerHolder() {
        return containerHolder;
    }

    public static void setContainerHolder(ContainerHolder c) {
        containerHolder = c;
    }
}