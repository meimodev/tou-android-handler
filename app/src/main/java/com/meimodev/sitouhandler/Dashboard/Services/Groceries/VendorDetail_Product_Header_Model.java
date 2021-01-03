package com.meimodev.sitouhandler.Dashboard.Services.Groceries;

public class VendorDetail_Product_Header_Model implements SectionVendorProducts {

    String name;
    private int section;

    public VendorDetail_Product_Header_Model(int section) {
        this.section = section;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public boolean isHeader() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public int getVendorId() {
        return 0;
    }

    @Override
    public String getVendorName() {
        return null;
    }

    @Override
    public String getImageUrl() {
        return null;
    }

    @Override
    public String getPrice() {
        return null;
    }

    @Override
    public String getUnit() {
        return null;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public String isAvailableMessage() {
        return null;
    }

    @Override
    public int sectionPosition() {
        return section;
    }
}