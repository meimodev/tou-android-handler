/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.Services.Groceries;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Issue.OnRecyclerItemOperationListener;
import com.meimodev.sitouhandler.R;

import java.util.ArrayList;

public class ActivityServiceGroceries_Product_RecyclerAdapter extends RecyclerView.Adapter<ActivityServiceGroceries_Product_RecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ActivityServiceGroceries.HelperModelProduct> items;

    private OnRecyclerItemOperationListener.ItemClickListener clickListener;

    ActivityServiceGroceries_Product_RecyclerAdapter(
            Context context,
            ArrayList<ActivityServiceGroceries.HelperModelProduct> items,
            OnRecyclerItemOperationListener.ItemClickListener clickListener
    ) {
        this.clickListener = clickListener;
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.resource_list_item_groceries_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ActivityServiceGroceries.HelperModelProduct model = items.get(position);

        holder.tvName.setText(model.getName());
        String price = Constant.convertNumberToCurrency(model.getPrice()) + " / " + model.getUnit();
        holder.tvPrice.setText(price);

        holder.cvAdd.setOnClickListener(v -> {
            Bundle data = new Bundle();
            data.putInt("ID", model.getId());
            data.putString("NAME", model.getName());
            data.putInt("PRICE", model.getPrice());
            data.putString("UNIT", model.getUnit());
            data.putInt("POS", position);
            clickListener.itemClick(data);
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPrice;
        CardView cvAdd;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.recyclerItem_name);
            tvPrice = itemView.findViewById(R.id.recyclerItem_price);
            cvAdd = itemView.findViewById(R.id.recyclerItem_layout_add);

        }

    }
}
