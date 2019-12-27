package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Secretary;

public class NavFragment_Secretary_Papers_RecyclerModel {

    private int id;
    private String letterType, letterEntry;

    public int getId() {
        return id;
    }

    public String getLetterType() {
        return letterType;
    }

    public String getLetterEntry() {
        return letterEntry;
    }

    public NavFragment_Secretary_Papers_RecyclerModel(int id, String letterType, String letterEntry) {
        this.id = id;
        this.letterType = letterType;
        this.letterEntry = letterEntry;
    }
}
