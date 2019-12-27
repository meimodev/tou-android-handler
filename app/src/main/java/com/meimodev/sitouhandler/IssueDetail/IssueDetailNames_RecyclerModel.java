package com.meimodev.sitouhandler.IssueDetail;

import com.meimodev.sitouhandler.Models.BasicMember_Model;

public class IssueDetailNames_RecyclerModel {
    private String category;
    private boolean categoryHead;
    private boolean unregistered;

    private BasicMember_Model basicMemberModels;

    public IssueDetailNames_RecyclerModel(
            BasicMember_Model basicMemberModels,
            String category,
            boolean unregistered) {

        this.category = category;
        this.unregistered = unregistered;
        this.basicMemberModels = basicMemberModels;
        categoryHead = !category.isEmpty();
    }




    public String getCategory() {
        return category;
    }

    public boolean isCategoryHead() {
        return categoryHead;
    }

    public boolean isUnregistered() {
        return unregistered;
    }

    public BasicMember_Model getBasicMemberModels() {
        return basicMemberModels;
    }
}
