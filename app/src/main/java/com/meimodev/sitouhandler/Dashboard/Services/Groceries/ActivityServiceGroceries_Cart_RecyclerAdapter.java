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

import com.github.squti.guru.Guru;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Issue.OnRecyclerItemOperationListener;
import com.meimodev.sitouhandler.R;

import java.util.ArrayList;

public class ActivityServiceGroceries_Cart_RecyclerAdapter extends RecyclerView.Adapter<ActivityServiceGroceries_Cart_RecyclerAdapter.MyViewHolder> {
    private static final String TAG = "RecyclerAdapter_Cart";
    private Context context;
    private ArrayList<ActivityServiceGroceries.HelperModelCart> items;

    private OnRecyclerItemOperationListener.ItemClickListener clickListener;

    private boolean isChurchMember;

    ActivityServiceGroceries_Cart_RecyclerAdapter(
            Context context,
            ArrayList<ActivityServiceGroceries.HelperModelCart> items,
            OnRecyclerItemOperationListener.ItemClickListener clickListener
    ) {
        this.clickListener = clickListener;
        this.context = context;
        this.items = items;
        isChurchMember = Guru.getInt(Constant.KEY_CHURCH_ID, -99) > 0;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.resource_list_item_groceries_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ActivityServiceGroceries.HelperModelCart model = items.get(position);

        holder.tvName.setText(model.getName());
        String quantity = model.getQuantity() + " " + model.getUnit();
        holder.tvQuantity.setText(quantity);
        holder.tvTotalPrice.setText(Constant.convertNumberToCurrency(model.getQuantity() * model.getPricePerUnit()));

        setLowestQuantityToOne(model, holder);


        Bundle data = new Bundle();

        data.putInt("ID", model.getId());
        holder.btnDel.setOnClickListener(v -> {
            items.remove(model);
            notifyItemRemoved(holder.getLayoutPosition());
//            Log.e(TAG, "onBindViewHolder: getAdapterPosition() " + holder.getAdapterPosition() + " getLayoutPosition() " + holder.getLayoutPosition() + " position " + position);

            data.putString("OPERATION", "DEL");
            clickListener.itemClick(data);
        });

        holder.btnAdd.setOnClickListener(v -> {
            data.putString("OPERATION", "ADD");

            int newQuantity = model.getQuantity() + 1;
            if (!isChurchMember && newQuantity > Constant.VALUE_MAX_NON_MEMBER_PRODUCT_UNIT) {
                Constant.displayDialog(
                        context,
                        "Perhatian !",
                        "Dikarenakan akun anda tidak terdaftar sebagai anggota dari Gereja mitra TOU, " +
                                "Pemesanan Produk ini (" + model.getName() + ") tidak bisa melebihi " +
                                Constant.VALUE_MAX_NON_MEMBER_PRODUCT_UNIT +
                                " " + model.getUnit() +
                                System.lineSeparator() +
                                System.lineSeparator() +
                                "untuk menghilangkan batasan ini, silahkan daftarkan akun sebagai anggota gereja anda, jika Gereja anda merupakan mitra TOU"
                        ,
                        (dialog, which) -> {

                        }
                );
                return;
            }
            model.setQuantity(newQuantity);
            String q = model.getQuantity() + " " + model.getUnit();
            holder.tvQuantity.setText(q);
            holder.tvTotalPrice.setText(Constant.convertNumberToCurrency(model.getQuantity() * model.getPricePerUnit()));

            clickListener.itemClick(data);

            setLowestQuantityToOne(model, holder);
        });

        holder.btnSub.setOnClickListener(v -> {
            data.putString("OPERATION", "SUB");

            int newQuantity = model.getQuantity() - 1;
            model.setQuantity(newQuantity);
            String q = model.getQuantity() + " " + model.getUnit();
            holder.tvQuantity.setText(q);
            holder.tvTotalPrice.setText(Constant.convertNumberToCurrency(model.getQuantity() * model.getPricePerUnit()));

            clickListener.itemClick(data);

            setLowestQuantityToOne(model, holder);
        });

    }

    private void setLowestQuantityToOne
            (ActivityServiceGroceries.HelperModelCart model, MyViewHolder holder) {
        if (model.getQuantity() <= 1) {
            holder.btnSub.setEnabled(false);
            holder.btnSub.setCardBackgroundColor(context.getResources().getColor(R.color.disabled_background));
        }
        else {
            holder.btnSub.setEnabled(true);
            holder.btnSub.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvQuantity, tvTotalPrice;
        CardView btnDel, btnAdd, btnSub;


        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.recyclerItem_name);
            tvQuantity = itemView.findViewById(R.id.recyclerItem_quantity);
            tvTotalPrice = itemView.findViewById(R.id.recyclerItem_price);
            btnAdd = itemView.findViewById(R.id.recyclerItem_add);
            btnDel = itemView.findViewById(R.id.recyclerItem_delete);
            btnSub = itemView.findViewById(R.id.recyclerItem_sub);

        }

    }

    public ArrayList<ActivityServiceGroceries.HelperModelCart> getItems() {
        return items;
    }
}
