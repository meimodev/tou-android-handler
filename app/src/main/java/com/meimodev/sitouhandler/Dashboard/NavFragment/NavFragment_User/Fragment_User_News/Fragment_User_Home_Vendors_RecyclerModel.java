/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_News;


public class Fragment_User_Home_Vendors_RecyclerModel {

    private int id;
    private String imageUrl;
    private String name;
    private String openHour;
    private String closeHour;
    private boolean isOpen;

    public Fragment_User_Home_Vendors_RecyclerModel(
            int id, String imageUrl, String name, String openHour, String closeHour, boolean isOpen) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.openHour = openHour;
        this.closeHour = closeHour;
        this.isOpen = isOpen;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpenHour() {
        return openHour;
    }

    public String getCloseHour() {
        return closeHour;
    }

    public boolean isOpen() {
        return isOpen;
    }
}
