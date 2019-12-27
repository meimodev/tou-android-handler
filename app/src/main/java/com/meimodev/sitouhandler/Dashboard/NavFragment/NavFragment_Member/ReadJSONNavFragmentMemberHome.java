
package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Member;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReadJSONNavFragmentMemberHome {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Data> data = null;

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

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("issue_id")
        @Expose
        private Integer issueId;
        @SerializedName("issue_key")
        @Expose
        private String issueKey;
        @SerializedName("issued_by_member_name")
        @Expose
        private String issuedByMemberName;
        @SerializedName("issued_by_member_column")
        @Expose
        private String issuedByMemberColumn;
        @SerializedName("auth_id")
        @Expose
        private Integer authId;
        @SerializedName("auth_status")
        @Expose
        private String authStatus;
        @SerializedName("auth_on")
        @Expose
        private String authOn;

        public Integer getIssueId() {
            return issueId;
        }

        public void setIssueId(Integer issueId) {
            this.issueId = issueId;
        }

        public String getIssueKey() {
            return issueKey;
        }

        public void setIssueKey(String issueKey) {
            this.issueKey = issueKey;
        }

        public String getIssuedByMemberName() {
            return issuedByMemberName;
        }

        public void setIssuedByMemberName(String issuedByMemberName) {
            this.issuedByMemberName = issuedByMemberName;
        }

        public String getIssuedByMemberColumn() {
            return issuedByMemberColumn;
        }

        public void setIssuedByMemberColumn(String issuedByMemberColumn) {
            this.issuedByMemberColumn = issuedByMemberColumn;
        }

        public Integer getAuthId() {
            return authId;
        }

        public void setAuthId(Integer authId) {
            this.authId = authId;
        }

        public String getAuthStatus() {
            return authStatus;
        }

        public void setAuthStatus(String authStatus) {
            this.authStatus = authStatus;
        }

        public String getAuthOn() {
            return authOn;
        }

        public void setAuthOn(String authOn) {
            this.authOn = authOn;
        }

    }


}


