package com.meimodev.sitouhandler.Dashboard.NavFragment;

import com.meimodev.sitouhandler.Constant;

public class Notification_Model implements Comparable<Notification_Model> {

    private int issueId;
    private int authId;
    private int authStatusCode;
    private String keyIssue;
    private String relatedDate;
    private String issuedByMemberName;
    private String issuedByMemberColumn;

    private String authStatus;

    public Notification_Model(int issueId, int authId,
                              String authStatus, String keyIssue,
                              String relatedDate, String issuedByMemberName,
                              String issuedByMemberColumn
    ) {
        this.issueId = issueId;
        this.authId = authId;
        this.keyIssue = keyIssue;
        this.relatedDate = relatedDate;
        this.issuedByMemberName = issuedByMemberName;
        this.issuedByMemberColumn = issuedByMemberColumn;
        this.authStatus = authStatus;

        switch (authStatus) {
            case Constant.AUTHORIZATION_STATUS_UNCONFIRMED:
                this.authStatusCode = Constant.AUTHORIZATION_STATUS_CODE_UNCONFIRMED;
                break;
            case Constant.AUTHORIZATION_STATUS_ACCEPTED:
                this.authStatusCode = Constant.AUTHORIZATION_STATUS_CODE_ACCEPTED;
                break;
            case Constant.AUTHORIZATION_STATUS_REJECTED:
                this.authStatusCode = Constant.AUTHORIZATION_STATUS_CODE_REJECTED;
                break;
            case Constant.AUTHORIZATION_STATUS_UNAUTHORIZED:
                this.authStatusCode = Constant.AUTHORIZATION_STATUS_CODE_UNAUTHORIZED;
                break;

        }


    }

    public String getIssuedByMemberName() {
        return issuedByMemberName;
    }

    public String getIssuedByMemberColumn() {
        return issuedByMemberColumn;
    }

    public int getIssueId() {
        return issueId;
    }

    public int getAuthId() {
        return authId;
    }

    public String getKeyIssue() {
        return keyIssue;
    }

    public String getRelatedDate() {
        return relatedDate;
    }

    public int getAuthStatusCode() {
        return authStatusCode;
    }

    @Override
    public int compareTo(Notification_Model o) {
        int compareConfirmed = o.getAuthStatusCode();
        return this.getAuthStatusCode() - compareConfirmed;
    }

    public String getAuthStatus() {
        return authStatus;
    }
}
