/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_News;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.meimodev.sitouhandler.Dashboard.Services.Groceries.ActivityServiceGroceries;
import com.meimodev.sitouhandler.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class Fragment_User_Home_Vendors_RecyclerAdapter extends RecyclerView.Adapter<Fragment_User_Home_Vendors_RecyclerAdapter.MyViewHolder> {

    private ArrayList<Fragment_User_Home_Vendors_RecyclerModel> items;
    private Context context;

    Fragment_User_Home_Vendors_RecyclerAdapter(ArrayList<Fragment_User_Home_Vendors_RecyclerModel> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_item_vendors_small, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Fragment_User_Home_Vendors_RecyclerModel model = items.get(position);

        Picasso
                .get()
                .load(model.getImageUrl())
                .placeholder(R.drawable.ic_logo_tou_system)
                .transform(new CropCircleTransformation())
                .into(holder.image);

        holder.tvTitle.setText(model.getName());
        holder.tvHour.setText(String.format("%s - %s", model.getOpenHour(), model.getCloseHour()));

        if (model.isOpen()){
            holder.ivIcon1.setImageResource(R.drawable.ic_open_exit_door_main);
        }


        holder.layout.setOnClickListener(v -> {
            Intent i = new Intent(context, ActivityServiceGroceries.class);
            i.putExtra(ActivityServiceGroceries.KEY_STRAIGHT_TO_VENDOR_DETAIL, ActivityServiceGroceries.KEY_STRAIGHT_TO_VENDOR_DETAIL);
            i.putExtra("VENDOR_ID", model.getId());
            i.putExtra("VENDOR_NAME",model.getName());
            context.startActivity(i);
        });

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ViewGroup layout;
        ImageView image, ivIcon1, ivIcon2;
        TextView tvTitle;
        TextView tvHour;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.recyclerItem_layout);
            image = itemView.findViewById(R.id.recyclerItem_image);
            ivIcon1 = itemView.findViewById(R.id.recyclerItem_icon1);
            ivIcon2 = itemView.findViewById(R.id.recyclerItem_icon2);
            tvTitle = itemView.findViewById(R.id.recyclerItem_title);
            tvHour = itemView.findViewById(R.id.recyclerItem_hour);
        }
    }
}
