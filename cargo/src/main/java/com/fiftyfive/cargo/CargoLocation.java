package com.fiftyfive.cargo;

import android.location.Location;

/**
 * Created by Julien Gil on 02/02/2018.
 *
 * This class is used to pass Location objects through Cargo, from the app to the handlers.
 */
public class CargoLocation {

    /** the Location to be sent */
    private static Location location = null;

    /**
     * Sets the user location after the location object given as parameter.
     *
     * @param userLocation  the location to set
     */
    public static void setLocation(Location userLocation) {
        location = userLocation;
    }

    /**
     * Retrieves the user location previously set.
     *
     * @return  Last Location object which has been set
     */
    public static Location getLocation() {
        return location;
    }

    /**
     * Returns whether a non null Location has been set already.
     *
     * @return  true if a non null Location object has been given, false otherwise
     */
    public static boolean locationIsSet() {
        return (location != null);
    }

}
