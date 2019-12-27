package com.meimodev.sitouhandler.Issue;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Adding_RecyclerModel /* implements Parcelable */ {

    private int id;
    private String name, birthDate, kolom, imageUrl;
    private String category;

    private boolean isBaptis = false, isSidi = false, isNikah = false;
    private boolean isCategoryHead = false;
    private boolean isUnregistered = false;


    public Adding_RecyclerModel(int id,
                                @NonNull String name,
                                @Nullable String birthDate,
                                @Nullable String kolom,
                                @Nullable String imageUrl) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.kolom = kolom;
        this.imageUrl = imageUrl;
    }

//    private Adding_RecyclerModel(Parcel parcel) {
//        id = parcel.readInt();
//        name = parcel.readString();
//        birthDate = parcel.readString();
//        kolom = parcel.readString();
//        imageUrl = parcel.readString();
//
//        category = parcel.readString();
//
//        isBaptis = parcel.readInt() == 1;
//        isSidi = parcel.readInt() == 1;
//        isNikah = parcel.readInt() == 1;
//        isCategoryHead = parcel.readInt() == 1;
//
//    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getKolom() {
        return kolom;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isBaptis() {
        return isBaptis;
    }

    public void setBaptis(boolean baptis) {
        isBaptis = baptis;
    }

    public boolean isSidi() {
        return isSidi;
    }

    public void setSidi(boolean sidi) {
        isSidi = sidi;
    }

    public boolean isNikah() {
        return isNikah;
    }

    public void setNikah(boolean nikah) {
        isNikah = nikah;
    }

    public boolean isCategoryHead() {
        if (category == null) category = "";
        return !this.category.isEmpty();

    }

//    public void setCategoryHead(boolean categoryHead) {
//        isCategoryHead = categoryHead;
//    }

    public String getCategory() {
        return  category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isUnregistered() {
        return isUnregistered;
    }

    public void setUnregistered(boolean unregistered) {
        isUnregistered = unregistered;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//
//        parcel.writeInt(id);
//        parcel.writeString(name);
//        parcel.writeString(birthDate);
//        parcel.writeString(kolom);
//        parcel.writeString(imageUrl);
//
//        parcel.writeString(category);
//
//
//        parcel.writeInt(isBaptis ? 1 : 0);
//        parcel.writeInt(isSidi ? 1 : 0);
//        parcel.writeInt(isNikah ? 1 : 0);
//        parcel.writeInt(isCategoryHead ? 1 : 0);
//
//    }
//
//    public static final Parcelable.Creator<Adding_RecyclerModel> CREATOR = new Parcelable.Creator<Adding_RecyclerModel>() {
//
//        @Override
//        public Adding_RecyclerModel createFromParcel(Parcel parcel) {
//            return new Adding_RecyclerModel(parcel);
//        }
//
//        @Override
//        public Adding_RecyclerModel[] newArray(int i) {
//            return new Adding_RecyclerModel[i];
//        }
//    };
}
