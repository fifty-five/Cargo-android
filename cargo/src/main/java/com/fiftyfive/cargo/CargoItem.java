package com.fiftyfive.cargo;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fiftyfive.cargo.models.Item;

import java.util.ArrayList;

/**
 * Created by Julien Gil on 06/01/2017.
 *
 * This class allows to create generic item objects for handlers which send events with item objects.
 * This class also contains an ArrayList which stores CargoItem objects. When en event needing Item
 * objects is about to be sent, each handlers requiring Item objects check on this ArrayList and
 * translate the CargoItem objects into their specific SDK Item objects.
 */
public class CargoItem {

    /** a list of CargoItem which will be sent to some SDK at the next event */
    private static ArrayList<CargoItem> itemsList = null;
    private static boolean tagFiredSinceLastChange = false;

    /** name of the item */
    private String name;
    /** id of the item */
    private String id;
    /** unit price of the item */
    private double unitPrice = -1;
    /** number of items concerned */
    private int quantity = -1;
    /** total cost of all these items */
    private double revenue = -1;

    /** brand of the item */
    private String brand;
    /** category of the item */
    private String category;
    /** variant of the item */
    private String variant;
    /** position of the item */
    private int position = -1;
    /** couponCode of the item */
    private String couponCode;
    /** index for an item customDim */
    private int iDimension = -1;
    /** value for an item customDim */
    private String vDimension;
    /** index for an item customMetric */
    private int iMetric = -1;
    /** value for an item customMetric */
    private int vMetric = -1;

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



/** ***************************************** itemsList ***************************************** */

    /**
     * Adds an item to the list which will be sent to the next "item relative" event.
     * A null parameter will be ignored.
     *
     * @param item the item to add as a parameter to the future event.
     */
    public static void attachItemToEvent(@NonNull CargoItem item) {
        if (item == null)
            return ;
        checkIfTagHasBeenFired();
        if (itemsList == null) {
            itemsList = new ArrayList<CargoItem>();
        }
        itemsList.add(item);
    }

    /**
     * Sets the list of items which will be sent to the next "item relative" event with a new value.
     *
     * @param newList A new ArrayList of CargoItem objects, which value can be null.
     */
    public static void setItemsList(ArrayList<CargoItem> newList) {
        checkIfTagHasBeenFired();
        // changes the value of the list and its linked data depending on the given list value.
        if (newList != null && !newList.isEmpty()) {
            itemsList = newList;
        }
        else {
            itemsList = null;
        }
    }

    /**
     * A getter for the list of items which will be sent to the next "item relative" event.
     * May be used to modify some objects before setting a new list with setItemsList() method.
     *
     * @return an ArrayList of CargoItem object.
     */
    public static ArrayList<CargoItem> getItemsList() {
        return itemsList;
    }

    /**
     * Verifies whether a tag has been fired since the last list modification.
     * If applicable, reset the 'tagFiredSinceLastChange' boolean to 'false' and set the 'itemsList'
     * ArrayList to 'null' value.
     * This method is called each time the 'itemsList' attribute is accessed in writing.
     */
    private static void checkIfTagHasBeenFired() {
        if (tagFiredSinceLastChange) {
            tagFiredSinceLastChange = false;
            CargoItem.setItemsList(null);
            Log.d("CargoItem", "CargoItem.itemsList is set to 'null' after an event " +
                    "has been sent and before any other itemsList manipulation.");
        }
    }

    /**
     * Called by Tags class to notify this class when a tag is fired, in order to wipe the list.
     */
    static void notifyTagFired() {
        tagFiredSinceLastChange = true;
    }



/** ***************************************** CargoItem ***************************************** */

    /**
     * Constructor for the CargoItem object. Creates the object with an item name.
     * Use these objects in order to send items related hits to SDKs.
     *
     * @param name the name of the item.
     */
    public CargoItem(@NonNull String name) {
        this.name = name;
    }

    /**
     * Constructor for the CargoItem object.
     * Creates the object with an item name, its price, and the quantity selected.
     * Use these objects in order to send items related hits to SDKs.
     *
     * @param name the name of the item.
     * @param unitPrice the unit price for this item.
     * @param quantity number of items concerned by the hit.
     */
    public CargoItem(@NonNull String name, double unitPrice, int quantity) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    /**
     * The toString method for the CargoItem object.
     *
     * @return the description of the current object as a String.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("{CargoItem:");
        builder.append(" "+Item.NAME+"=").append(this.name);
        if (id != null)
            builder.append(", "+Item.ID+"=").append(this.id);
        if (unitPrice != -1)
            builder.append(", "+Item.UNIT_PRICE+"=").append(Double.toString(this.unitPrice));
        if (quantity != -1)
            builder.append(", "+Item.QUANTITY+"=").append(Integer.toString(this.quantity));
        if (revenue != -1)
            builder.append(", "+Item.REVENUE+"=").append(Double.toString(this.revenue));
        if (brand != null)
            builder.append(", "+Item.BRAND+"=").append(this.brand);
        if (category != null)
            builder.append(", "+Item.CATEGORY+"=").append(this.category);
        if (variant != null)
            builder.append(", "+Item.VARIANT+"=").append(this.variant);
        if (position != -1)
            builder.append(", "+Item.POSITION+"=").append(Integer.toString(this.position));
        if (couponCode != null)
            builder.append(", "+Item.COUPON_CODE+"=").append(this.couponCode);
        if (iDimension != -1 && vDimension != null) {
            builder.append(", customDim='").append(Integer.toString(this.iDimension) + "="
                    + this.vDimension + "'");
        }
        if (iMetric != -1 && vMetric != -1) {
            builder.append(", customMetric='").append(Integer.toString(this.iMetric) + "="
                    + Integer.toString(this.vMetric) + "'");
        }
        if (attribute1 != null)
            builder.append(", "+Item.ATTR1+"=").append(this.attribute1);
        if (attribute2 != null)
            builder.append(", "+Item.ATTR2+"=").append(this.attribute2);
        if (attribute3 != null)
            builder.append(", "+Item.ATTR3+"=").append(this.attribute3);
        if (attribute4 != null)
            builder.append(", "+Item.ATTR4+"=").append(this.attribute4);
        if (attribute5 != null)
            builder.append(", "+Item.ATTR5+"=").append(this.attribute5);
        builder.append("}");
        return builder.toString();
    }

/** ****************************************** getter ******************************************* */

    /**
     * Getter for the name attribute.
     *
     * @return the name of the current object.
     */
    public String getName() {
        return name;
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
     * Getter for the brand attribute.
     *
     * @return the brand of the current object.
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Getter for the id attribute.
     *
     * @return the id of the current object.
     */
    public String getId() {
        return id;
    }

    /**
     * Getter for the category attribute.
     *
     * @return the category of the current object.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Getter for the variant attribute.
     *
     * @return the variant of the current object.
     */
    public String getVariant() {
        return variant;
    }

    /**
     * Getter for the position attribute.
     *
     * @return the position of the current object.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Getter for the couponCode attribute.
     *
     * @return the couponCode of the current object.
     */
    public String getCouponCode() {
        return couponCode;
    }

    /**
     * Getter for the iDimension attribute.
     *
     * @return the iDimension of the current object.
     */
    public int getiDimension() {
        return iDimension;
    }

    /**
     * Getter for the vDimension attribute.
     *
     * @return the vDimension of the current object.
     */
    public String getvDimension() {
        return vDimension;
    }

    /**
     * Getter for the iMetric attribute.
     *
     * @return the iMetric of the current object.
     */
    public int getiMetric() {
        return iMetric;
    }

    /**
     * Getter for the vMetric attribute.
     *
     * @return the vMetric of the current object.
     */
    public int getvMetric() {
        return vMetric;
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
     * @param name the new name to set for this object.
     * @return the current item
     */
    public CargoItem setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Setter for the unitPrice attribute.
     *
     * @param unitPrice the new unitPrice to set for this object.
     * @return the current item
     */
    public CargoItem setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }

    /**
     * Setter for the quantity attribute.
     *
     * @param quantity the new quantity to set for this object.
     * @return the current item
     */
    public CargoItem setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    /**
     * Setter for the revenue attribute.
     *
     * @param revenue the new revenue to set for this object.
     * @return the current item
     */
    public CargoItem setRevenue(double revenue) {
        this.revenue = revenue;
        return this;
    }

    /**
     * Setter for the vMetric attribute.
     *
     * @param vMetric the new vMetric to set for this object.
     * @return the current item
     */
    public CargoItem setvMetric(int vMetric) {
        this.vMetric = vMetric;
        return this;
    }

    /**
     * Setter for the id attribute.
     *
     * @param id the new id to set for this object.
     * @return the current item
     */
    public CargoItem setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Setter for the brand attribute.
     *
     * @param brand the new brand to set for this object.
     * @return the current item
     */
    public CargoItem setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    /**
     * Setter for the category attribute.
     *
     * @param category the new category to set for this object.
     * @return the current item
     */
    public CargoItem setCategory(String category) {
        this.category = category;
        return this;
    }

    /**
     * Setter for the variant attribute.
     *
     * @param variant the new variant to set for this object.
     * @return the current item
     */
    public CargoItem setVariant(String variant) {
        this.variant = variant;
        return this;
    }

    /**
     * Setter for the position attribute.
     *
     * @param position the new position to set for this object.
     * @return the current item
     */
    public CargoItem setPosition(int position) {
        this.position = position;
        return this;
    }

    /**
     * Setter for the couponCode attribute.
     *
     * @param couponCode the new couponCode to set for this object.
     * @return the current item
     */
    public CargoItem setCouponCode(String couponCode) {
        this.couponCode = couponCode;
        return this;
    }

    /**
     * Setter for the iDimension attribute.
     *
     * @param iDimension the new iDimension to set for this object.
     * @return the current item
     */
    public CargoItem setiDimension(int iDimension) {
        this.iDimension = iDimension;
        return this;
    }

    /**
     * Setter for the vDimension attribute.
     *
     * @param vDimension the new vDimension to set for this object.
     * @return the current item
     */
    public CargoItem setvDimension(String vDimension) {
        this.vDimension = vDimension;
        return this;
    }

    /**
     * Setter for the iMetric attribute.
     *
     * @param iMetric the new iMetric to set for this object.
     * @return the current item
     */
    public CargoItem setiMetric(int iMetric) {
        this.iMetric = iMetric;
        return this;
    }

    /**
     * Setter for the attribute1 attribute.
     *
     * @param attribute1 the new attribute1 to set for this object.
     * @return the current item
     */
    public CargoItem setAttribute1(String attribute1) {
        this.attribute1 = attribute1;
        return this;
    }

    /**
     * Setter for the attribute2 attribute.
     *
     * @param attribute2 the new attribute2 to set for this object.
     * @return the current item
     */
    public CargoItem setAttribute2(String attribute2) {
        this.attribute2 = attribute2;
        return this;
    }

    /**
     * Setter for the attribute3 attribute.
     *
     * @param attribute3 the new attribute3 to set for this object.
     * @return the current item
     */
    public CargoItem setAttribute3(String attribute3) {
        this.attribute3 = attribute3;
        return this;
    }

    /**
     * Setter for the attribute4 attribute.
     *
     * @param attribute4 the new attribute4 to set for this object.
     * @return the current item
     */
    public CargoItem setAttribute4(String attribute4) {
        this.attribute4 = attribute4;
        return this;
    }

    /**
     * Setter for the attribute5 attribute.
     *
     * @param attribute5 the new attribute5 to set for this object.
     * @return the current item
     */
    public CargoItem setAttribute5(String attribute5) {
        this.attribute5 = attribute5;
        return this;
    }
}
