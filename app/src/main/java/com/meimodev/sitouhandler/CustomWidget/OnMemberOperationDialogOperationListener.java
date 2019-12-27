package com.meimodev.sitouhandler.CustomWidget;

import android.os.Bundle;

import androidx.annotation.Nullable;

public interface OnMemberOperationDialogOperationListener {

    interface AddMemberListener {
        void addMember(Bundle memberData);
    }
    interface EditMemberListener {
        void editMember(Bundle memberData);
    }
    interface DeleteMemberListener {
        void deleteMember(Bundle memberData);
    }

}
