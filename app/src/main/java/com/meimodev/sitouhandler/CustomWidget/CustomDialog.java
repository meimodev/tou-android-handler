/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.CustomWidget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.DialogCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.meimodev.sitouhandler.R;

public class CustomDialog extends DialogFragment {

    private static final String TAG = "CustomDialog";

    private Context context;


    private Button btnYES;
    private Button btnNo;

    private String title;
    private String content;
    private boolean cancelable = true;
    private DialogInterface.OnClickListener positiveListener;
    private DialogInterface.OnCancelListener negativeListener;
    private DialogInterface.OnDismissListener dismissListener;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOnClickPositive(DialogInterface.OnClickListener positiveListener) {
        this.positiveListener = positiveListener;
    }

    public void setOnClickNegative(DialogInterface.OnCancelListener negativeListener) {
        this.negativeListener = negativeListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public void setCancelable(Boolean cancelable) {
        this.cancelable = cancelable == null ? true : cancelable;
    }

    public void show(Context context) {
        super.show(((FragmentActivity) context).getSupportFragmentManager(), null);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View view = LayoutInflater.from(context).inflate(R.layout.resource_default_dialog, getActivity().findViewById(android.R.id.content), false);
        TextView tvTitle = view.findViewById(R.id.textView_dialogTitle);
        tvTitle.setVisibility(View.GONE);
        TextView tvContent = view.findViewById(R.id.textView_dialogContent);
        tvContent.setVisibility(View.GONE);

        if (title != null) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);

            if (title.contains("!")) {
                tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_warning), null, null, null);
                tvTitle.setCompoundDrawablePadding(8);
            } else if (title.contains("OK")){
                tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_check_24px), null, null, null);
                tvTitle.setCompoundDrawablePadding(8);
            }

        }
        if (content != null) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(content);
        }

        btnYES = view.findViewById(R.id.button_dialogAccept);
        btnYES.setVisibility(View.GONE);
        btnNo = view.findViewById(R.id.button_dialogDeny);
        btnNo.setVisibility(View.GONE);

        builder.setCancelable(cancelable);

        if (positiveListener != null) {
            btnYES.setVisibility(View.VISIBLE);
            btnYES.setOnClickListener(v -> {
                positiveListener.onClick(getDialog(), 0);
                getDialog().dismiss();
            });
        }

        if (negativeListener != null) {
            btnNo.setVisibility(View.VISIBLE);
            btnNo.setOnClickListener(v -> {
                negativeListener.onCancel(getDialog());
                getDialog().dismiss();
            });
        }

        if (dismissListener != null) {
            builder.setOnDismissListener(dismissListener);
        }


        builder.setView(view);

        return builder.create();
    }


}
