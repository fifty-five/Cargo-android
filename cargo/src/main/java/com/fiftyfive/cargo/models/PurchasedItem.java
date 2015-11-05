package com.fiftyfive.cargo.models;

/**
 * Created by louis on 04/11/15.
 */
public class PurchasedItem extends CargoModel {

    private String name;
    private String sku;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
