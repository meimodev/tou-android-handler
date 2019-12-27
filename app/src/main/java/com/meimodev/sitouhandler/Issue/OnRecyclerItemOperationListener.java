package com.meimodev.sitouhandler.Issue;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public interface OnRecyclerItemOperationListener {

    interface DeleteListener {
        void delete();
    }

    interface DeleteDetailsListener {
        void deleteDetails(int position);
    }

    interface CreateItemListener {
        void createItem(int position);
    }

    interface AcceptItemListener {
        void acceptItem (@Nullable Bundle data);

    }

    interface RejectItemListener{
        void rejectItem (@Nullable Bundle data);
    }

    interface ItemClickListener{
        void itemClick (@Nullable Bundle data);
    }

}
