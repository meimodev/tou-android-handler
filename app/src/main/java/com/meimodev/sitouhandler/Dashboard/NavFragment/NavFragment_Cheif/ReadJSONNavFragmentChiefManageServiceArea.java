
package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Cheif;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ReadJSONNavFragmentChiefManageServiceArea {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public Boolean isError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public class Data {

        @SerializedName("priests")
        @Expose
        private List<Priest> priests = null;

        public List<Priest> getPriests() {
            return priests;
        }

        public void setPriests(List<Priest> priests) {
            this.priests = priests;
        }

    }


    public class Priest {

        @SerializedName("member_id")
        @Expose
        private Integer memberId;
        @SerializedName("service_area_id")
        @Expose
        private Integer serviceAreaId;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("domicile_column")
        @Expose
        private String domicileColumn;
        @SerializedName("from_column")
        @Expose
        private String fromColumn;
        @SerializedName("to_column")
        @Expose
        private String toColumn;

        public Integer getMemberId() {
            return memberId;
        }

        public void setMemberId(Integer memberId) {
            this.memberId = memberId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDomicileColumn() {
            return domicileColumn;
        }

        public void setDomicileColumn(String domicileColumn) {
            this.domicileColumn = domicileColumn;
        }

        public String getFromColumn() {
            return fromColumn;
        }

        public void setFromColumn(String fromColumn) {
            this.fromColumn = fromColumn;
        }

        public String getToColumn() {
            return toColumn;
        }

        public void setToColumn(String toColumn) {
            this.toColumn = toColumn;
        }

        public Integer getServiceAreaId() {
            return serviceAreaId;
        }

        public void setServiceAreaId(Integer serviceAreaId) {
            this.serviceAreaId = serviceAreaId;
        }
    }
}
