package com.fiftyfive.cargo;

import com.fiftyfive.cargo.models.Item;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Julien Gil on 06/01/2017.
 */

public class CargoItem {

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
    private int position = 1;
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


    /**
     * Constructor for the CargoItem object. Creates the object with an item name.
     * Use these objects in order to send items related hits to SDKs.
     *
     * @param name the name of the item.
     */
    public CargoItem(String name) {
        this.name = name;
    }

    /**
     * Constructor for the CargoItem object.
     * Creates the object with an item name, its price, and the quantity selected.
     * The revenue is automatically generated (unitPrice x quantity).
     * Use these objects in order to send items related hits to SDKs.
     *
     * @param name the name of the item.
     * @param unitPrice the unit price for this item.
     * @param quantity number of items concerned by the hit.
     */
    public CargoItem(String name, double unitPrice, int quantity) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.revenue = unitPrice * (double) quantity;
    }

    /**
     * Static method you have to call in order to send items through GTM with Cargo.
     * Cargo transforms the CargoItem array into a String containing a JSON object.
     * Once GTM transmitted the parameter with the callback,
     * the array is rebuilt with true Item objects depending on the handler within they are rebuilt.
     *
     * @param itemArray an array of CargoItem to send to a SDK.
     * @return a String containing a JSON which represents the array given as parameter.
     */
    public static String toGTM(CargoItem[] itemArray) {
        int i = 0;
        try {
            JSONObject jsonObject = new JSONObject();
            for (CargoItem item : itemArray) {
                JSONObject itemJson = new JSONObject();
                itemJson.put(Item.NAME, item.getName());
                if (item.getId() != null)
                    itemJson.put(Item.ID, item.getId());
                if (item.getUnitPrice() != -1)
                    itemJson.put(Item.UNIT_PRICE, item.getUnitPrice());
                if (item.getQuantity() != -1)
                    itemJson.put(Item.QUANTITY, item.getQuantity());
                if (item.getRevenue() != -1)
                    itemJson.put(Item.REVENUE, item.getRevenue());
                if (item.getBrand() != null)
                    itemJson.put(Item.BRAND, item.getBrand());
                if (item.getCategory() != null)
                    itemJson.put(Item.CATEGORY, item.getCategory());
                if (item.getId() != null)
                    itemJson.put(Item.VARIANT, item.getVariant());
                if (item.getPosition() != -1)
                    itemJson.put(Item.POSITION, item.getPosition());
                if (item.getCouponCode() != null)
                    itemJson.put(Item.COUPON_CODE, item.getCouponCode());
                if (item.getiDimension() != -1 && item.getvDimension() != null) {
                    itemJson.put(Item.INDEX_DIM, item.getiDimension());
                    itemJson.put(Item.VALUE_DIM, item.getvDimension());
                }
                if (item.getiMetric() != -1 && item.getvMetric() != -1) {
                    itemJson.put(Item.INDEX_METRIC, item.getiMetric());
                    itemJson.put(Item.VALUE_METRIC, item.getvMetric());
                }
                if (item.getAttribute1() != null)
                    itemJson.put(Item.ATTR1, item.getAttribute1());
                if (item.getAttribute2() != null)
                    itemJson.put(Item.ATTR2, item.getAttribute2());
                if (item.getAttribute3() != null)
                    itemJson.put(Item.ATTR3, item.getAttribute3());
                if (item.getAttribute4() != null)
                    itemJson.put(Item.ATTR4, item.getAttribute4());
                if (item.getAttribute5() != null)
                    itemJson.put(Item.ATTR5, item.getAttribute5());

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

        builder.append("{CargoCustomItem:");
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
     */
    public void setName(String name) {
        this.name = name;
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
     * Setter for the vMetric attribute.
     *
     * @param vMetric the new vMetric to set for this object.
     */
    public void setvMetric(int vMetric) {
        this.vMetric = vMetric;
    }

    /**
     * Setter for the id attribute.
     *
     * @param id the new id to set for this object.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Setter for the brand attribute.
     *
     * @param brand the new brand to set for this object.
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Setter for the category attribute.
     *
     * @param category the new category to set for this object.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Setter for the variant attribute.
     *
     * @param variant the new variant to set for this object.
     */
    public void setVariant(String variant) {
        this.variant = variant;
    }

    /**
     * Setter for the position attribute.
     *
     * @param position the new position to set for this object.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Setter for the couponCode attribute.
     *
     * @param couponCode the new couponCode to set for this object.
     */
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    /**
     * Setter for the iDimension attribute.
     *
     * @param iDimension the new iDimension to set for this object.
     */
    public void setiDimension(int iDimension) {
        this.iDimension = iDimension;
    }

    /**
     * Setter for the vDimension attribute.
     *
     * @param vDimension the new vDimension to set for this object.
     */
    public void setvDimension(String vDimension) {
        this.vDimension = vDimension;
    }

    /**
     * Setter for the iMetric attribute.
     *
     * @param iMetric the new iMetric to set for this object.
     */
    public void setiMetric(int iMetric) {
        this.iMetric = iMetric;
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
