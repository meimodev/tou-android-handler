package com.meimodev.sitouhandler.IssueDetail;

import com.meimodev.sitouhandler.Constant;

public class IssueDetailNotation_NotationModel {
    private String position, confirmedDate;
    private String authStatus;

    public IssueDetailNotation_NotationModel(String position, String confirmedDate, String authStatus) {
        this.position = position;
        this.authStatus = authStatus;
        this.confirmedDate = confirmedDate;
    }

    public String getPosition() {
        return position;
    }

    public String getConfirmedDate() {
        return confirmedDate;
    }

    public String getAuthStatus() {
        String str;
        switch (authStatus) {
            case Constant.AUTHORIZATION_STATUS_ACCEPTED:
                str = "DITERIMA";
                break;
            case Constant.AUTHORIZATION_STATUS_REJECTED:
                str = "DITOLAK";
                break;
            default:
                str = "DITEMBUS";
        }


        return str;
    }
}
