/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.IssueDetail;

import com.meimodev.sitouhandler.Constant;

import org.json.JSONArray;

public class IssueDetailNotation_NotationModel {
    private String positionsString;
    private String confirmedDate;
    private String authStatus;

    public IssueDetailNotation_NotationModel(String positions, String confirmedDate, String authStatus) {
        this.positionsString = positions;
        this.authStatus = authStatus;
        this.confirmedDate = confirmedDate;
    }


    public String getPositionsString(){
        return positionsString;
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
                str = "DIAJUKAN";
        }


        return str;
    }
}
