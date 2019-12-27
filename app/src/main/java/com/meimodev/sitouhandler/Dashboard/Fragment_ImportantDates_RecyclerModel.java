package com.meimodev.sitouhandler.Dashboard;

import com.meimodev.sitouhandler.IssueDetail.IssueDetailNames_RecyclerModel;
import com.meimodev.sitouhandler.Models.BasicMember_Model;

public class Fragment_ImportantDates_RecyclerModel {

    private int issueId;

    private String issue, time, date, month;
    
    
    private String place, place_column;
    private IssueDetailNames_RecyclerModel priestModel;
    private BasicMember_Model issuedByModel;


    Fragment_ImportantDates_RecyclerModel(int issueId, String issue,
                                          String time,
                                          String date, String month,
                                          String place,String place_column,
                                          IssueDetailNames_RecyclerModel priestModel,
                                          BasicMember_Model issuedByModel) {
        this.issueId = issueId;
        this.issue = issue;
        this.time = time;
        this.date = date;
        this.month = month;
        this.place = place;
        this.place_column = place_column;
        this.priestModel = priestModel;
        this.issuedByModel = issuedByModel;
    }

    public int getIssueId() {
        return issueId;
    }

    public String getIssue() {
        return issue;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getMonth() {
        return month;
    }

    public String getPlace() {
        return place;
    }

    public IssueDetailNames_RecyclerModel getPriestModel() {
        return priestModel;
    }

    public BasicMember_Model getIssuedByModel() {
        return issuedByModel;
    }

    public String getPlace_column() {
        return place_column;
    }
}
