package com.meimodev.sitouhandler.Dashboard.Services.Groceries;

public interface SectionVendorProducts {

    boolean isHeader();
    String getName();

    int getId();
    int getVendorId();
    String getVendorName();

    String getImageUrl ();
    String getPrice ();
    String getUnit ();

    boolean isAvailable ();
    String isAvailableMessage ();

    int sectionPosition();

    int getSize();
}
