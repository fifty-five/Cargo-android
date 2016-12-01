package com.fiftyfive.cargo.handlers;

import com.google.android.gms.analytics.HitBuilders;

/**
 * Created by Julien Gil on 30/11/2016.
 */

class EventBuilderGA {

    private HitBuilders.EventBuilder event;

    public EventBuilderGA() {
        this.event = new HitBuilders.EventBuilder();
    }

    void setAction(String action) {
        this.event.setAction(action);
    }

    void setCategory(String category) {
        this.event.setCategory(category);
    }

    void setLabel(String label) {
        this.event.setLabel(label);
    }

    void setValue(long value) {
        this.event.setValue(value);
    }

    void setNonInteraction(boolean bool) {
        this.event.setNonInteraction(bool);
    }

    HitBuilders.EventBuilder getEvent() {
        return this.event;
    }
}
