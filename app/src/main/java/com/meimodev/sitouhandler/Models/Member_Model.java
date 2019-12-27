package com.meimodev.sitouhandler.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Member_Model {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("church_position")
    @Expose
    private String churchPosition;
    @SerializedName("nomor_surat_baptis")
    @Expose
    private String nomorSuratBaptis;
    @SerializedName("nomor_surat_sidi")
    @Expose
    private String nomorSuratSidi;
    @SerializedName("nomor_surat_nikah")
    @Expose
    private String nomorSuratNikah;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("column_id")
    @Expose
    private String columnId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChurchPosition() {
        return churchPosition;
    }

    public void setChurchPosition(String churchPosition) {
        this.churchPosition = churchPosition;
    }

    public String getNomorSuratBaptis() {
        return nomorSuratBaptis;
    }

    public void setNomorSuratBaptis(String nomorSuratBaptis) {
        this.nomorSuratBaptis = nomorSuratBaptis;
    }

    public String getNomorSuratSidi() {
        return nomorSuratSidi;
    }

    public void setNomorSuratSidi(String nomorSuratSidi) {
        this.nomorSuratSidi = nomorSuratSidi;
    }

    public String getNomorSuratNikah() {
        return nomorSuratNikah;
    }

    public void setNomorSuratNikah(String nomorSuratNikah) {
        this.nomorSuratNikah = nomorSuratNikah;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }


}
