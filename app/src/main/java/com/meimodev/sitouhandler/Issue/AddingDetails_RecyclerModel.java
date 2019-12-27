package com.meimodev.sitouhandler.Issue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AddingDetails_RecyclerModel /* implements Parcelable */ {

    private int id;
    private int position;

    private String amount, details;

    public AddingDetails_RecyclerModel(int id, int position, String amount, String details) {
        this.id = id;
        this.position = position;
        this.amount = amount;
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
