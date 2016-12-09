package com.fiftyfive.cargo.handlers;

import com.google.android.gms.analytics.HitBuilders;

/**
 * Created by Julien Gil on 30/11/2016.
 */

/**
 * A class designed for testing purposes. Helps to test the GoogleAnalyticsHandler's tagEvent method
 * Creates a EventBuilder Object and adds properties to it.
 */
class EventBuilderGA {

    /** The HitBuilders.EventBuilder built in the class */
    private HitBuilders.EventBuilder event;

    /**
     * Contructor, creates a instance of HitBuilders.EventBuilder object.
     */
    public EventBuilderGA() {
        this.event = new HitBuilders.EventBuilder();
    }

    /**
     * Set an action to the HitBuilders.EventBuilder object.
     *
     * @param action the action to set.
     */
    void setAction(String action) {
        this.event.setAction(action);
    }

    /**
     * Set a category to the HitBuilders.EventBuilder object.
     *
     * @param category the category to set
     */
    void setCategory(String category) {
        this.event.setCategory(category);
    }

    /**
     * Set a label to the HitBuilders.EventBuilder object.
     *
     * @param label the label to set
     */
    void setLabel(String label) {
        this.event.setLabel(label);
    }

    /**
     * Set a value to the HitBuilders.EventBuilder object.
     *
     * @param value the value you want to set as a long
     */
    void setValue(long value) {
        this.event.setValue(value);
    }

    /**
     * Set whether the event is interactive or not to the HitBuilders.EventBuilder object.
     *
     * @param bool true to set an event to non interactive.
     */
    void setNonInteraction(boolean bool) {
        this.event.setNonInteraction(bool);
    }

    /**
     * Returns the HitBuilders.EventBuilder with its properties
     * @return the HitBuilders.EventBuilder object with its properties
     */
    HitBuilders.EventBuilder getEvent() {
        return this.event;
    }
}
