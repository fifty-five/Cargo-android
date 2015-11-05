package com.fiftyfive.cargo.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;

/**
 * Created by louis on 04/11/15.
 */
public class CargoModel {

    @JsonAnySetter
    public void set(String name, Object value) {
    }
}
