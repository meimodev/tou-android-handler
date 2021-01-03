package com.meimodev.sitouhandler.Dashboard.Services.Groceries;

public class VendorDetail_Product_Child_Model implements SectionVendorProducts {

    private int id;
    private String name;
    private String imageUrl;
    private String price;
    private String unit;

    private boolean isAvailable;
    private String isAvailableMessage;

    private int vendorId;
    private String vendorName;

    private int section;

    public VendorDetail_Product_Child_Model(
            int id,
            String name,
            String imageUrl,
            String price,
            String unit,
            boolean isAvailable,
            String isAvailableMessage
    ) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.unit = unit;
        this.isAvailable = isAvailable;
        this.isAvailableMessage = isAvailableMessage;
    }



    public void setSection(int section){
        this.section = section;
    }

    public int getId() {
        return id;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    @Override
    public int getVendorId() {
        return vendorId;
    }

    @Override
    public String getVendorName() {
        return vendorName;
    }

    @Override
    public boolean isHeader() {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getPrice() {
        return price;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public String isAvailableMessage() {
        return isAvailableMessage;
    }

    @Override
    public int sectionPosition() {
        return section;
    }

}