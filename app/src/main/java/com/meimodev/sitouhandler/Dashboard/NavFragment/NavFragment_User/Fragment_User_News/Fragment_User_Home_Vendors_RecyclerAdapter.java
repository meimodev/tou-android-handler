/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_News;

import android.content.Context;
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

        holder.layout.setOnClickListener(v -> {
            Toast.makeText(context, String.valueOf(model.getId()), Toast.LENGTH_SHORT).show();
        });

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ViewGroup layout;
        ImageView image;
        TextView tvTitle;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.recyclerItem_layout);
            image = itemView.findViewById(R.id.recyclerItem_image);
            tvTitle = itemView.findViewById(R.id.recyclerItem_title);
        }
    }
}
