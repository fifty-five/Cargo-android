package com.fiftyfive.cargo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Julien Gil on 06/01/2017.
 */

public class CargoItem {

    /** name of the item */
    private String item;
    /** unit price of the item */
    private double unitPrice = -1;
    /** number of items concerned */
    private int quantity = -1;
    /** total cost of all these items */
    private double revenue = -1;
    /** attribute of the item */
    private String attribute1;
    /** attribute of the item */
    private String attribute2;
    /** attribute of the item */
    private String attribute3;
    /** attribute of the item */
    private String attribute4;
    /** attribute of the item */
    private String attribute5;


    /**
     * Constructor for the CargoItem object. Creates the object with an item name.
     * Use these objects in order to send items related hits to the Tune SDK.
     *
     * @param item the name of the item.
     */
    public CargoItem(String item) {
        this.item = item;
    }

    /**
     * Constructor for the CargoItem object.
     * Creates the object with an item name, its price, and the quantity selected.
     * The revenue is automatically generated (unitPrice x quantity).
     * Use these objects in order to send items related hits to the Tune SDK.
     *
     * @param item the name of the item.
     * @param unitPrice the unit price for this item.
     * @param quantity number of items concerned by the hit.
     */
    public CargoItem(String item, double unitPrice, int quantity) {
        this.item = item;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.revenue = unitPrice * (double) quantity;
    }

    /**
     * Static method you have to call in order to send items through GTM with Cargo.
     * Cargo transforms the CargoItem array into a String containing a JSON object.
     * Once GTM transmitted the parameter with the callback,
     * the array is rebuilt with true TuneEventItem objects.
     *
     * @param itemArray an array of CargoItem to send to the Tune SDK.
     * @return a String containing a JSON which represents the array given as parameter.
     */
    public static String toGTM(CargoItem[] itemArray) {
        int i = 0;
        try {
            JSONObject jsonObject = new JSONObject();
            for (CargoItem item : itemArray) {
                JSONObject itemJson = new JSONObject();
                itemJson.put("item", item.getItem());
                if (item.getUnitPrice() != -1)
                    itemJson.put("unitPrice", item.getUnitPrice());
                if (item.getQuantity() != -1)
                    itemJson.put("quantity", item.getQuantity());
                if (item.getRevenue() != -1)
                    itemJson.put("revenue", item.getRevenue());
                if (item.getAttribute1() != null)
                    itemJson.put("attribute1", item.getAttribute1());
                if (item.getAttribute2() != null)
                    itemJson.put("attribute2", item.getAttribute2());
                if (item.getAttribute3() != null)
                    itemJson.put("attribute3", item.getAttribute3());
                if (item.getAttribute4() != null)
                    itemJson.put("attribute4", item.getAttribute4());
                if (item.getAttribute5() != null)
                    itemJson.put("attribute5", item.getAttribute5());

                jsonObject.put(Integer.toString(i++), itemJson);
            }
            return jsonObject.toString();
        }
        catch(JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * The toString method for the CargoItem object.
     *
     * @return the description of the current object as a String.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("{TunecustomItem:");
        builder.append(" item=").append(this.item);
        if (unitPrice != -1)
            builder.append(", unitPrice=").append(Double.toString(this.unitPrice));
        if (quantity != -1)
            builder.append(", quantity=").append(Integer.toString(this.quantity));
        if (revenue != -1)
            builder.append(", revenue=").append(Double.toString(this.revenue));
        if (attribute1 != null)
            builder.append(", attribute1=").append(this.attribute1);
        if (attribute2 != null)
            builder.append(", attribute2=").append(this.attribute2);
        if (attribute3 != null)
            builder.append(", attribute3=").append(this.attribute3);
        if (attribute4 != null)
            builder.append(", attribute4=").append(this.attribute4);
        if (attribute5 != null)
            builder.append(", attribute5=").append(this.attribute5);
        builder.append("}");
        return builder.toString();
    }

/** ****************************************** getter ******************************************* */

    /**
     * Getter for the name attribute.
     *
     * @return the name of the current object.
     */
    public String getItem() {
        return item;
    }

    /**
     * Getter for the unitPrice attribute.
     *
     * @return the unitPrice of the current object.
     */
    public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * Getter for the quantity attribute.
     *
     * @return the quantity of the current object.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Getter for the revenue attribute.
     *
     * @return the revenue of the current object.
     */
    public double getRevenue() {
        return revenue;
    }

    /**
     * Getter for the attribute1 attribute.
     *
     * @return the attribute1 of the current object.
     */
    public String getAttribute1() {
        return attribute1;
    }

    /**
     * Getter for the attribute2 attribute.
     *
     * @return the attribute2 of the current object.
     */
    public String getAttribute2() {
        return attribute2;
    }

    /**
     * Getter for the attribute3 attribute.
     *
     * @return the attribute3 of the current object.
     */
    public String getAttribute3() {
        return attribute3;
    }

    /**
     * Getter for the attribute4 attribute.
     *
     * @return the attribute4 of the current object.
     */
    public String getAttribute4() {
        return attribute4;
    }

    /**
     * Getter for the attribute5 attribute.
     *
     * @return the attribute5 of the current object.
     */
    public String getAttribute5() {
        return attribute5;
    }



/** ****************************************** setter ******************************************* */

    /**
     * Setter for the item attribute.
     *
     * @param item the new name to set for this object.
     */
    public void setItem(String item) {
        this.item = item;
    }

    /**
     * Setter for the unitPrice attribute.
     *
     * @param unitPrice the new unitPrice to set for this object.
     */
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * Setter for the quantity attribute.
     *
     * @param quantity the new quantity to set for this object.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Setter for the revenue attribute.
     *
     * @param revenue the new revenue to set for this object.
     */
    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    /**
     * Setter for the attribute1 attribute.
     *
     * @param attribute1 the new attribute1 to set for this object.
     */
    public void setAttribute1(String attribute1) {
        this.attribute1 = attribute1;
    }

    /**
     * Setter for the attribute2 attribute.
     *
     * @param attribute2 the new attribute2 to set for this object.
     */
    public void setAttribute2(String attribute2) {
        this.attribute2 = attribute2;
    }

    /**
     * Setter for the attribute3 attribute.
     *
     * @param attribute3 the new attribute3 to set for this object.
     */
    public void setAttribute3(String attribute3) {
        this.attribute3 = attribute3;
    }

    /**
     * Setter for the attribute4 attribute.
     *
     * @param attribute4 the new attribute4 to set for this object.
     */
    public void setAttribute4(String attribute4) {
        this.attribute4 = attribute4;
    }

    /**
     * Setter for the attribute5 attribute.
     *
     * @param attribute5 the new attribute5 to set for this object.
     */
    public void setAttribute5(String attribute5) {
        this.attribute5 = attribute5;
    }
}
