package com.meimodev.sitouhandler.IssueDetail;

import com.meimodev.sitouhandler.Constant;

import org.json.JSONArray;

public class IssueDetailNotation_NotationModel {
    private JSONArray positions;
    private String confirmedDate;
    private String authStatus;

    public IssueDetailNotation_NotationModel(JSONArray positions, String confirmedDate, String authStatus) {
        this.positions = positions;
        this.authStatus = authStatus;
        this.confirmedDate = confirmedDate;
    }

    public JSONArray getPositions() {
        return positions;
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
