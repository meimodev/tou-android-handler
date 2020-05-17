/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_News;

import android.content.Intent;

import androidx.annotation.DrawableRes;

public class Fragment_User_Home_News_RecyclerModel {

    private int id;
    private String imageUrl;
    private Intent toActivityIntent;
    private String description;
    private String title;
    private String time;

    Fragment_User_Home_News_RecyclerModel(int id, String imageUrl,
                                          Intent toActivityIntent,
                                          String title,
                                          String description,
                                          String time) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.toActivityIntent = toActivityIntent;
        this.description = description;
        this.title = title;
        this.time = time;
    }


    public int getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Intent getToActivityIntent() {
        return toActivityIntent;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }


    public String getTime() {
        return time;
    }
}
