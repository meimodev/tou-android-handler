/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_Order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Dashboard.Services.ActivityOrderDetail;
import com.meimodev.sitouhandler.Dashboard.Services.Groceries.ActivityServiceGroceries;
import com.meimodev.sitouhandler.Issue.OnRecyclerItemOperationListener;
import com.meimodev.sitouhandler.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class Fragment_User_Home_Order_RecyclerAdapter extends
        RecyclerView.Adapter<Fragment_User_Home_Order_RecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Fragment_User_Home_Order.RecyclerViewHelperModel> items;

    Fragment_User_Home_Order_RecyclerAdapter(Context context, ArrayList<Fragment_User_Home_Order.RecyclerViewHelperModel> items) {
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
        Fragment_User_Home_Order.RecyclerViewHelperModel model = items.get(position);

//        String type = model.getType();
//        if (type.contentEquals(Constant.PRODUCT_TYPE_GROCERIES)) {
//            type = "Bahan Makanan";
//        }
//        else if (type.contentEquals(Constant.PRODUCT_TYPE_ELECTRONICS)) {
//            type = "Servis Elektronik";
//        }
//        else if (type.contentEquals(Constant.PRODUCT_TYPE_COOKIES)) {
//            type = "Kukis";
//        }
//        else if (type.contentEquals("SSS")) {
//            type = Constant.PRODUCT_TYPE_SSS;
//        }
//        h.tvType.setText(type);

        String deliveryStatus =
                Integer.parseInt(model.getTransportFee()) != 0 ?
                        "Pesan Antar" : "Pesan Ambil";

        String t = "Order ID " + model.getId() + ", " + deliveryStatus;
        h.tvType.setText(t);

        if (model.getStatus().contentEquals(Constant.AUTHORIZATION_STATUS_UNCONFIRMED)) {
            h.tvStatus.setText(String.format("Menunggu konfirmasi %s", model.getVendorName()));
        }
        else if (model.getStatus().contentEquals(Constant.AUTHORIZATION_STATUS_ACCEPTED)) {

            h.tvStatus.setTextColor(context.getResources().getColor(R.color.accept));

            if (deliveryStatus.contentEquals("Pesan Antar")) {
                h.tvStatus.setText(String.format("Diantarkan %s", model.getDeliveryTime()));
            }
            else {
                h.tvStatus.setText(String.format("Ambil Pesanan di %s", model.getVendorName()));
            }


        }
        else if (model.getStatus().contentEquals(Constant.AUTHORIZATION_STATUS_REJECTED)) {
            h.tvStatus.setTextColor(context.getResources().getColor(R.color.reject));
            h.tvStatus.setText("Pesanan dibatalkan penyedia");
        } else if (model.getStatus().contentEquals("PAID")){
            h.tvStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            h.tvStatus.setText("Pesanan Selesai");

            if (StringUtils.isNotEmpty( model.getFinishDate())){
                h.tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_check_24px, null),
                        null, null, null
                );
            }

        }

        h.tvDate.setText(model.getOrderDate());

        h.cvMain.setOnClickListener(v -> {
            Intent i = new Intent(context, ActivityOrderDetail.class);
            i.putExtra("ID", model.getId());
            context.startActivity(i);
        });

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvType, tvStatus, tvDate;
        CardView cvMain;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvType = itemView.findViewById(R.id.recyclerItem_type);
            tvStatus = itemView.findViewById(R.id.recyclerItem_status);
            tvDate = itemView.findViewById(R.id.recyclerItem_date);
            cvMain = itemView.findViewById(R.id.recyclerItem_card);

        }

    }

}
