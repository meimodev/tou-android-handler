/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.meimodev.sitouhandler.IssueDetail.IssueDetail;
import com.meimodev.sitouhandler.Issue.OnRecyclerItemOperationListener;
import com.meimodev.sitouhandler.R;

import java.util.ArrayList;

import static com.meimodev.sitouhandler.Constant.*;

public class Notification_RecyclerAdapter extends RecyclerView.Adapter<Notification_RecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Notification_Model> items;

    private OnRecyclerItemOperationListener.AcceptItemListener acceptItemListener;
    private OnRecyclerItemOperationListener.RejectItemListener rejectItemListener;

    public Notification_RecyclerAdapter(Context context,
                                        ArrayList<Notification_Model> items,
                                        OnRecyclerItemOperationListener.AcceptItemListener acceptItemListener,
                                        OnRecyclerItemOperationListener.RejectItemListener rejectItemListener
    ) {
        this.context = context;
        this.items = items;
        this.acceptItemListener = acceptItemListener;
        this.rejectItemListener = rejectItemListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_item_notification_home, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Notification_Model model = items.get(position);
        holder.setIsRecyclable(false);

        holder.cvItem.setOnClickListener(view -> {
            Intent intent = new Intent(context, IssueDetail.class);
            intent.putExtra("ISSUE_ID", model.getIssueId());
            intent.putExtra("AUTHORIZATION_STATUS_CODE", model.getAuthStatusCode());
            intent.putExtra("AUTHORIZATION_ID", model.getAuthId());
            context.startActivity(intent);
        });

        holder.tvIssue.setText(model.getKeyIssue());
        if (holder.tvIssue.getEllipsize() != null)
            holder.tvIssue.setSelected(true);

        holder.tvName.setText(model.getIssuedByMemberName());
        holder.tvColumn.setText(model.getIssuedByMemberColumn());
        holder.tvIssuedDate.setText(model.getRelatedDate());

        int color;

        switch (model.getAuthStatusCode()) {

            case AUTHORIZATION_STATUS_CODE_ACCEPTED:
                holder.tvConfirmationStatus.setText("DITERIMA");
                 color = context.getResources().getColor(R.color.accept);
                holder.tvConfirmationStatus.setTextColor(color);
                holder.tvConfirmationStatus.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_check_24px), null, null, null);
                holder.ivStrip.setBackgroundColor(color);
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnDeny.setVisibility(View.GONE);
                break;
            case AUTHORIZATION_STATUS_CODE_REJECTED:
                holder.tvConfirmationStatus.setText("DITOLAK");
                 color = context.getResources().getColor(R.color.reject);
                holder.tvConfirmationStatus.setTextColor(color);
                holder.tvConfirmationStatus.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_close_24px), null, null, null);
                holder.ivStrip.setBackgroundColor(color);
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnDeny.setVisibility(View.GONE);
                break;
            case AUTHORIZATION_STATUS_CODE_UNCONFIRMED:
                holder.tvConfirmationStatus.setText("DIAJUKAN");
                color = context.getResources().getColor(R.color.colorAccent);
                holder.tvConfirmationStatus.setTextColor(color);
                holder.tvConfirmationStatus.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_priority_high_24px), null, null, null);
                holder.ivStrip.setBackgroundColor(color);
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnDeny.setVisibility(View.VISIBLE);
        }

        if (acceptItemListener == null || rejectItemListener == null){
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnDeny.setVisibility(View.GONE);
            holder.cvItem.setOnClickListener(view -> {
                Intent intent = new Intent(context, IssueDetail.class);
                intent.putExtra("ISSUE_ID", model.getIssueId());
                intent.putExtra("AUTHORIZATION_STATUS_CODE", model.getAuthStatusCode());
                intent.putExtra("AUTHORIZATION_ID", model.getAuthId());
                intent.putExtra(IssueDetail.OPERATION_VIEW_ONLY, true);
                context.startActivity(intent);
            });
        }

        View.OnClickListener btnListener = view -> {
            String message, title;
            DialogInterface.OnClickListener listener ;

            Bundle data = new Bundle();
            data.putString("name", model.getIssuedByMemberName());
            data.putString("column", model.getIssuedByMemberColumn());
            data.putInt("id", model.getAuthId());

            if (holder.btnAccept.equals(view)) {
                title = "Konfirmasi Penerimaan!";
                message = "Anda akan menerima pengajuan atas nama\n" + model.getIssuedByMemberName() + ",\n" + model.getIssuedByMemberColumn() + ".\nSilahkan sentuh tombol 'OK' untuk konfirmasi bahwa pengajuan ini akan diterima";
                listener = (dialog, which) -> {
                    data.putInt("status", AUTHORIZATION_STATUS_CODE_ACCEPTED);
                    if (acceptItemListener != null) acceptItemListener.acceptItem(data);
                };
            } else {
                title = "Konfirmasi Penolakan!";
                message = "Anda akan menolak pengajuan atas nama\n" + model.getIssuedByMemberName() + ",\n" + model.getIssuedByMemberColumn() + ".\nSilahkan sentuh tombol 'OK' untuk konfirmasi bahwa pengajuan ini akan ditolak";
                listener = (dialog, which) -> {
                    data.putInt("status", AUTHORIZATION_STATUS_CODE_REJECTED);
                    if (rejectItemListener != null) rejectItemListener.rejectItem(data);
                };
            }
            displayDialog(
                    context, title, message, listener
            );
        };
        holder.btnAccept.setOnClickListener(btnListener);
        holder.btnDeny.setOnClickListener(btnListener);

        if (position == items.size() - 1) {
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 25, 0, 220);
            holder.cvItem.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvIssue, tvName, tvColumn, tvIssuedDate, tvConfirmationStatus;
        CardView btnAccept, btnDeny;
        CardView cvItem;
        ImageView ivStrip;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIssue = itemView.findViewById(R.id.recyclerItem_issueKey);
            tvName = itemView.findViewById(R.id.recyclerItem_name);
            tvColumn = itemView.findViewById(R.id.recyclerItem_column);
            tvIssuedDate = itemView.findViewById(R.id.recyclerItem_date);
            tvConfirmationStatus = itemView.findViewById(R.id.recyclerItem_status);
            btnAccept = itemView.findViewById(R.id.recyclerItem_accept);
            btnDeny = itemView.findViewById(R.id.recyclerItem_reject);
            cvItem = itemView.findViewById(R.id.recyclerItem_itemHolder);
            ivStrip = itemView.findViewById(R.id.reacyclerItem_strip);
        }
    }
}
