/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Vendor;

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

import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Dashboard.NavFragment.Notification_Model;
import com.meimodev.sitouhandler.Dashboard.Services.ActivityOrderDetail;
import com.meimodev.sitouhandler.Issue.OnRecyclerItemOperationListener;
import com.meimodev.sitouhandler.IssueDetail.IssueDetail;
import com.meimodev.sitouhandler.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import static com.meimodev.sitouhandler.Constant.AUTHORIZATION_STATUS_CODE_ACCEPTED;
import static com.meimodev.sitouhandler.Constant.AUTHORIZATION_STATUS_CODE_REJECTED;
import static com.meimodev.sitouhandler.Constant.AUTHORIZATION_STATUS_CODE_UNCONFIRMED;
import static com.meimodev.sitouhandler.Constant.displayDialog;

public class VendorHome_RecyclerAdapter extends RecyclerView.Adapter<VendorHome_RecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<VendorHome.HelperModel> items;


    public VendorHome_RecyclerAdapter(Context context, ArrayList<VendorHome.HelperModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.resource_list_item_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder h, int position) {
        VendorHome.HelperModel model = items.get(position);
        h.tvType.setText(model.getType());

        if (model.getStatus().contentEquals(Constant.AUTHORIZATION_STATUS_ACCEPTED)) {
            h.tvStatus.setText(String.format("Diantar %s", model.getDeliveryTime()));
            h.tvStatus.setTextColor(context.getResources().getColor(R.color.accept));
        }
        else {
            if (model.getStatus().contentEquals(Constant.AUTHORIZATION_STATUS_UNCONFIRMED)) {
                h.tvStatus.setText("Menunggu konfirmasi penyedia");
            }
            else {
                h.tvStatus.setText("Pesanan ditolak penyedia");
                h.tvStatus.setTextColor(context.getResources().getColor(R.color.reject));
            }
        }

        if (StringUtils.isNotEmpty(model.getFinishDate())) {
            h.tvStatus.setText(String.format("Pesanan selesai, %s", model.getFinishDate()));
            h.tvStatus.setTextColor(context.getResources().getColor( R.color.colorPrimary));
        }

        h.tvOrderDate.setText(model.getOrderTime());
        h.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivityOrderDetail.class);
            intent.putExtra("ID", model.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvType, tvStatus, tvOrderDate;
        CardView cardView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.recyclerItem_type);
            tvStatus = itemView.findViewById(R.id.recyclerItem_status);
            tvOrderDate = itemView.findViewById(R.id.recyclerItem_date);
            cardView = itemView.findViewById(R.id.recyclerItem_card);
        }
    }
}
